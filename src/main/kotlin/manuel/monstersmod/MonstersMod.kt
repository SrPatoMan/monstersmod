package manuel.monstersmod

import manuel.monstersmod.items.MisItems
import manuel.monstersmod.items.MisItems.Companion.registrarItem
import manuel.monstersmod.tabsCreativo.MisTabs
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import org.slf4j.LoggerFactory

object MonstersMod : ModInitializer {

	const val MOD_ID: String = "monstersmod"

	override fun onInitialize() {


		MisItems.agregarAlInventario(MisItems.MANGO_LOCO, ItemGroups.INGREDIENTS)
		MisTabs.TAB_MOD_MANUEL
	}
}