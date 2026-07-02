package manuel.monstersmod.npcs

import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import manuel.monstersmod.MonstersMod.MOD_ID

object ModEntities {

    val XOKAS: EntityType<XokasNpc> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(MOD_ID, "xokas"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::XokasNpc)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
            .build()
    )

    val ALEXELCAPO: EntityType<AlexElCapo> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(MOD_ID, "alexelcapo"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::AlexElCapo)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
            .build()
    )

    val MAESTROSIMON: EntityType<MaestroSimon> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(MOD_ID, "maestrosimon"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::MaestroSimon)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
            .build()
    )

    val MADURO: EntityType<Maduro> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(MOD_ID, "maduro"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::Maduro)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
            .build()
    )

}