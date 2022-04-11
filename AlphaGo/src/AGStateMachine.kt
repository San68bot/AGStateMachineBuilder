import utils.ActionTimer
import utils.OneTime

class AGStateMachine(mainBlock: AGStateMachine.() -> Unit) {
    private val states = mutableListOf<AGState>()
    private var currentState = 0
    private val runningState get() = states[currentState]
    var allStatesCompleted = false

    private var capturedTime = 0.0
    private val stateTimer = ActionTimer()
    private val oneTimes = arrayListOf(OneTime(), OneTime(), OneTime())
    private var transition: (() -> Unit) = {}

    init {
        oneTimes.forEach { it.reset() }
        states.clear()
        stateTimer.reset()
        mainBlock()
    }

    private fun resetTransition(runCustomTransition: Boolean = true) {
        if (runCustomTransition) transition.invoke()
        oneTimes.forEach { it.reset() }
        capturedTime = 0.0
        resetTimer()
    }

    private fun resetTimer() {
        stateTimer.reset()
    }

    val secondsInState get() = stateTimer.seconds
    infix fun Any.checkTime(time: Double): Boolean {
        return secondsInState >= time
    }

    fun runAfterTime(time: Double, block: () -> Unit = {}): Boolean {
        if (checkTime(time)) block()
        return checkTime(time)
    }

    fun runForTime(time: Double, block: () -> Unit = {}): Boolean {
        if (secondsInState < time) block()
        return checkTime(time)
    }

    fun captureTime(): Double {
        oneTimes[1].runAction { capturedTime = stateTimer.seconds }
        return capturedTime
    }

    fun run(): Boolean {
        if (allStatesCompleted) return true
        oneTimes.first().runAction {
            resetTimer()
            runningState.enterAction?.invoke()
        }
        val exit = runningState.loopAction.invoke()
        when {
            exit && runningState.exitAction == null -> {
                runningState.isCompleted = true
                nextState()
            }

            exit && runningState.exitAction != null -> {
                oneTimes.last().runAction {
                    runningState.exitAction?.invoke()
                    runningState.isCompleted = true
                }
                if (!allStatesCompleted) allStatesCompleted = (runningState == states.last()) && (!oneTimes.last().isActive())
            }
        }
        return allStatesCompleted
    }

    fun state(name: String, block: AGState.() -> Unit) {
        if (states.any { it.name == name }) throw IllegalArgumentException("State with name $name already exists")
        val myState = AGState(name, block)
        states.add(myState)
        block(myState)
    }

    infix fun setTransitions(block: () -> Unit): AGStateMachine {
        transition = block
        return this
    }

    fun nextState(name: String, runCustomTransition: Boolean = true) {
        resetTransition(runCustomTransition)
        val nextState = states.find { it.name == name }
            ?: throw IllegalArgumentException("State with name $name does not exist")
        currentState = states.indexOf(nextState)
        resetTimer()
    }

    fun nextState(runCustomTransition: Boolean = true) {
        resetTransition(runCustomTransition)
        if (currentState == states.lastIndex) {
            allStatesCompleted = true
            states[currentState].isCompleted = true
        } else {
            currentState++
        }
        resetTimer()
    }

    fun AGState.enter(block: () -> Unit) {
        states.last().enterAction = block
    }

    fun AGState.loop(block: () -> Boolean) {
        states.last().loopAction = block
    }

    fun AGState.exit(block: () -> Unit) {
        states.last().exitAction = block
    }

    data class AGState(var name: String, var block: AGState.() -> Unit,
                       var enterAction: (() -> Unit)? = null,
                       var loopAction: (() -> Boolean) = { true },
                       var exitAction: (() -> Unit)? = null,
                       var isCompleted: Boolean = false
    )
}