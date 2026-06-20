package manuel.monstersmod.npcs

import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.ai.goal.WanderAroundFarGoal
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraft.text.Text
import manuel.monstersmod.dialogos.DialogueNode
import manuel.monstersmod.dialogos.DialogueOption
import manuel.monstersmod.dialogos.DialogueAction
import manuel.monstersmod.network.DialogueNetworking
import manuel.monstersmod.quests.QuestManager
import manuel.monstersmod.quests.QuestRegistry
import net.minecraft.server.network.ServerPlayerEntity

class AlexElCapo(type: EntityType<out AlexElCapo>, world: World) : NpcEntity(type, world) {

    override fun initGoals() {
        goalSelector.add(1, WanderAroundFarGoal(this, 0.5))
        goalSelector.add(2, LookAtEntityGoal(this, PlayerEntity::class.java, 6f))
    }

    /* to crea el par clave valor para el diccionario. Creamos un map y no una lista por comodidad de busqueda.
        Guardamos el Map en la variable nodes.
    */
    companion object Dialogue {
        val nodes = mapOf(
            "intro" to DialogueNode(
                id = "intro",
                text = "Ey camarada que pasa? Te comento tío, mi primo Manolo ha perdido la edición coleccionista del Final Fantasy 7 por ahí, podrías ayudarme a recuperarla?",
                options = listOf(
                    DialogueOption(
                        "Por supuesto tío!!", "questFinalFantasy",
                        DialogueAction.ACCEPT_QUEST,
                        questId = QuestRegistry.FINAL_FANTASY_PERDIDO.id
                    ),
                    DialogueOption("Joder Final Fantasy 7 flipo colega que va colega paso", null, DialogueAction.DECLINE_QUEST)
                )
            ),
            "questFinalFantasy" to DialogueNode(
                id = "questFinalFantasy",
                text = "Perfecto colegón, pues te comento. El otro día estuvimos en el yate del novio marroquí de mi esposa y creo que se le cayó por ahí. Es un barco muy guapo, en mitad del agua ¿sabes?",
                options = listOf(
                    DialogueOption("Continuar", "questFinalFantasy2")
                )
            ),

            "questFinalFantasy2" to DialogueNode(
                id = "questFinalFantasy2",
                text = "Debe ser que se lo robaron o algo, el dice que se le debió de caer, pero no me sorprendería que se lo robaran, había mucho facha de ultraderecha.",
                options = listOf(
                    DialogueOption("Continuar", null)
                )
            ),

            // Nodo al que se llegas cuando encuentras en final fantasy.
            "entregarFinalFantasy" to DialogueNode(
                id = "entregarFinalFantasy",
                text = "Muchas gracias, ostia tío colega porfin podremos jugar Final Fantasy 7 mi primo Manolo y yo, flipo colega gracias.",
                options = listOf(
                    DialogueOption(
                        "Entregar el Final Fantasy 7", null,
                        DialogueAction.COMPLETE_QUEST, questId = QuestRegistry.FINAL_FANTASY_PERDIDO.id
                    )
                )
            ),

            // Nodo al que se llega cuando la quest ya está aceptada pero el jugador todavía no tiene las pesetas.
            "esperandoFinalFantasy" to DialogueNode(
                id = "esperandoFinalFantasy",
                text = "¿Ya tienes el Final Fantasy tío? Me estoy poniendo nervioso joder ponte a trabajar ya o mandaré al novio marroquí de mi esposa a cuckearte.",
                options = listOf(
                    DialogueOption("Sigo buscando", null)
                )
            )
        )

        const val START_NODE = "intro"
    }

    /* Funcion que se ejecuta cuando el jugador hace click derecho en el NPC. pl
    player es un PlayerEntity, la clase que representa al jugador en Minecraft. hand es de tipo Hand,
    que es un enum con dos opciones, la mano derecha y la mano izquierda.

    ActionResult es un enum que le indica a Minecraft el estado de la accion. Por ejemplo, ActionResult.SUCESS,
    todo salio bien, no hagas nada mas. ActionResult.PASS, ignora esta interaccion.

    */

    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS // Si el jugador hace clic con la mano secundaria, ignoramos la interaccion y la pasamos al siguiente handler
        if (player.world.isClient) return ActionResult.SUCCESS // Si estamos en el cliente, salimos sin hacer nada para que la logica solo se ejecute en el servidor

        val serverPlayer = player as ServerPlayerEntity
        val questId = QuestRegistry.FINAL_FANTASY_PERDIDO.id

        // Según el estado de la quest del jugador, abrimos un nodo de dialogo distinto: la intro normal,
        // un recordatorio si la tiene activa pero aún no ha cumplido el objetivo, o el nodo de entrega si ya puede cobrarla.
        val nodeId = when {
            QuestManager.listaParaEntregar(serverPlayer, questId) -> "entregarFinalFantasy"
            QuestManager.estaActiva(serverPlayer, questId) -> "esperandoFinalFantasy"
            else -> Dialogue.START_NODE
        }

        val node = Dialogue.nodes[nodeId]!!
        DialogueNetworking.sendOpenDialogue(serverPlayer, "alexelcapo", node) // Mandamos el nodo al cliente para que abra la pantalla de dialogo.

        return ActionResult.SUCCESS // Le decimos a Minecraft que la interaccion se proceso correctamente. Devolvemos el resultado de la interacción con el NPC
    }
}