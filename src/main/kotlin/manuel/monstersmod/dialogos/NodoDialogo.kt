package manuel.monstersmod.dialogos

data class DialogueOption(
    val text: String,           // texto del botón, ej: "Sí, acepto la misión"
    val nextNodeId: String?,    // a qué nodo lleva (null = cierra el diálogo)
    val action: DialogueAction? = null, // acción opcional al elegir esta opción
    val questId: String? = null // id de la quest (en QuestRegistry) sobre la que actúa la action, si aplica
)

data class DialogueNode(
    val id: String,             // identificador único del nodo"
    val text: String,           // lo que dice el NPC
    val options: List<DialogueOption>, // Lista de opciones del jugador
    val sound: String? = null  // nombre del sonido custom a reproducir al abrir este nodo (sin namespace), ej: "xokas_intro". Null = sin sonido.
)

// Enum con las acciones que se puedan llevar a cabo por parte del jugador
enum class DialogueAction {
    ACCEPT_QUEST,
    DECLINE_QUEST,
    COMPLETE_QUEST,
    GIVE_ITEM
}