package org.example.fureverfriends.util

fun checkExpectNull(obj: Any?, lazyMessage: () -> String) {
    if (obj != null) {
        throw IllegalStateException(lazyMessage())
    }
}