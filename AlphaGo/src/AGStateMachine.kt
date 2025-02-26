import utils.ActionTimer
import utils.OneTime

/**
 * Robust State Machine Builder creates with Kotlin DSLs
 */
class AGStateMachine(mainBlock: AGStateMachine.() -> Unit) {
    /**
     * List of all states
     */
    private val states = mutableListOf<AGState>()

    /**
     * Current running state
     */
    private var currentState = 0

    /**
     * State currently active
     */
    private val runningState get() = states[currentState]

    /**
     * The last state of the state machine
     */
    private val lastState get() = states.last()

    /**
     * Enter one time
     */
    private val enterOneTime = OneTime()

    /**
     * Exit one time
     */
    private val exitOneTime = OneTime()

    /**
     * Runs block between state changes
     */
    private var transition: () -> Unit = {}

    /**
     * Variable to check if all states have been completed
     */
    var completed = false

    /**
     * State timer, resets on state change
     */
    private val stateTimer = ActionTimer()

    /**
     * Retrieves instantaneous time in seconds
     */
    private val captureTimeOneTime = OneTime()

    /**
     * Captured time in seconds
     */
    private var capturedTime = 0.0

    /**
     * Retrieves the time in seconds that the state has been running
     */
    val secondsInState get() = stateTimer.seconds

    /**
     * Resets all variables to default and runs the main block
     */
    init {
        reset()
        states.clear()
        mainBlock.invoke(this)
    }

    /**
     * Creates a new state
     */
    fun state(name: String, block: AGState.() -> Unit) {
        if (states.any { it.name == name })
            throw IllegalArgumentException("State with name $name already exists")
        val myState = AGState(name, block)
        states.add(myState)
        block(myState)
    }

    /**
     * Enter block of code when state is entered
     */
    fun AGState.enter(block: () -> Unit) {
        lastState.enterAction = block
    }

    /**
     * Loop block of code while state is active
     */
    fun AGState.loop(block: () -> Boolean) {
        lastState.loopAction = block
    }

    /**
     * Exit block of code when state is exited
     */
    fun AGState.exit(block: () -> Unit) {
        lastState.exitAction = block
    }

    /**
     * Runs the state machine
     * @return True if all states have been completed
     */
    fun run(): Boolean {
        if (completed) return true
        runningState.apply {
            enterOneTime.once {
                resetTimer()
                enterAction?.invoke()
            }
            if (loopAction.invoke()) {
                exitOneTime.once { exitAction?.invoke() }
                if (this == runningState) nextState()
                completed = (this == lastState && this == runningState)
            }
        }
        return completed
    }

    /**
     * Goes to the next state
     * @param name Name of the state to go to
     * @param runTransition Whether to run the custom transition
     */
    fun nextState(name: String? = null, runTransition: Boolean = true) {
        when(name) {
            null -> {
                if (currentState == states.lastIndex) {
                    completed = true
                    return
                } else {
                    currentState++
                }
            }
            else -> {
                currentState = states.indexOfFirst { it.name == name }.takeIf { it != -1 }
                    ?: throw IllegalArgumentException("State with name $name does not exist")
            }
        }
        if (runTransition) transition.invoke()
        reset()
    }

    /**
     * Custom transition that can be run each time state changes
     */
    infix fun setTransitions(block: () -> Unit): AGStateMachine {
        transition = block
        return this
    }

    /**
     * Checks if the time in seconds has been reached
     * @param time Time in seconds
     * @return True if time has been reached
     */
    infix fun checkTime(time: Double): Boolean = secondsInState >= time

    /**
     * Runs a block of code after a certain time
     * @param time Time in seconds
     * @param block Block of code to run
     * @return True if time has been reached
     */
    fun runAfterTime(time: Double, block: () -> Unit = {}): Boolean {
        val result = checkTime(time)
        if (result) block.invoke()
        return result
    }

    /**
     * Runs a block of code for a certain duration of time
     * @param time Time in seconds
     * @param block Block of code to run
     * @return True if time has been reached
     */
    fun runForTime(time: Double, block: () -> Unit = {}): Boolean {
        if (secondsInState < time) block()
        return checkTime(time)
    }

    /**
     * Captures the time in seconds that the state has been running
     * @return Time in seconds
     */
    fun captureTime(): Double {
        captureTimeOneTime.once { capturedTime = stateTimer.seconds }
        return capturedTime
    }

    /**
     * Resets the state machine for a new run
     */
    fun refresh() {
        completed = false
        currentState = 0
        transition.invoke()
        reset()
    }

    /**
     * Resets state machine constants
     */
    fun reset() {
        capturedTime = 0.0
        listOf(
            captureTimeOneTime,
            enterOneTime,
            exitOneTime
        ).forEach { it.reset() }
        resetTimer()
    }

    /**
     * Resets the state timer
     */
    private fun resetTimer() {
        stateTimer.reset()
    }

    /**
     * Data class that defines properties of a state
     */
    data class AGState (
        var name: String, var block: AGState.() -> Unit,
        var enterAction: (() -> Unit)? = null,
        var loopAction: () -> Boolean = { true },
        var exitAction: (() -> Unit)? = null
    )
}