package manuel.monstersmod.npcs

import net.minecraft.client.render.entity.BipedEntityRenderer
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.util.Identifier

/*

Clase que se encarga de renderizar el NPC en el cliente del jugador. El objeto Context contiene todo
lo necesario para que el renderer funcione. Nuestra clase de NpcRender hereda de BipedEntityRenderer,
la clase de Minecraft para renderizar entidades de forma humana.

*/
class NpcRender(ctx: EntityRendererFactory.Context, private val texture: Identifier)
    : BipedEntityRenderer<NpcEntity, PlayerEntityModel<NpcEntity>>(
    ctx,
    PlayerEntityModel(ctx.getPart(EntityModelLayers.PLAYER), false), // Creamos el modelo 3D
    0.5f // Esta float representa la sombra del NPC, puramente visual
) {
    /*

    Implementamos nuestra propia logica para el metodo getTexture, devolviendo un identificador con el
    id del mod seguido de la ruta donde tendremos nuestra skin para el NPC. Resumido, establecemos la
    skin para nuestro NPC

    */

    // Le pasamos al constructor de la clase la textura de nuestro NPC y getTexture() simplemente la devolverá, con eso ya tendriamos la skin del NPC.
    override fun getTexture(entity: NpcEntity): Identifier = texture
}