package com.jakubmeysner.legitnik.util

fun <E> List<E>.startsWith(other: List<E>): Boolean {
    if (size < other.size) {
        return false
    }

    return subList(0, other.size) == other
}
