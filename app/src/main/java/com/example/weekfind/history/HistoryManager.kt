package com.example.weekfind.history

import com.example.weekfind.model.WeekFindHistory
import java.time.LocalDate
import java.util.UUID

class HistoryManager {

    companion object {
        const val MAX_HISTORY_SIZE = 10
        private const val ENTRY_SEPARATOR = "\n"
        private const val FIELD_SEPARATOR = "|"
    }

    /**
     * Adds [date] to [currentHistory] (newest first).
     * - Stores [date] strictly as ISO-8601 (`YYYY-MM-DD`).
     * - Does not create unnecessary consecutive duplicate entries if the top entry already matches [date].
     * - Caps history at [MAX_HISTORY_SIZE] (10 entries), removing the oldest entry when saving an 11th.
     */
    fun addEntry(
        currentHistory: List<WeekFindHistory>,
        date: LocalDate,
        savedAt: Long = System.currentTimeMillis(),
        id: String = UUID.randomUUID().toString()
    ): List<WeekFindHistory> {
        val isoDate = date.toString()

        if (currentHistory.firstOrNull()?.date == isoDate) {
            val updatedTop = currentHistory.first().copy(savedAt = savedAt)
            return listOf(updatedTop) + currentHistory.drop(1)
        }

        val newEntry = WeekFindHistory(
            id = id,
            date = isoDate,
            savedAt = savedAt
        )
        return (listOf(newEntry) + currentHistory).take(MAX_HISTORY_SIZE)
    }

    fun removeEntry(
        currentHistory: List<WeekFindHistory>,
        entryId: String
    ): List<WeekFindHistory> {
        return currentHistory.filterNot { it.id == entryId }
    }

    /**
     * Restores the authoritative [LocalDate] from a [WeekFindHistory] item so that all
     * weekday and calendar properties are freshly recalculated.
     */
    fun restoreDate(entry: WeekFindHistory): LocalDate {
        return LocalDate.parse(entry.date)
    }

    fun serialize(history: List<WeekFindHistory>): String {
        if (history.isEmpty()) return ""
        return history.take(MAX_HISTORY_SIZE).joinToString(ENTRY_SEPARATOR) { item ->
            "${item.id}$FIELD_SEPARATOR${item.date}$FIELD_SEPARATOR${item.savedAt}"
        }
    }

    fun deserialize(raw: String?): List<WeekFindHistory> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(ENTRY_SEPARATOR)
            .mapNotNull { line ->
                val parts = line.split(FIELD_SEPARATOR)
                if (parts.size != 3) {
                    null
                } else {
                    val id = parts[0]
                    val dateStr = parts[1]
                    val savedAt = parts[2].toLongOrNull()
                    val isValidDate = runCatching { LocalDate.parse(dateStr) }.isSuccess
                    if (id.isNotBlank() && isValidDate && savedAt != null) {
                        WeekFindHistory(id = id, date = dateStr, savedAt = savedAt)
                    } else {
                        null
                    }
                }
            }
            .take(MAX_HISTORY_SIZE)
    }
}
