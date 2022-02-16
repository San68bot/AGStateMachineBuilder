# AGStateMachineBuilder
## Introduction
This is a library for easy creation of state machines using advanced concepts of kotlin.
As of 2/15/2022, this library only works in kotlin, but will be expanded to work in Java later
(whenever I get bored enough to rewrite an entire code structure in java :/ ).
## Usage
For creating a state machine, you can use the AGStateMachineBuilder.kt class, such as follows:
```kotlin
val stateMachine = AGStateMachineBuilder {
    state("first") {
        enter {
            //code that runs one time when entering the state
        }
        
        loop {
            //code that runs every time the state is active
            true //some condition here that returne a boolean, (ALWAYS PUT AT AND OF SEGMENT)
        }
        
        exit {
            //code that runs one time when exiting the state
            nextState("second")
        }
    }
    
    state("second") {
        enter {
            //code that runs one time when entering the state
        }
        
        loop {
            //code that runs every time the state is active
            true //some condition here that returne a boolean, (ALWAYS PUT AT AND OF SEGMENT)
        }
        
        exit {
            //code that runs one time when exiting the state
            //since this is the last state, we dont need a next stage call here
        }
    }
}
```
So now we defined a state machine, and we can use it to create a state machine object like so:
```kotlin
while (!stateMachine.allStatesCompleted){
    stateMachine.run()
}
```