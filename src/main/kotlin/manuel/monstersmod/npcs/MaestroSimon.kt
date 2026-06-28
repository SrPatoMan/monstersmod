package manuel.monstersmod.npcs

import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import manuel.monstersmod.dialogos.DialogueAction
import manuel.monstersmod.dialogos.DialogueNode
import manuel.monstersmod.dialogos.DialogueOption
import manuel.monstersmod.network.DialogueNetworking
import net.minecraft.server.network.ServerPlayerEntity

class MaestroSimon(type: EntityType<out MaestroSimon>, world: World) : NpcEntity(type, world) {

    override fun initGoals() {
        goalSelector.add(1, LookAtEntityGoal(this, PlayerEntity::class.java, 6f))
    }

    companion object Dialogue {
        val nodes = mapOf(
            "intro" to DialogueNode(
                id = "intro",
                text = "¡Bienvenido a mi tienda! Soy Maestro Simón, el mejor comerciante de estas tierras. ¿Qué deseas comprar?",
                options = listOf(
                    DialogueOption(
                        text = "Monster Blanco (20 pesetas)",
                        nextNodeId = "intro",
                        action = DialogueAction.BUY_ITEM,
                        itemId = "monsterblanco",
                        price = 20
                    ),
                    DialogueOption(
                        text = "Mango Loco (10 pesetas)",
                        nextNodeId = "intro",
                        action = DialogueAction.BUY_ITEM,
                        itemId = "mangoloco",
                        price = 10
                    ),
                    DialogueOption(
                        text = "Nada, adiós",
                        nextNodeId = null
                    )
                )
            )
        )

        const val START_NODE = "intro"
    }

    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS
        if (player.world.isClient) return ActionResult.SUCCESS

        val node = Dialogue.nodes[Dialogue.START_NODE]!!
        DialogueNetworking.sendOpenDialogue(player as ServerPlayerEntity, "maestrosimon", node)

        return ActionResult.SUCCESS
    }
}
