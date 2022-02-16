fun main() {
    val agStateMachine = AGStateMachine {
        state("s1") {
            enter {
                println("enter1")
                name = "b1"
            }

            loop {
                println("loop1: $loops")
                name == "b1"
            }

            exit {
                println("exit1")
                nextState("s2")
            }
        }
        state("s2") {
            enter {
                println("enter2")
                name = "b2"
            }

            loop {
                println("loop2: $loops at time $timeInCurrentState")
                name == "b2" && loops == 2000
            }

            exit {
                println("exit2")
            }
        }
    }
    while (!agStateMachine.allStatesCompleted){
        val isdone = agStateMachine.run()
        println("ASC $isdone")
    }

    //println(agStateMachine.states.toString())
    //println("ASC ${agStateMachine.allStatesCompleted}")
    //println(agStateMachine.runningState.name)
    //println(agStateMachine.states.size)
    //println(agStateMachine.states[0].block)
}