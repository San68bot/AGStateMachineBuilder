class AGStateMachine(private val mainBlock: AGStateMachine.() -> Unit) {
    val states = mutableListOf<AGState>()
    private var currentState = 0
    val runningState
        get() = states[currentState]

    private val oneTimes = arrayListOf(OneTime(), OneTime())

    init {
        oneTimes.forEach { it.reset() }
        states.clear()
        mainBlock()
    }

    private fun resetTransition() {
        oneTimes.forEach { it.reset() }
    }

    fun state(name: String, block: AGState.() -> Unit) {
        if (states.any { it.name == name }) {
            throw IllegalArgumentException("State with name $name already exists")
        }
        val myState = AGState(name, block)
        states.add(myState)
        block(myState)
    }

    fun run() {
        states.forEach {
            if (it == states[currentState]) {
                oneTimes.first().runAction { states[currentState].enterAction?.invoke() }
                //loop
                oneTimes.last().runAction { states[currentState].exitAction?.invoke() }
            }
        }
    }

    fun AGState.enter(block: () -> Unit) {
        states.last().enterAction = block
    }

    fun AGState.loop(block: () -> Unit) {
        states.last().loopAction = block
    }

    fun AGState.exit(block: () -> Unit) {
        states.last().exitAction = block
    }

    fun nextState(name: String) {
        resetTransition()
        println("eek: ${states.indexOf(states.find { it.name == name })}")
        currentState = states.indexOf(states.find { it.name == name })
    }
}

data class AGState(var name: String, var block: AGState.() -> Unit,
                   var enterAction: (() -> Unit)? = null,
                   var loopAction: (() -> Unit)? = null,
                   var exitAction: (() -> Unit)? = null
)