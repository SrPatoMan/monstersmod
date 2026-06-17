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
    fun sendOpenDialogue(player: ServerPlayerEntity, node: DialogueNode) {
        val buf: PacketByteBuf = PacketByteBufs.create() // Creamos un buffer vacío donde meterá el servidor los datos antes de mandarlos.

        buf.writeString(node.id) // Escribimos el id del nodo en el buffer
        buf.writeString(node.text) // Escribimos el texto en el buffer

        buf.writeInt(node.options.size) // Escribimos el numero de elementos del options
        node.options.forEach { option ->
            buf.writeString(option.text)
        } // Como options es una lista de opciones, hacemos un forEach para iterarla y escribir en el buffer la propiedad text (el texto de la opcion) para cada una de las opciones.

        // Una vez esta todo escrito, enviamos el buffer al jugador
        ServerPlayNetworking.send(player, OPEN_DIALOGUE, buf) // Metodo de la API de Fabric para enviar un paquete de red del servidor a un cliente concreto. Le pasamos el jugador, el id del canal y el buffer que enviaremos.
    }

    // Funcion que llama el cliente cuando el jugador pulsa un botón. Le pasamos el id del nodo actual y el indice del boton que pulsó el jugador.
    fun sendDialogueChoice(nodeId: String, optionIndex: Int) {
        val buf = PacketByteBufs.create() // Creamos un buffer vacío
        buf.writeString(nodeId) // Escribimos el id del nodo
        buf.writeInt(optionIndex) // Escribimos el indice del botón
        ClientPlayNetworking.send(DIALOGUE_CHOICE, buf) // Enviamos el buffer al servidor, le pasamos el id del canal.
    }

}