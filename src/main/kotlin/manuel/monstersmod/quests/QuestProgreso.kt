package manuel.monstersmod.quests

// Progreso de UN jugador en UNA quest concreta. Vive dentro de QuestState, que es lo que se persiste a NBT.
data class QuestProgreso(
    val questId: String,
    var cantidadActual: Int = 0,
    var completada: Boolean = false,
    var recompensaEntregada: Boolean = false
)
