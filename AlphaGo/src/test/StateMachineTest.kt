fun main() {
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
            exit {
                println("Exiting state2")
            }
        }
    }
    while (!agsm.allStatesCompleted){
        agsm.run()
    }
}