package manuel.monstersmod.dialogos

data class DialogueOption(
    val text: String,           // texto del botón, ej: "Sí, acepto la misión"
    val nextNodeId: String?,    // a qué nodo lleva (null = cierra el diálogo)
    val action: DialogueAction? = null // acción opcional al elegir esta opción
)

data class DialogueNode(
    val id: String,             // identificador único del nodo"
    val text: String,           // lo que dice el NPC
    val options: List<DialogueOption>
)

// Enum con las acciones que se puedan llevar a cabo por parte del jugador
enum class DialogueAction {
    ACCEPT_QUEST,
    DECLINE_QUEST,
    COMPLETE_QUEST,
    GIVE_ITEM
}