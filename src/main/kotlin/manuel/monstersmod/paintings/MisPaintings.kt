package manuel.monstersmod.paintings

import manuel.monstersmod.MonstersMod
import net.minecraft.entity.decoration.painting.PaintingVariant
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

/*

Los PaintingVariant no son un registro dinámico basado en JSON, se registran en código igual que un item.
Tras registrarlos aquí, hay que añadir su id al tag data/minecraft/tags/painting_variant/placeable.json
para que puedan salir al azar al colocar un cuadro vanilla. La textura debe existir en
assets/monstersmod/textures/painting/<id>.png con tamaño (ancho*16 x alto*16) píxeles.

*/

class MisPaintings {

    companion object {

        val BEST_UNEMPLOYED_MOMENTS = registrarCuadro("best_unemployed_moments", 4, 3)
        val BOBESPONJA_2 = registrarCuadro("bobesponja_2", 4, 2)
        val BOBESPONJA_GANGSTER = registrarCuadro("bobesponja_gangster", 4, 4)
        val CALAMARDO_LOCO = registrarCuadro("calamardo_loco", 2, 1)
        val CODIFICANDO = registrarCuadro("codificando", 1, 1)
        val LINUX_PINTURA = registrarCuadro("linux_pintura", 2, 2)
        val MOGGED1 = registrarCuadro("mogged1", 1, 2)
        val MOMO1 = registrarCuadro("momo1", 4, 4)
        val NUEVATEMP = registrarCuadro("nuevatemp", 2, 2)
        val PABLOMOTOS1 = registrarCuadro("pablomotos1", 2, 2)
        val PABLOMOTOS2 = registrarCuadro("pablomotos2", 2, 2)
        val PERROSANCHE = registrarCuadro("perrosanche", 4, 4)
        val SAUL_GOODMAN = registrarCuadro("saul_goodman", 2, 2)
        val SIRIUSBLACK = registrarCuadro("siriusblack", 2, 2)
        val TEAM_PIRULETA = registrarCuadro("team_piruleta", 2, 1)
        val WILLY2 = registrarCuadro("willy2", 4, 3)
        val WILLYPUTERO = registrarCuadro("willyputero", 2, 2)
        val WILLY = registrarCuadro("willy", 4, 4)

        fun registrarCuadro(id: String, anchoBloques: Int, altoBloques: Int): PaintingVariant {
            // PaintingVariant espera el ancho/alto en píxeles (16 por bloque), no en bloques.
            return Registry.register(
                Registries.PAINTING_VARIANT,
                Identifier(MonstersMod.MOD_ID, id),
                PaintingVariant(anchoBloques * 16, altoBloques * 16)
            )
        }
    }
}
