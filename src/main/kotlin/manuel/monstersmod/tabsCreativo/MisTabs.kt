package manuel.monstersmod.tabsCreativo

import manuel.monstersmod.MonstersMod
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import manuel.monstersmod.items.MisItems.Companion.MANGO_LOCO
import manuel.monstersmod.items.MisItems.Companion.MONSTER_BLANCO
import manuel.monstersmod.items.MisItems.Companion.CUBO_KFC
import manuel.monstersmod.items.MisItems.Companion.CUBO_KFC_VACIO
import manuel.monstersmod.items.MisItems.Companion.FINAL_FANTASY_7
import manuel.monstersmod.items.MisItems.Companion.PESETA

class MisTabs {

    companion object {

        /*

        El metodo FabricItemGroup.builder() devuelve una instancia de la clase de Minecraft ItemGroup.Builder,
        y este objeto sirve para construir y configurar nuestra nueva pestaña del creativo. El metodo .build() del
        final es el que devuelve el ItemGroup ya construido, el displayName() es el nombre que aparecerá, metemos
        la clave de la pestaña e irá a buscar su traducción en el en_us.json o el del idioma correspondiente,el icon()
        el icono que aparece en la pestaña del creativo. ItemStack es un item con cantidad y le pasamos el MANGO_LOCO
        para que lo use de icono de la petaña. entries es el objeto que Fabric inyectará cuando se construya la pestaña
        del creativo y con el metodo entries.add() añadimos los objetos/items que queramos que aparezcan en esa pestaña.

         */

        val TAB_MOD_MANUEL: ItemGroup? = registrarTab("tab_mod_manuel", FabricItemGroup.builder()
            .displayName(Text.translatable("itemgroup.monstersmod.tab_mod_manuel"))
            .icon { ItemStack(MANGO_LOCO) }
            .entries { context, entries -> entries.add(MANGO_LOCO)
                entries.add(MONSTER_BLANCO)
                entries.add(CUBO_KFC)
                entries.add(CUBO_KFC_VACIO)
                entries.add(PESETA)
                entries.add(FINAL_FANTASY_7)
            }
            .build()
        )

        /*

        Funcion Registry.register normal y corriente solo que esta vez en vez de usarla para registrar Items la usamos
        para registrar un ItemGroup, que es la clase que

         */

        fun registrarTab(idTab: String, tab: ItemGroup ): ItemGroup? {
            return Registry.register(
                Registries.ITEM_GROUP,
                Identifier(MonstersMod.MOD_ID, idTab),
                tab
            )
        }

    }

}