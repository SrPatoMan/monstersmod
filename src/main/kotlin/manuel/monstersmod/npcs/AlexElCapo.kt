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
import net.minecraft.server.network.ServerPlayerEntity

class AlexElCapo(type: EntityType<out XokasNpc>, world: World) : NpcEntity(type, world) {

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
                    DialogueOption("Por supuesto tío!!", "questFinalFantasy", DialogueAction.ACCEPT_QUEST),
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
                text = "Debe ser que se lo robaron o algo, el dice que se le debió de caer, pero no me sorprendería que se lo robaran, había mucho facha de ultraderecha",
                options = listOf(
                    DialogueOption("Continuar", null)
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
        val node = Dialogue.nodes[Dialogue.START_NODE]!! // Obtenemos el nodo inicial del arbol de dialogo del Xokas accediendo al map por su clave (START_NODE = "intro"). Los !! indican que estamos seguros de que no es null, si lo fuera lanzaria una excepcion
        DialogueNetworking.sendOpenDialogue(player as ServerPlayerEntity, "alexelcapo", node) // Mandamos el nodo al cliente para que abra la pantalla de dialogo. Hacemos un cast de PlayerEntity a ServerPlayerEntity porque sendOpenDialogue lo requiere, y sabemos que en este punto estamos en el servidor

        return ActionResult.SUCCESS // Le decimos a Minecraft que la interaccion se proceso correctamente. Devolvemos el resultado de la interacción con el NPC
    }
}