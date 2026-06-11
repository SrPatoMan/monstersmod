package manuel.monstersmod.npcs

import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.ai.goal.WanderAroundFarGoal
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraft.text.Text

class XokasNpc(type: EntityType<out XokasNpc>, world: World) : NpcEntity(type, world) {

    override fun initGoals() {
        goalSelector.add(1, WanderAroundFarGoal(this, 0.5))
        goalSelector.add(2, LookAtEntityGoal(this, PlayerEntity::class.java, 6f))
    }

    /* Funcion que se ejecuta cuando el jugador hace click derecho en el NPC. pl
    player es un PlayerEntity, la clase que representa al jugador en Minecraft. hand es de tipo Hand,
    que es un enum con dos opciones, la mano derecha y la mano izquierda.

    ActionResult es un enum que le indica a Minecraft el estado de la accion. Por ejemplo, ActionResult.SUCESS,
    todo salio bien, no hagas nada mas. ActionResult.PASS, ignora esta interaccion.

    */

    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        if (player.world.isClient) return ActionResult.SUCCESS // Cuando haces click derecho el cliente se ejecuta dos veces, una en el server y otra en el servidor. Para que el mensaje no se envie dos veces, en caso de estar en el cliente nos salimos del metodo, por lo que solo se ejecuta el mensaje desde el servidor. Es un check necesario.
        player.sendMessage(Text.literal("¡Te dispararia a quemarropa si tuviera la oportunidad!"), true) // sendMessage() envia un mensaje al chat del jugador que interactuo con el NPC. Primer parametro texto del mensaje, segundo parametro si aparece en el action bar.
        return ActionResult.SUCCESS
    }
}