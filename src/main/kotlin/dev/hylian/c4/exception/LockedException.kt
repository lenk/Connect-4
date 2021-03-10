package dev.hylian.c4.exception

class LockedException(message: String = "game is locked, no winners!") : Exception(message) {
}