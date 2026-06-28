package manuel.monstersmod.quests

import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

/*

Punto de entrada unico para todo lo relacionado con quests desde fuera de este paquete (dialogos, eventos
de muerte, etc.). Por debajo siempre trabaja contra el QuestState del mundo del jugador, que es lo que se
persiste a NBT.

*/
object QuestManager {

    // Inicia una quest para el jugador. Devuelve false si ya la tenia iniciada (o completada) previamente.
    fun iniciarQuest(jugador: ServerPlayerEntity, questId: String): Boolean {
        val estado = QuestState.obtenerEstado(jugador.serverWorld)
        val progresoJugador = estado.progresoDe(jugador.uuid)
        if (progresoJugador.containsKey(questId)) return false

        progresoJugador[questId] = QuestProgreso(questId)
        estado.markDirty()
        return true
    }

    fun estaActiva(jugador: ServerPlayerEntity, questId: String): Boolean {
        val progreso = QuestState.obtenerEstado(jugador.serverWorld).progresoDe(jugador.uuid)[questId]
        return progreso != null && !progreso.recompensaEntregada
    }

    fun estaCompletada(jugador: ServerPlayerEntity, questId: String): Boolean {
        return QuestState.obtenerEstado(jugador.serverWorld).progresoDe(jugador.uuid)[questId]?.recompensaEntregada == true
    }

    // Hay que llamar a esto cuando el jugador mata una entidad (ver el listener en MonstersMod.onInitialize).
    // Recorre todas las quests activas del jugador y avanza las que tengan un objetivo KillEntity de ese tipo.
    fun registrarMuerte(jugador: ServerPlayerEntity, tipoEntidad: EntityType<*>) {
        val estado = QuestState.obtenerEstado(jugador.serverWorld)
        val progresoJugador = estado.progresoDe(jugador.uuid)
        var cambios = false

        progresoJugador.values.forEach { progreso ->
            if (progreso.completada || progreso.recompensaEntregada) return@forEach
            val objetivo = QuestRegistry.obtener(progreso.questId)?.objetivo ?: return@forEach
            if (objetivo is QuestObjective.KillEntity && objetivo.entityType == tipoEntidad) {
                progreso.cantidadActual++
                if (progreso.cantidadActual >= objetivo.requiredAmount) progreso.completada = true
                cambios = true
            }
        }

        if (cambios) estado.markDirty()
    }

    // Comprueba si el objetivo de la quest ya esta cumplido (sin marcarla como entregada todavia).
    fun listaParaEntregar(jugador: ServerPlayerEntity, questId: String): Boolean {
        val quest = QuestRegistry.obtener(questId) ?: return false
        val progreso = QuestState.obtenerEstado(jugador.serverWorld).progresoDe(jugador.uuid)[questId] ?: return false
        if (progreso.recompensaEntregada) return false
        return objetivoCumplido(jugador, quest.objetivo, progreso)
    }

    // Intenta cerrar la quest: comprueba el objetivo, consume los items si aplica y entrega la recompensa.
    // Devuelve false si la quest no esta iniciada o el objetivo todavia no se ha cumplido.
    fun intentarCompletarQuest(jugador: ServerPlayerEntity, questId: String): Boolean {
        val quest = QuestRegistry.obtener(questId) ?: return false
        val estado = QuestState.obtenerEstado(jugador.serverWorld)
        val progreso = estado.progresoDe(jugador.uuid)[questId] ?: return false
        if (progreso.recompensaEntregada) return false
        if (!objetivoCumplido(jugador, quest.objetivo, progreso)) return false

        val objetivo = quest.objetivo
        if (objetivo is QuestObjective.CollectItem) {
            quitarItem(jugador, objetivo.item, objetivo.requiredAmount)
        }

        quest.recompensa?.item?.let { item -> jugador.giveItemStack(ItemStack(item, quest.recompensa.amount)) }

        progreso.completada = true
        progreso.recompensaEntregada = true
        estado.markDirty()
        return true
    }

    private fun objetivoCumplido(jugador: ServerPlayerEntity, objetivo: QuestObjective, progreso: QuestProgreso): Boolean =
        when (objetivo) {
            is QuestObjective.KillEntity -> progreso.cantidadActual >= objetivo.requiredAmount
            is QuestObjective.CollectItem -> contarItem(jugador, objetivo.item) >= objetivo.requiredAmount
            is QuestObjective.TalkToNpc -> true
        }

    private fun contarItem(jugador: ServerPlayerEntity, item: Item): Int =
        jugador.inventory.main.filter { it.item == item }.sumOf { it.count }

    private fun quitarItem(jugador: ServerPlayerEntity, item: Item, cantidad: Int) {
        var restante = cantidad
        for (stack in jugador.inventory.main) {
            if (restante <= 0) break
            if (stack.item == item) {
                val aQuitar = minOf(restante, stack.count)
                stack.decrement(aQuitar)
                restante -= aQuitar
            }
        }
    }
}
