package manuel.monstersmod

import manuel.monstersmod.dialogos.DialogueAction
import manuel.monstersmod.items.MisItems
import manuel.monstersmod.items.MisItems.Companion.registrarItem
import manuel.monstersmod.network.DialogueNetworking
import manuel.monstersmod.npcs.AlexElCapo
import manuel.monstersmod.npcs.MaestroSimon
import manuel.monstersmod.npcs.ModEntities
import manuel.monstersmod.paintings.MisPaintings
import manuel.monstersmod.quests.QuestManager
import manuel.monstersmod.tabsCreativo.MisTabs
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import manuel.monstersmod.npcs.NpcEntity
import manuel.monstersmod.npcs.XokasNpc
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.item.ItemStack
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

		// Llamamos a los NPCs para inicializar el object.
		ModEntities.XOKAS
		ModEntities.ALEXELCAPO
		ModEntities.MAESTROSIMON
		ModEntities.MADURO
		//Este metodo establece los atributos por defecto para el NPC. Primer argumento el NPC, segundo los atributos del NPC
		FabricDefaultAttributeRegistry.register(ModEntities.XOKAS, NpcEntity.createAttributes())
		FabricDefaultAttributeRegistry.register(ModEntities.ALEXELCAPO, NpcEntity.createAttributes())
		FabricDefaultAttributeRegistry.register(ModEntities.MAESTROSIMON, NpcEntity.createAttributes())
		FabricDefaultAttributeRegistry.register(ModEntities.MADURO, NpcEntity.createAttributes())

		// Llamamos al companion object para inicializar y registrar los cuadros personalizados.
		MisPaintings.WILLY


		ServerPlayNetworking.registerGlobalReceiver(DialogueNetworking.DIALOGUE_CHOICE) { server, player, handler, buf, responseSender ->
			val npcId = buf.readString()
			val nodeId = buf.readString()
			val optionIndex = buf.readInt()

			// Buscamos el nodo actual en el árbol de dialogo segun el NPC
			val node = when (npcId) {
				"xokas" -> XokasNpc.Dialogue.nodes[nodeId]
				"alexelcapo" -> AlexElCapo.Dialogue.nodes[nodeId]
				"maestrosimon" -> MaestroSimon.Dialogue.nodes[nodeId]
				else -> null
			}

			if (node == null) return@registerGlobalReceiver

			val option = node.options.getOrNull(optionIndex)

			if (option == null) return@registerGlobalReceiver

			val npcIdFinal = npcId
			server.execute {
				// Si la opción lleva a otro nodo, lo mandamos al cliente
				val nextNodeId = option.nextNodeId
				if (nextNodeId != null) {
					val nextNode = when (npcIdFinal) {
						"xokas" -> XokasNpc.Dialogue.nodes[nextNodeId]
						"alexelcapo" -> AlexElCapo.Dialogue.nodes[nextNodeId]
						"maestrosimon" -> MaestroSimon.Dialogue.nodes[nextNodeId]
						else -> null
					}
					if (nextNode != null) {
						DialogueNetworking.sendOpenDialogue(player, npcIdFinal, nextNode)
					}
				}

				// Si la opción tiene una acción, la ejecutamos
				when (option.action) {
					DialogueAction.ACCEPT_QUEST -> {
						val questId = option.questId
						if (questId != null && QuestManager.iniciarQuest(player, questId)) {
							player.sendMessage(Text.literal("Así me gusta chavalin, ponte a currar para MI!!!"), false)
						}
					}
					DialogueAction.DECLINE_QUEST -> {
						player.sendMessage(Text.literal("Cagón, ya vendrás a mi cuando lo necesites..."), false)
					}
					DialogueAction.COMPLETE_QUEST -> {
						val questId = option.questId
						if (questId != null && QuestManager.intentarCompletarQuest(player, questId)) {
							player.sendMessage(Text.literal("¡Quest completada mi rey!"), false)
						} else {
							player.sendMessage(Text.literal("Todavía no has terminado esa misión mi rey."), false)
						}
					}
					DialogueAction.BUY_ITEM -> {
						val itemId = option.itemId
						val price = option.price
						if (itemId != null && price != null) {
							val peseta = MisItems.PESETA!!
							val totalPesetas = player.inventory.main.sumOf { if (it.isOf(peseta)) it.count else 0 } +
									player.inventory.offHand.sumOf { if (it.isOf(peseta)) it.count else 0 }
							if (totalPesetas >= price) {
								var remaining = price
								for (stack in player.inventory.main) {
									if (stack.isOf(peseta) && remaining > 0) {
										val toRemove = minOf(stack.count, remaining)
										stack.decrement(toRemove)
										remaining -= toRemove
									}
								}
								for (stack in player.inventory.offHand) {
									if (stack.isOf(peseta) && remaining > 0) {
										val toRemove = minOf(stack.count, remaining)
										stack.decrement(toRemove)
										remaining -= toRemove
									}
								}
								val itemToGive = when (itemId) {
									"monsterblanco" -> MisItems.MONSTER_BLANCO
									"mangoloco" -> MisItems.MANGO_LOCO
									else -> null
								}
								if (itemToGive != null) {
									player.giveItemStack(ItemStack(itemToGive))
									player.sendMessage(Text.literal("¡Aquí tienes! Vuelve cuando quieras."), false)
								}
							} else {
								player.sendMessage(Text.literal("No tienes suficientes pesetas."), false)
							}
						}
					}
					else -> {}
				}
			}
		}

		LootTableEvents.MODIFY.register { _, _, id, tableBuilder, _ ->
			val esCofre = id.path.contains("abandoned_mineshaft") ||
				id.path.contains("dungeon") ||
				id.path.contains("chests/")
			if (esCofre) {
				tableBuilder.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1f, 1f))
						.with(ItemEntry.builder(MisItems.PESETA)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0f, 5f)))
						)
						.build()
				)
			}
		}

		// Cada vez que muere una entidad en el servidor, avisamos al QuestManager por si el jugador
		// que la mató tiene alguna quest activa de tipo KillEntity que dependa de ese tipo de entidad.
		ServerLivingEntityEvents.AFTER_DEATH.register { entity, damageSource ->
			val jugador = damageSource.attacker as? ServerPlayerEntity ?: return@register
			QuestManager.registrarMuerte(jugador, entity.type)
		}

	}
}