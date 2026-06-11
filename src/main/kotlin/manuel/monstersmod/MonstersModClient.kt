package manuel.monstersmod

import manuel.monstersmod.npcs.ModEntities
import manuel.monstersmod.npcs.NpcRender
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.util.Identifier
import manuel.monstersmod.MonstersMod.MOD_ID

object MonstersModClient: ClientModInitializer {

    override fun onInitializeClient() {

        /*

        Este metodo asocia un tipo de entidad con su render. Aqui le decimos que cuando vaya a dibujar
        ese NPC que le pasamos como primer argumento, que use la clase NpcRender (segundo argumento,
        el cual es una lambda). el argumento de la lambda, ctx, es un EntityRendererFactory.Context,
        un objeto que contiene todo lo necesario para que el renderer funcione.

        */
        EntityRendererRegistry.register(ModEntities.XOKAS) {
            ctx -> NpcRender(ctx, Identifier(MOD_ID, "textures/entity/xokas.png"))
        }

    }


}