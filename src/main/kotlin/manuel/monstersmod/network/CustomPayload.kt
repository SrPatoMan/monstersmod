package manuel.monstersmod.network

import manuel.monstersmod.dialogos.DialogueNode
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import manuel.monstersmod.MonstersMod.MOD_ID
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

/*

Porque de este archivo:

En Minecraft, el cliente y el servidor son dos programas separados que se comunican por red, incluso en singleplayer (se levanta un servidor interno).
Toda la lógica de las quests, los dialogos con los NPCs, etc... se registra en el servidor. Ahora bien, el servidor tiene que enviar los datos al cliente
y el cliente por su cuenta tiene que dibujarlos. Este archivo se encarga de empaquetar y enviar la logica que ocurre en el servidor al cliente para que este la pueda dibujar.
 */


object DialogueNetworking {

    // Definimos dos identificadores, igual que los de los registros, Identifier(MOD_ID, ID VARIABLE)
    val OPEN_DIALOGUE: Identifier = Identifier(MOD_ID, "open_dialogue") // Identificador para cuando el servidor manda al cliente
    val DIALOGUE_CHOICE: Identifier = Identifier(MOD_ID, "dialogue_choice") // Identificador para cuando el cliente manda al servidor

    // Esta funcion la llamará el servidor cuando quiera mostrar un nodo de diálogo al jugador. ServerPlayerEntity es una subclase de la clase PlayerEntity con funciones especificas del servidor agregadas.
    fun sendOpenDialogue(player: ServerPlayerEntity, npcId: String, node: DialogueNode) {
        val buf: PacketByteBuf = PacketByteBufs.create()

        buf.writeString(npcId)
        buf.writeString(node.id)
        buf.writeString(node.text)
        buf.writeString(node.sound ?: "") // String vacío = el nodo no tiene sonido custom

        buf.writeInt(node.options.size)
        node.options.forEach { option ->
            buf.writeString(option.text)
        }

        ServerPlayNetworking.send(player, OPEN_DIALOGUE, buf)
    }

    // Funcion que llama el cliente cuando el jugador pulsa un botón. Le pasamos el id del nodo actual y el indice del boton que pulsó el jugador.
    fun sendDialogueChoice(npcId: String, nodeId: String, optionIndex: Int) {
        val buf = PacketByteBufs.create() // Creamos un buffer vacío
        buf.writeString(npcId) // Mandamos el ID del NPC al buffer.
        buf.writeString(nodeId) // Escribimos el id del nodo
        buf.writeInt(optionIndex) // Escribimos el indice del botón
        ClientPlayNetworking.send(DIALOGUE_CHOICE, buf) // Enviamos el buffer al servidor, le pasamos el id del canal.
    }

}