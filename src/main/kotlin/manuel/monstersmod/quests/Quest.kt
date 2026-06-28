package manuel.monstersmod.quests

import net.minecraft.item.Item

// Recompensa que se entrega al completar la quest. De momento solo soporta un item, se puede ampliar
// a una lista de recompensas (items, experiencia, etc.) cuando haga falta.
data class QuestReward(val item: Item? = null, val amount: Int = 1)

data class Quest(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val objetivo: QuestObjective,
    val recompensa: QuestReward? = null
)
