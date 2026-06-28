package manuel.monstersmod.quests

import manuel.monstersmod.MonstersMod.MOD_ID
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.PersistentState
import java.util.UUID

/*

PersistentState es la clase de Minecraft para guardar datos custom en el .dat de la partida (igual que
hace el juego con los mapas o los raids). Aqui guardamos, por jugador (UUID), el progreso de cada quest
que tenga iniciada. Una unica instancia de QuestState vive en el Overworld y se comparte para todos los
jugadores de la partida.

*/
class QuestState : PersistentState() {

    private val progresoPorJugador: MutableMap<UUID, MutableMap<String, QuestProgreso>> = mutableMapOf()

    // Devuelve el mapa de progreso de un jugador, creandolo vacio si es la primera vez que se le consulta.
    fun progresoDe(jugador: UUID): MutableMap<String, QuestProgreso> =
        progresoPorJugador.getOrPut(jugador) { mutableMapOf() }

    fun todosLosProgresos(): Map<UUID, MutableMap<String, QuestProgreso>> = progresoPorJugador

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        val jugadoresNbt = NbtList()
        progresoPorJugador.forEach { (uuid, quests) ->
            val jugadorNbt = NbtCompound()
            jugadorNbt.putUuid("uuid", uuid)

            val questsNbt = NbtList()
            quests.values.forEach { progreso ->
                val questNbt = NbtCompound()
                questNbt.putString("id", progreso.questId)
                questNbt.putInt("cantidad", progreso.cantidadActual)
                questNbt.putBoolean("completada", progreso.completada)
                questNbt.putBoolean("recompensaEntregada", progreso.recompensaEntregada)
                questsNbt.add(questNbt)
            }
            jugadorNbt.put("quests", questsNbt)
            jugadoresNbt.add(jugadorNbt)
        }
        nbt.put("jugadores", jugadoresNbt)
        return nbt
    }

    companion object {
        // Id del fichero .dat dentro de la carpeta data/ del mundo. Lo prefijamos con el MOD_ID para no chocar con otros mods.
        private const val ID = "${MOD_ID}_quests"

        private fun fromNbt(nbt: NbtCompound): QuestState {
            val estado = QuestState()
            val jugadoresNbt = nbt.getList("jugadores", NbtElement.COMPOUND_TYPE.toInt())
            for (i in 0 until jugadoresNbt.size) {
                val jugadorNbt = jugadoresNbt.getCompound(i)
                val uuid = jugadorNbt.getUuid("uuid")

                val quests = mutableMapOf<String, QuestProgreso>()
                val questsNbt = jugadorNbt.getList("quests", NbtElement.COMPOUND_TYPE.toInt())
                for (j in 0 until questsNbt.size) {
                    val questNbt = questsNbt.getCompound(j)
                    val progreso = QuestProgreso(
                        questId = questNbt.getString("id"),
                        cantidadActual = questNbt.getInt("cantidad"),
                        completada = questNbt.getBoolean("completada"),
                        recompensaEntregada = questNbt.getBoolean("recompensaEntregada")
                    )
                    quests[progreso.questId] = progreso
                }
                estado.progresoPorJugador[uuid] = quests
            }
            return estado
        }

        // Punto de entrada unico para obtener el QuestState del mundo: lo crea si no existe o lo carga del disco si ya existia.
        fun obtenerEstado(world: ServerWorld): QuestState =
            world.persistentStateManager.getOrCreate({ nbt -> fromNbt(nbt) }, { QuestState() }, ID)
    }
}
