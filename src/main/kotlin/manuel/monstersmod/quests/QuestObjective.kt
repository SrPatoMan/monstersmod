package manuel.monstersmod.quests

import net.minecraft.entity.EntityType
import net.minecraft.item.Item

/*

Cada subtipo representa una forma distinta de "objetivo" de quest. Al ser un sealed class, el "when"
que las consume (en QuestManager) es exhaustivo: si añades un nuevo tipo de objetivo, el compilador
te obliga a gestionarlo en todos los sitios donde se usa.

*/
sealed class QuestObjective {

    abstract val requiredAmount: Int

    // Objetivo de matar X cantidad de una entidad concreta. El progreso se actualiza en vivo
    // mediante el listener de ServerLivingEntityEvents.AFTER_DEATH (ver QuestManager.registrarMuerte).
    data class KillEntity(val entityType: EntityType<*>, override val requiredAmount: Int) : QuestObjective()

    // Objetivo de poseer X cantidad de un item en el inventario. A diferencia de KillEntity, el progreso
    // no se guarda incrementalmente: se comprueba contando el inventario en el momento de entregar la quest.
    data class CollectItem(val item: Item, override val requiredAmount: Int) : QuestObjective()

    // Objetivo que se cumple simplemente al hablar con un NPC concreto (por ejemplo, quests de "ve y habla con X").
    data class TalkToNpc(val npcId: String) : QuestObjective() {
        override val requiredAmount: Int = 1
    }
}
