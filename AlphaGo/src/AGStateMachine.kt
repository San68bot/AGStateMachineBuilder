class AGStateMachine(private val mainBlock: AGStateMachine.() -> Unit) {
    val states = mutableListOf<AGState>()
    var currentState = 0

    init {
        states.clear()
        mainBlock()
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
        states[currentState].enterAction?.invoke()
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
}

data class AGState(var name: String, var block: AGState.() -> Unit,
                   var enterAction: (() -> Unit)? = null,
                   var loopAction: (() -> Unit)? = null,
                   var exitAction: (() -> Unit)? = null
)