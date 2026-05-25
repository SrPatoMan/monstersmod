package manuel.monstersmod

import manuel.monstersmod.items.MisItems
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object MonstersMod : ModInitializer {

	const val MOD_ID: String = "monstersmod"

	override fun onInitialize() {
		MisItems.registrarItem()
	}
}