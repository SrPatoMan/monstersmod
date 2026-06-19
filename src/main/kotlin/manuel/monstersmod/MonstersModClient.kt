package manuel.monstersmod

import manuel.monstersmod.npcs.ModEntities
import manuel.monstersmod.npcs.NpcRender
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.util.Identifier
import manuel.monstersmod.MonstersMod.MOD_ID
import manuel.monstersmod.gui.DialogueScreen
import manuel.monstersmod.network.DialogueNetworking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object MonstersModClient: ClientModInitializer {

    override fun onInitializeClient() {

        /*

        Este metodo asocia un tipo de entidad con su render. Aqui le decimos que cuando vaya a dibujar
        ese NPC que le pasamos como primer argumento, que use la clase NpcRender (segundo argumento,
        el cual es una lambda). el argumento de la lambda, ctx, es un EntityRendererFactory.Context,
        un objeto que contiene todo lo necesario para que el renderer funcione.

        */
        EntityRendererRegistry.register(ModEntities.XOKAS) {
            ctx -> NpcRender(ctx, Identifier(MOD_ID, "textures/entity/xokas.png"))
        }

        ClientPlayNetworking.registerGlobalReceiver(DialogueNetworking.OPEN_DIALOGUE) { client, handler, buf, responseSender ->
            // Leemos los strings del buffer que envia el servidor. Lo leen en el mismo orden que se escribió en el buffer.
            val nodeId = buf.readString()
            val text = buf.readString()

            // Leemos la cantidad de opciones del buffer.
            val size = buf.readInt()
            val options = (0 until size).map { buf.readString() } // Itrera entre el cero y el numero de opciones que tenemos y para cada iteracion lee el string

            // client es la instancia de MinecraftClient, el objeto principal del juego en el lado del cliente. Lo que hace es poner a la cola un bloque de codigo para que se ejecute en el render thread (el hilo de ejecucion del cliente)
            client.execute {
                // Aquí abriremos la Screen custom, pasándole text y options
                client.setScreen(DialogueScreen(nodeId, text, options))
            }
        }


    }
}