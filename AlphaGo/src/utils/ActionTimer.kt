package utils

class ActionTimer {
    private val timer = ElapsedTime()

    val seconds: Double
        get() = timer.seconds()
    val milliseconds: Double
        get() = timer.milliseconds()

    fun checkTime(sec: Double) = seconds > sec
    fun checkTimeMillis(millis: Double) = milliseconds > millis
    fun reset() = timer.reset()
}