import java.io.OutputStreamWriter
import java.net.URL
import java.net.HttpURLConnection

// We can't import libraries here for some reason, so we must use what is provided
// by gradle, which isn't much. The groovy JSON library is meant for use by groovy code,
// so we need some creativity to use it in Kotlin.
import org.apache.groovy.json.internal.LazyMap
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

// Run this as `./gradlew app:updateInstances`
tasks.register<UpdateInstancesTask>("updateInstances") {
    description = "Fetches a list of popular Lemmy instances and writes it to the DefaultInstances.kt file"

    // All lemmy instances with at least this amount of monthly active users will be included.
    minimumMAU.set(50)

    endpointUrl.set("https://api.fediverse.observer/")
    instancesFile.set(file("src/main/java/com/jerboa/DefaultInstances.kt"))
    manifestFile.set(file("src/main/AndroidManifest.xml"))
    nsfwList.set(listOf("lemmynsfw.com"))
}

@UntrackedTask(because = "Output depends on api response")
abstract class UpdateInstancesTask: DefaultTask() {
    private companion object {
        const val START_TAG = "<!--#AUTO_GEN_INSTANCE_LIST_DO_NOT_TOUCH#-->"
        const val END_TAG = "<!--#INSTANCE_LIST_END#-->"
        const val INDENT = 14
    }

    @get:Input
    abstract val minimumMAU: Property<Int>
    @get:Input
    abstract val endpointUrl: Property<String>
    @get:Input
    abstract val nsfwList: ListProperty<String>

    @get:OutputFile
    abstract val instancesFile: RegularFileProperty
    @get:OutputFile
    abstract val manifestFile: RegularFileProperty

    // Some extension methods to make the JsonSlurper output easier to process
    fun LazyMap.getMap(key: String): LazyMap {
        return this[key] as LazyMap
    }

    fun LazyMap.getArray(key: String): ArrayList<*> {
        return this[key] as ArrayList<*>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> LazyMap.getAs(key: String): T {
        return this[key] as T
    }

    @TaskAction
    fun action() {
        // Get sorted list of nodes
        val nodes = getData()
            .getMap("data")
            .getArray("nodes")
            .map {
                val name = (it as LazyMap)["domain"] as String
                val users = it["active_users_monthly"] as Int?

                Pair(name, users ?: 0)
            }
            .filter {
                it.second >= minimumMAU.get() && !nsfwList.get().contains(it.first)
            }
            .sortedBy {
                it.second
            }
            .reversed()

        updateInstanceList(nodes)
        updateManifest(nodes.map { it.first })
    }


    private fun getData(): LazyMap {
        val url = URL(endpointUrl.get())
        val query = """
            {
              nodes(softwarename: "lemmy") {
                domain
                active_users_monthly
              }
            }""".trimIndent()

        // Format JSON request body
        val body = JsonOutput.toJson(mapOf("query" to query))

        // Create POST request
        val req = url.openConnection() as HttpURLConnection
        req.requestMethod = "POST"
        req.doOutput = true
        req.setRequestProperty("Content-Type", "application/json")

        // Write body to request
        OutputStreamWriter(req.outputStream, "UTF-8").use {
            it.write(body)
        }

        // Get response and JSON parse it
        return JsonSlurper().parse(req.inputStream.reader()) as LazyMap
    }

    fun updateInstanceList(nodes: List<Pair<String, Int>>) {
        // Create output file and write header
        val outFile = instancesFile.get().asFile
        outFile.writeText("""
            package com.jerboa
            
            val DEFAULT_LEMMY_INSTANCES = arrayOf(
            
            """.trimIndent()
        )

        // Write each node's name, one per line
        for (n in nodes) {
            outFile.appendText("    \"${n.first}\", // ${n.second} monthly users\n")
        }

        outFile.appendText(")\n")
    }


    fun updateManifest(list: List<String>) {
        val manifest = manifestFile.get().asFile
        val lines = manifest.readLines()
        manifest.writeText("")

        var skip = false

        for (line in lines) {
            if (line.trim() == START_TAG) {
                skip = true
                manifest.appendText(" ".repeat(INDENT) + START_TAG)
                manifest.appendText(genManifestHosts(list))
                manifest.appendText(" ".repeat(INDENT) + END_TAG + System.lineSeparator())
            } else if (line.trim() == END_TAG) {
                skip = false
            } else if (!skip) {
                manifest.appendText(line + System.lineSeparator())
            }
        }
    }

    fun genManifestHosts(list: List<String>): String {
        return list.joinToString(
            separator = System.lineSeparator(),
            prefix = System.lineSeparator(),
            postfix = System.lineSeparator(),
        ) { " ".repeat(INDENT) + "<data android:host=\"$it\"/>" }
    }
}

