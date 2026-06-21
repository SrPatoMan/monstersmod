package manuel.monstersmod.items

import manuel.monstersmod.MonstersMod
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects

class MisItems {

    companion object{

        val MANGO_LOCO: Item? = registrarItem("mangoloco", Item(FabricItemSettings()
            .maxCount(1)
            .food(
                FoodComponent.Builder()
                    .hunger(0)
                    .alwaysEdible()
                    .saturationModifier(0f)
                    .statusEffect(
                        StatusEffectInstance(StatusEffects.SPEED, 3600, 4),
                        1f
                    )
                    .build()
            )
        ))

        val MONSTER_BLANCO: Item? = registrarItem("monsterblanco", Item(FabricItemSettings()
            .maxCount(1)
        ))

        val CUBO_KFC_VACIO: Item? = registrarItem("cubokfcvacio", Item(FabricItemSettings()))

        val CUBO_KFC: Item? = registrarItem("cubokfc", CuboKfcItem(FabricItemSettings()
            .maxCount(1)
            .food(
                FoodComponent.Builder()
                    .hunger(20)
                    .saturationModifier(20f)
                    .build()
                )
            )
        )

        val PESETA: Item? = registrarItem("peseta", Item(FabricItemSettings()))
        val FINAL_FANTASY_7: Item? = registrarItem("finalfantasy", Item(FabricItemSettings()))
        val BUTTER: Item? = registrarItem("butter", Item(FabricItemSettings()))


        // Funciones //

        /*

        Registry.register() crea el registro del item en la tabla de registros de los ITEMS (Registries.ITEM).
        Con Identifier() le pasas el id del registro y le pasas luego el item, que es una instancia de la clase Item
        y le pasas al constructor una instancia de la clase Item.Settings o de la clase FabricItemSettings (lo mismo
        pero con mas funcionalidades). Éste será el objeto de configuración del item.

        */

        fun registrarItem(idItem: String, item: Item ): Item? {
            return Registry.register(
                Registries.ITEM,
                Identifier(MonstersMod.MOD_ID, idItem),
                item
                )
        }

        /*

        Obtenemos el evento del grupo del inventario creativo, por ejemplo pasamos ItemGroups.INGREDIENTS,
        que es el identificador de la pestaña de ingredientes del inventario creativo. El identificador de la
        pestaña se lo pasamos a la función ItemGroupEvents.modifyEntriesEvent() para obtener el evento y con la
        función register() al evento nos enganchamos/suscribimos al evento y le pasamos el item que queremos agregar
        mediante una lambda. {entries -> entries.add(item)}, entries es el objeto que se va a inyectar en el evento
        cuando se dispare y con add() añadimos el item.

         */

        fun agregarAlInventario(item: Item?, tabCreativo: RegistryKey<ItemGroup>) {
            ItemGroupEvents.modifyEntriesEvent(tabCreativo).register { entries -> entries.add(item) }
        }
    }

}