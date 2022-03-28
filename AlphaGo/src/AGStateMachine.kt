import utils.OneTime

class AGStateMachine(mainBlock: AGStateMachine.() -> Unit) {
    private val states = mutableListOf<AGState>()
    private var currentState = 0
    val runningState get() = states[currentState]
    var allStatesCompleted = false

    private val oneTimes = arrayListOf(OneTime(), OneTime())

    init {
        oneTimes.forEach { it.reset() }
        states.clear()
        mainBlock()
    }

    private fun resetTransition() {
        oneTimes.forEach { it.reset() }
    }

    fun run(): Boolean {
        if (allStatesCompleted) return true
        states.forEach {
            if (it == states[currentState]) {
                oneTimes.first().runAction { states[currentState].enterAction?.invoke() }
                val exit = states[currentState].loopAction?.invoke()!!
                when {
                    exit && states[currentState].exitAction == null -> {
                        states[currentState].isCompleted = true
                        nextState()
                    }

                    exit && states[currentState].exitAction != null -> {
                        oneTimes.last().runAction {
                            states[currentState].exitAction?.invoke()
                            states[currentState].isCompleted = true
                        }
                        if (!allStatesCompleted) allStatesCompleted = (states[currentState] == states.last()) && (!oneTimes.last().isActive())
                    }
                }
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
        val nextState = states.find { it.name == name }
            ?: throw IllegalArgumentException("State with name $name does not exist")
        currentState = states.indexOf(nextState)
    }

    fun nextState() {
        resetTransition()
        if (currentState == states.lastIndex) {
            allStatesCompleted = true
            states[currentState].isCompleted = true
        } else {
            currentState++
        }
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
                       var loopAction: (() -> Boolean)? = { true },
                       var exitAction: (() -> Unit)? = null,
                       var isCompleted: Boolean = false
    )
}