package test

import AGStateMachine

fun main() {
    var count = 0
    val agsm = AGStateMachine {
        state("state1") {
            enter {
                println("Entering state1")
            }

            loop {
                println("Looping state1")
                true
            }

            exit {
                println("Exiting state1")
                nextState()
            }
        }

        state("state2") {
            loop {
                println("Looping state2")
                if (count != 2) nextState("state1")
                count == 2
            }
            exit {
                println("Exiting state2")
                nextState()
            }
        }
    }
    while (!agsm.allStatesCompleted){
        agsm.run()
    }
}