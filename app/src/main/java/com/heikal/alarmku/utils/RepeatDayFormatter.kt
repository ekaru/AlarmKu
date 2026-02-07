package com.heikal.alarmku.utils

fun Set<Int>.toDayString(): String {
    return when {
        isEmpty() -> "Once"
        size == 7 -> "Daily"
        size == 5 && containsAll(setOf(1,2,3,4,5)) -> "Monday to Friday"
        else -> {
            val map = mapOf(
                1 to "Mon",
                2 to "Tue",
                3 to "Wed",
                4 to "Thu",
                5 to "Fri",
                6 to "Sat",
                7 to "Sun"
            )

            sorted()
                .mapNotNull { map[it] }
                .joinToString(", ")
        }
    }
}