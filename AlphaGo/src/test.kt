import java.lang.Thread.sleep

fun main() {
    val agStateMachine = AGStateMachine {
        state("s1") {
            enter {
                name = "b1"
                nextState("s2")
            }
        }

        state("s2") {
            enter {
                name = "b2"
            }
        }
    }
    agStateMachine.run()

    println(agStateMachine.states.toString())
    println(agStateMachine.runningState.name)
    println(agStateMachine.states.size)
    println(agStateMachine.states[0].block)
}