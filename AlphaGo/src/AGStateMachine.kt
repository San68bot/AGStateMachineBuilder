import utils.ElapsedTime
import utils.OneTime

class AGStateMachine(private val mainBlock: AGStateMachine.() -> Unit) {
    private val states = mutableListOf<AGState>()
    private val oneTimes = arrayListOf(OneTime(), OneTime())

    private var currentState = 0
    val runningState get() = states[currentState].name
    var allStatesCompleted = false

    var loops = 0 //this is just for TESTING, do not use it in your code
    private val stateTimer = ElapsedTime()
    val timeInCurrentState get() = stateTimer.seconds()

    init {
        oneTimes.forEach { it.reset() }
        states.clear()
        stateTimer.reset()
        mainBlock()
    }

    private fun resetTransition() {
        loops = 0
        oneTimes.forEach { it.reset() }
        stateTimer.reset()
    }

    fun run(): Boolean {
        states.forEach {
            if (it == states[currentState]) {
                loops++
                oneTimes.first().runAction { states[currentState].enterAction?.invoke() }
                val exit = states[currentState].loopAction?.invoke()
                if (exit!!) oneTimes.last().runAction {
                    states[currentState].isCompleted = true
                    states[currentState].exitAction?.invoke()
                }
                allStatesCompleted = (states[currentState] == states.last()) && (!oneTimes.last().isActive())
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

    fun nextState(name: String) {
        resetTransition()
        currentState = states.indexOf(states.find { it.name == name })
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
                       var loopAction: (() -> Boolean)? = null,
                       var exitAction: (() -> Unit)? = null,
                       var isCompleted: Boolean = false
    )
}