package com.heikal.alarmku.utils

fun Set<Int>.toDayString(): String {
    return when {
        isEmpty() -> "Once"
        size == 7 -> "Daily"
        size == 5 && containsAll(setOf(2,3,4,5,6)) -> "Monday to Friday"
        else -> {
            val map = mapOf(
                2 to "Mon",
                3 to "Tue",
                4 to "Wed",
                5 to "Thu",
                6 to "Fri",
                7 to "Sat",
                1 to "Sun"
            )

            sorted()
                .mapNotNull { map[it] }
                .joinToString(", ")
        }
    }
}