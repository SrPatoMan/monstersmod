package manuel.monstersmod

import manuel.monstersmod.items.MisItems
import manuel.monstersmod.items.MisItems.Companion.registrarItem
import manuel.monstersmod.npcs.ModEntities
import manuel.monstersmod.tabsCreativo.MisTabs
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import org.slf4j.LoggerFactory
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import manuel.monstersmod.npcs.NpcEntity

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

	}
}