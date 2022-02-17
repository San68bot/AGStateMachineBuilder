/*
this file is just for testing purposes such as storing and using data from dsl's
 */
object k {
    val listOfBlocks = mutableListOf<blockTest.() -> Unit>()
}

class blockTest(private val blockTest: blockTest.() -> Unit = {}) {
    init {
        k.listOfBlocks.add(blockTest)
    }
}

enum class testEnum {
    A, B, C
}

class storableBlock(private val block: storableBlock.() -> Unit) {
    val listOfBlocks = mutableListOf<storableBlock.() -> Unit>()

    var readme = "n/a"

    init {
        listOfBlocks.add(block)
    }
    fun run() {
        listOfBlocks.forEach { it() }
    }
}

fun interface runner {
    fun run()
}

var c = 0

fun a(a: runner) {
    a.run()
}

fun main() {
    /*val test = storableBlock {
        readme = "Hello"
    }
    println(test.readme)
    test.run()
    println(test.readme)*/
    a {
        c++
    }
    println(c)
}