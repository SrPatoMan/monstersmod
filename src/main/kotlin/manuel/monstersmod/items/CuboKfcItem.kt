package manuel.monstersmod.items

import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraft.entity.LivingEntity
import manuel.monstersmod.items.MisItems

/*

finishUsing() es el metodo que se llama cuando terminas de comer, le llamamos para cuando termines de comer te devuelva
el cubo vacío del KFC
 */

class CuboKfcItem(settings: Settings) : Item(settings) {
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        super.finishUsing(stack, world, user)
        return ItemStack(MisItems.CUBO_KFC_VACIO!!)
    }
}