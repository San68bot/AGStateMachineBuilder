package utils

class OneTime {
    private var oneTimeVar = true
    fun runAction(runnable: Runnable) {
        if (oneTimeVar) {
            oneTimeVar = false
            runnable.run()
        }
    }

    fun reset() {
        oneTimeVar = true
    }

    fun isActive() = oneTimeVar
}