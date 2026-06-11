package manuel.monstersmod.npcs

import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World


/*

DefaultAttributeContainer.Builder es un objeto que contiene las stats del NPC, es lo que retornamos con la funcion
createAttributes().



 */

class NpcEntity(entityType: EntityType<out NpcEntity>, world: World)
    : PathAwareEntity(entityType, world) {

    companion object {
        fun createAttributes(): DefaultAttributeContainer.Builder {
            return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0) // Añadimos vida
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25) // Añadimos velocidad de movimiento
        }
    }

    override fun isCustomNameVisible() = true

    /*

    initGoals() es el metodo que define el comportamiento de la IA, lo sobreescribimos para añadir nuestra propia logica.

    goalSelector es una lista de objetivos de IA que heredas de PathAwareEntity. Cada goal es un comportamiento.
    (Mirar, caminar, atacar, huir...)

    Con add() añadimos un comportamiento a la lista. 1 es la prioridad, cuanto mas bajo el numero más prioridad.
    LookAtEntityGoal() es un goal hecho por Minecraft que hace que la entidad gire hace otra entidad cercana.
    this, en este contexto es el NPC que ejecuta el goal, PlayerEntity::class.java el tipo de entidad que tiene que mirar,
    en este caso jugadores y 6f la distancia maxima en bloques que mira.

     */

    override fun initGoals() {
        goalSelector.add(1, LookAtEntityGoal(this, PlayerEntity::class.java, 6f))
    }
}