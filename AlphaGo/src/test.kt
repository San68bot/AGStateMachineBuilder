import java.lang.Thread.sleep

fun main() {
    val agStateMachine = AGStateMachine {
        state("s1") {
            enter {
                this.name = "b1"
            }
        }

        state("s2") {
            enter {
                this.name = "b2"
            }
        }
    }
    agStateMachine.run()

    println(agStateMachine.states.toString())
    println(agStateMachine.states.size)
    println(agStateMachine.states[0].block)
}