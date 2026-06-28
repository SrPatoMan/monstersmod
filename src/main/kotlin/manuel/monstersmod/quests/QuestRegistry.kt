package manuel.monstersmod.quests

import manuel.monstersmod.items.MisItems
import net.minecraft.entity.EntityType

/*

Registro central de quests, igual de patron que MisItems/MisTabs: cada quest se declara como un val y se
registra con registrar(). Para añadir una quest nueva solo hace falta declarar un val mas aqui, no hay que
tocar nada del resto del sistema (QuestState/QuestManager funcionan con cualquier Quest por su id).

*/
object QuestRegistry {

    private val quests = mutableMapOf<String, Quest>()

    private fun registrar(quest: Quest): Quest {
        quests[quest.id] = quest
        return quest
    }

    fun obtener(id: String): Quest? = quests[id]

    fun todas(): Collection<Quest> = quests.values

    // Quest de ejemplo, ligada al dialogo de AlexElCapo (ver npcs/AlexElCapo.kt).
    val FINAL_FANTASY_PERDIDO: Quest = registrar(
        Quest(
            id = "final_fantasy_perdido",
            titulo = "En busca del Final Fantasy 7",
            descripcion = "Busca el Final Fantasy 7 del primo Manolo en un yate",
            objetivo = QuestObjective.CollectItem(MisItems.FINAL_FANTASY_7!!, 1),
            recompensa = QuestReward(MisItems.PESETA, 10)
        )
    )

    val FAVOR_DE_XOKAS: Quest = registrar(
        Quest(
            id = "favor_de_xokas",
            titulo = "El tronco de butter",
            descripcion = "Consigue el tronco de butter para el xokas",
            objetivo = QuestObjective.CollectItem(MisItems.BUTTER!!, 1),
            recompensa = QuestReward(MisItems.PESETA, 10)
        )
    )
}
