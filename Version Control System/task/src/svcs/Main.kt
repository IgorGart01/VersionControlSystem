package svcs
import java.io.File
fun main(args: Array<String>) {
    val help = mapOf(
        "--help" to "These are SVCS commands:\n" +
                "config     Get and set a username.\n" +
                "add        Add a file to the index.\n" +
                "log        Show commit logs.\n" +
                "commit     Save changes.\n" +
                "checkout   Restore a file.",
        "config" to "", "add" to "", "log" to "", "commit" to "","checkout" to "")
    if (args.isEmpty()) println(help.getValue("--help"))
    else if (args.first() !in help.keys) println("'${args.first()}' is not a SVCS command.")
    else println(help.getValue(args.first()))
    val vcs = File("vcs").apply { mkdir() }
    val config = File("vcs\\config.txt").apply { createNewFile() }
    val index = File("vcs\\index.txt").apply { createNewFile() }
    val log = File("vcs\\log.txt").apply { createNewFile() }
    var hashBoofer = File("hashBoofer.txt").apply { createNewFile() }
    //val commits = File("vcs\\commits").apply { mkdir() }
    if (args.isEmpty()) return
    when (args.first()){
        "config" -> {
            if (args.size == 1) {
                if (config.readText().isEmpty()) println("Please, tell me who you are.")
                else println(config.readText())
            } else {config.writeText("The username is ${args.last()}."); println(config.readText())}
        }
        "add" -> {
            if (args.size == 1){ if (index.readText().isEmpty()) println("Add a file to the index.")
                else println("Tracked files:"); index.forEachLine { println(it) }
            } else { if (!File(args.last()).exists() || index.readLines().contains(args.last())) {
                    println("Can't find '${args.last()}'.");return}
                if (index.readText().isEmpty()) index.appendText(args.last()) else index.appendText("\n${args.last()}")
                println("The file '${args.last()}' is tracked.")
            }
        }
        "commit" -> {
            var hash = File("hash.txt").apply { createNewFile() }
            if (args.size != 1) {
                if (!index.readText().isEmpty()) {
                    val commits = File("vcs\\commits").apply { mkdir() }
                    index.forEachLine { hash.appendText(File(it).readText().hashCode().toString()) }
                    if (hash.readText() != File("hashBoofer.txt").readText() || commits.list()!!.isEmpty()) { //Здесь
                        File("vcs\\commits\\${args.last().hashCode()}").mkdir()
                        index.forEachLine { File(it).copyTo(File("vcs\\commits\\${args.last().hashCode()}\\$it")) }
                        File("hashBoofer.txt").writeText(hash.readText())
                        println("Changes are committed.")
                        File("indexBooffer.txt").apply { createNewFile() }.writeText(log.readText())
                        log.writeText("commit ${args.last().hashCode()}\n" + "Author:${config.readText().substring(15,config.readText().lastIndex)}\n" + "${args.last()}\n")
                        log.appendText(File("indexBooffer.txt").readText())
                    } else println("Nothing to commit.")
                    hash.delete()
                }
            }
            else println("Message was not passed.")
        }
        "log" -> if (File("vcs\\commits").exists()) println(log.readText()) else println("No commits yet.")
        "checkout" -> {
            if (args.size != 1) {
                if (File("vcs\\commits\\${args.last()}").exists()) {
                    File("vcs\\commits\\${args.last()}").list()!!.forEach { File(it.toString()).writeText(File("vcs\\commits\\${args.last()}\\$it").readText()) }
                    println("Switched to commit ${args.last()}.")
                } else println("Commit does not exist.")
            } else println("Commit id was not passed.")
        }
    }
}