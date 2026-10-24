package com.example.weekfind.model

/**
 * Authoritative persisted model for a saved date in History.
 * [date] is always stored in ISO-8601 format (e.g., "2026-10-24").
 * Weekday and date details are never stored as authoritative data and are always recalculated.
 */
data class WeekFindHistory(
    val id: String,
    val date: String,
    val savedAt: Long
)
