package utils

class OneTime {
    private var oneTimeVar = true

    infix fun runAction(block: () -> Unit) {
        if (oneTimeVar) {
            oneTimeVar = false
            block()
        }
    }

    fun reset() {
        oneTimeVar = true
    }

    fun isActive() = oneTimeVar
}