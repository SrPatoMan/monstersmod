package manuel.monstersmod.npcs

import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World

class Maduro(type: EntityType<out Maduro>, world: World) : NpcEntity(type, world) {

    // Sin wander, se queda quieto y solo mira al jugador.
    override fun initGoals() {
        goalSelector.add(1, LookAtEntityGoal(this, PlayerEntity::class.java, 6f))
    }
}
