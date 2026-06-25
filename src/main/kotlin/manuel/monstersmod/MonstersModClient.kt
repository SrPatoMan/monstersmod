package manuel.monstersmod

import manuel.monstersmod.npcs.ModEntities
import manuel.monstersmod.npcs.NpcRender
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.util.Identifier
import manuel.monstersmod.MonstersMod.MOD_ID
import manuel.monstersmod.gui.DialogueScreen
import manuel.monstersmod.network.DialogueNetworking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent

object MonstersModClient: ClientModInitializer {

    private var currentDialogueSound: PositionedSoundInstance? = null

    // -1 significa que el jugador no está en zona de cueva, >= 0 es el contador de ticks hasta el siguiente sonido
    private var caveSoundTimer = -1

    private fun randomCaveInterval(): Int {
        val min = 15 * 60 * 20 // 18000 ticks = 15 minutos
        val max = 40 * 60 * 20 // 48000 ticks = 40 minutos
        return min + (Math.random() * (max - min)).toInt()
    }

    override fun onInitializeClient() {

        /*

        Este metodo asocia un tipo de entidad con su render. Aqui le decimos que cuando vaya a dibujar
        ese NPC que le pasamos como primer argumento, que use la clase NpcRender (segundo argumento,
        el cual es una lambda). el argumento de la lambda, ctx, es un EntityRendererFactory.Context,
        un objeto que contiene todo lo necesario para que el renderer funcione.

        */
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            val player = client.player ?: return@register

            if (player.blockY >= -40) {
                caveSoundTimer = -1
                return@register
            }

            if (caveSoundTimer == -1) {
                caveSoundTimer = randomCaveInterval()
                return@register
            }

            caveSoundTimer--
            if (caveSoundTimer <= 0) {
                caveSoundTimer = randomCaveInterval()
                val soundEvent = SoundEvent.of(Identifier(MOD_ID, "momo_amigo"))
                val soundInstance = PositionedSoundInstance.master(soundEvent, 0.4f)
                client.soundManager.play(soundInstance)
            }
        }

        // Registramos los renders
        EntityRendererRegistry.register(ModEntities.XOKAS) {
            ctx -> NpcRender(ctx, Identifier(MOD_ID, "textures/entity/xokas.png"))
        }

        EntityRendererRegistry.register(ModEntities.ALEXELCAPO) {
            ctx -> NpcRender(ctx, Identifier(MOD_ID, "textures/entity/alexelcapo.png"))
        }

        ClientPlayNetworking.registerGlobalReceiver(DialogueNetworking.OPEN_DIALOGUE) { client, handler, buf, responseSender ->
            // Leemos los strings del buffer que envia el servidor. Lo leen en el mismo orden que se escribió en el buffer.
            val npcId = buf.readString()
            val nodeId = buf.readString()
            val text = buf.readString()
            val soundName = buf.readString() // Vacío si el nodo no tiene sonido custom asociado

            // Leemos la cantidad de opciones del buffer.
            val size = buf.readInt()
            val options = (0 until size).map { buf.readString() } // Itrera entre el cero y el numero de opciones que tenemos y para cada iteracion lee el string

            // client es la instancia de MinecraftClient, el objeto principal del juego en el lado del cliente. Lo que hace es poner a la cola un bloque de codigo para que se ejecute en el render thread (el hilo de ejecucion del cliente)
            client.execute {
                // Aquí abriremos la Screen custom, pasándole text y options
                client.setScreen(DialogueScreen(npcId, nodeId, text, options))

                // Si el nodo trae un sonido custom, lo reproducimos. SoundEvent.of crea el evento "al vuelo"
                // a partir del id sin necesidad de registrarlo en el Registry, solo necesita una entrada en sounds.json.
                if (soundName.isNotEmpty()) {
                    // Cortamos el sonido de diálogo anterior si seguía sonando, para que no se solapen.
                    currentDialogueSound?.let { client.soundManager.stop(it) }

                    val soundEvent = SoundEvent.of(Identifier(MOD_ID, soundName))
                    val soundInstance = PositionedSoundInstance.master(soundEvent, 1.0f)
                    currentDialogueSound = soundInstance
                    client.soundManager.play(soundInstance)
                }
            }
        }


    }
}