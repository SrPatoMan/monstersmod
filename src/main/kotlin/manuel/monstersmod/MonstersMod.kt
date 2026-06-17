package manuel.monstersmod

import manuel.monstersmod.dialogos.DialogueAction
import manuel.monstersmod.items.MisItems
import manuel.monstersmod.items.MisItems.Companion.registrarItem
import manuel.monstersmod.network.DialogueNetworking
import manuel.monstersmod.npcs.ModEntities
import manuel.monstersmod.tabsCreativo.MisTabs
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import org.slf4j.LoggerFactory
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import manuel.monstersmod.npcs.NpcEntity
import manuel.monstersmod.npcs.XokasNpc
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.text.Text

object MonstersMod : ModInitializer {

	const val MOD_ID: String = "monstersmod"

	override fun onInitialize() {


		MisItems.agregarAlInventario(MisItems.MANGO_LOCO, ItemGroups.INGREDIENTS)
		MisItems.agregarAlInventario(MisItems.MONSTER_BLANCO, ItemGroups.INGREDIENTS)
		MisItems.agregarAlInventario(MisItems.CUBO_KFC, ItemGroups.INGREDIENTS)
		MisItems.agregarAlInventario(MisItems.CUBO_KFC_VACIO, ItemGroups.INGREDIENTS)
		MisItems.agregarAlInventario(MisItems.PESETA, ItemGroups.INGREDIENTS)
		MisTabs.TAB_MOD_MANUEL

		// Llamamos a un NPC para inicializar el object.
		ModEntities.XOKAS
		//Este metodo establece los atributos por defecto para el NPC. Primer argumento el NPC, segundo los atributos del NPC
		FabricDefaultAttributeRegistry.register(ModEntities.XOKAS, NpcEntity.createAttributes())

		ServerPlayNetworking.registerGlobalReceiver(DialogueNetworking.DIALOGUE_CHOICE) { server, player, handler, buf, responseSender ->
			val nodeId = buf.readString()
			val optionIndex = buf.readInt()

			// Buscamos el nodo actual en el árbol de diálogo del Xokas
			val node = XokasNpc.Dialogue.nodes[nodeId]

			if (node == null) return@registerGlobalReceiver

			// Obtenemos la opción que eligió el jugador
			val option = node.options.getOrNull(optionIndex)

			if (option == null) return@registerGlobalReceiver

			server.execute {
				// Si la opción lleva a otro nodo, lo mandamos al cliente
				val nextNodeId = option.nextNodeId
				if (nextNodeId != null) {
					val nextNode = XokasNpc.Dialogue.nodes[nextNodeId]
					if (nextNode != null) {
						DialogueNetworking.sendOpenDialogue(player, nextNode)
					}
				}

				// Si la opción tiene una acción, la ejecutamos
				when (option.action) {
					DialogueAction.ACCEPT_QUEST -> {
						player.sendMessage(Text.literal("¡Quest aceptada!"), false)
						// aquí irá la lógica real de la quest
					}
					DialogueAction.DECLINE_QUEST -> {
						player.sendMessage(Text.literal("Quizás otro día..."), false)
					}
					else -> {} // sin acción, el diálogo simplemente termina
				}
			}
		}

	}
}