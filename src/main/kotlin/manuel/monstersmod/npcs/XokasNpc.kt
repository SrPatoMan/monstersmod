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

class XokasNpc(type: EntityType<out XokasNpc>, world: World) : NpcEntity(type, world) {

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
                text = "Ey, ¿que tal estas? Me llamo cativi... digo... Xokas. Bueno, ¿Supongo que me conoceras no? Quiero decir, soy el numero 1 en Twitch y esas cosas joder. ¿Que tal estas? Encantado de conocerte por cierto.",
                options = listOf(
                    DialogueOption("Buenas xokas, encantado", "intro2")
                )
            ),
            "intro2" to DialogueNode(
                id = "intro2",
                text = "La verdad que no llevo mucho tiempo en estas tierras ¿sabes?, Antes vivia en Galicia, pero me tuve que ir porque me andaban buscando unos podemitas para arrebatarme la vida... Ya sabes como están las cosas, últimamente está pasando mucho...",
                options = listOf(
                    DialogueOption("Continuar", "intro3")
                )
            ),

            "intro3" to DialogueNode(
                id = "intro3",
                text = "Pero bueno, ahora estoy por estos lares, listo para performar a high level. Oye tio, te puedo pedir un favor?",
                options = listOf(
                    DialogueOption(
                        "Claro que si tilin", null,
                        DialogueAction.ACCEPT_QUEST, questId = QuestRegistry.FAVOR_DE_XOKAS.id
                    ),
                    DialogueOption("Na bro, en esta estas solo lil dog", null, DialogueAction.DECLINE_QUEST)
                )
            ),

            "intro4" to DialogueNode(
                id = "intro4",
                text = "PENDIENTE",
                options = listOf(
                    DialogueOption("Continuar", null,)
                )
            ),

            // Nodo al que se llega (ver interactMob) cuando el jugador ya ha cumplido el objetivo de la quest.
            "entregarFavorXokas" to DialogueNode(
                id = "entregarFavorXokas",
                text = "PENDIENTE",
                options = listOf(
                    DialogueOption(
                        "Toma tu recompensa", null,
                        DialogueAction.COMPLETE_QUEST, questId = QuestRegistry.FAVOR_DE_XOKAS.id
                    )
                )
            ),

            // Nodo al que se llega cuando la quest ya esta aceptada pero el objetivo todavia no se ha cumplido.
            "esperandoFavorXokas" to DialogueNode(
                id = "esperandoFavorXokas",
                text = "PENDIENTE",
                options = listOf(
                    DialogueOption("Sigo en ello", null)
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
        val questId = QuestRegistry.FAVOR_DE_XOKAS.id

        // Según el estado de la quest del jugador, abrimos un nodo de dialogo distinto: la intro normal,
        // un recordatorio si la tiene activa pero aún no ha cumplido el objetivo, o el nodo de entrega si ya puede cobrarla.
        val nodeId = when {
            QuestManager.listaParaEntregar(serverPlayer, questId) -> "entregarFavorXokas"
            QuestManager.estaActiva(serverPlayer, questId) -> "esperandoFavorXokas"
            else -> Dialogue.START_NODE
        }

        val node = Dialogue.nodes[nodeId]!! // Obtenemos el nodo correspondiente del arbol de dialogo del Xokas accediendo al map por su clave. Los !! indican que estamos seguros de que no es null, si lo fuera lanzaria una excepcion
        DialogueNetworking.sendOpenDialogue(serverPlayer, "xokas", node) // Mandamos el nodo al cliente para que abra la pantalla de dialogo.

        return ActionResult.SUCCESS // Le decimos a Minecraft que la interaccion se proceso correctamente. Devolvemos el resultado de la interacción con el NPC
    }
}