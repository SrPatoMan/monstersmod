package manuel.monstersmod.items

import manuel.monstersmod.MonstersMod
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class MisItems {

    companion object{

        val MANGO_LOCO: Item? = registrarItem("mangoloco", Item(FabricItemSettings()))

        fun registrarItem(idItem: String, item: Item ): Item? {
            return Registry.register(
                Registries.ITEM,
                Identifier(MonstersMod.MOD_ID, idItem),
                item
                )
        }
    }

}