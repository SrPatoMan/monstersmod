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
                text = "¡Buenas! Soy cathivi... digo, el xokas, que tal tio?? Me he mudado hace poco a esta ciudad y no conozco a mucha gente la verdad. Encantado de conocerte",
                options = listOf(
                    DialogueOption("Buenas xokas, encantado ", "intro2")
                )
            ),
            "intro2" to DialogueNode(
                id = "intro2",
                text = "Oye, ya se que nos acabamos de conocer, pero me podrias ayudar con una cosilla? Necesito que me consigas un cablo HDMI para performar a maximo nivel. Me lo podrias conseguir",
                options = listOf(
                    DialogueOption("Si claro!!", null, DialogueAction.ACCEPT_QUEST),
                    DialogueOption("No, que te jodan", null, DialogueAction.DECLINE_QUEST)
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
        DialogueNetworking.sendOpenDialogue(player as ServerPlayerEntity, node) // Mandamos el nodo al cliente para que abra la pantalla de dialogo. Hacemos un cast de PlayerEntity a ServerPlayerEntity porque sendOpenDialogue lo requiere, y sabemos que en este punto estamos en el servidor

        return ActionResult.SUCCESS // Le decimos a Minecraft que la interaccion se proceso correctamente
    }
}