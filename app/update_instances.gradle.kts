import java.io.OutputStreamWriter
import java.net.URL
import java.net.HttpURLConnection

// We can't import libraries here for some reason, so we must use what is provided
// by gradle, which isn't much. The groovy JSON library is meant for use by groovy code,
// so we need some creativity to use it in Kotlin.
import org.apache.groovy.json.internal.LazyMap
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

// All lemmy instances with at least this amount of monthly active users will be included.
val minimumMAU = 50


val endpointUrl = "https://api.fediverse.observer/"
val instancesFilePath = "src/main/java/com/jerboa/DefaultInstances.kt"
val manifestPath = "src/main/AndroidManifest.xml"
val START_TAG = "<!--#AUTO_GEN_INSTANCE_LIST_DO_NOT_TOUCH#-->"
val END_TAG = "<!--#INSTANCE_LIST_END#-->"
val IDENT = 14


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

// Run this as `./gradlew app:updateInstances -no-configuration-cache`
tasks.register("updateInstances") {
    description = "Fetches a list of popular Lemmy instances and writes it to the DefaultInstances.kt file"

    doFirst {
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
                it.second >= minimumMAU
            }
            .sortedBy {
                it.second
            }
            .reversed()

        updateInstanceList(nodes)
        updateManifest(nodes.map { it.first })
    }
}


fun getData(): LazyMap {
    val url = URL(endpointUrl)
    val query = """
{
  nodes(softwarename: "lemmy") {
    domain
    active_users_monthly
  }
}"""

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
    val outFile = file(instancesFilePath)
    outFile.writeText(
        """package com.jerboa

val DEFAULT_LEMMY_INSTANCES = arrayOf(
"""
    )

    // Write each node's name, one per line
    for (n in nodes) {
        outFile.appendText("    \"${n.first}\", // ${n.second} monthly users\n")
    }

    outFile.appendText(")\n")
}


fun updateManifest(list: List<String>) {
    val manifest = file(manifestPath)
    val lines = manifest.readLines()
    manifest.writeText("")

    var skip = false

    for (line in lines) {
        if (line.trim() == START_TAG) {
            skip = true
            manifest.appendText(" ".repeat(IDENT) + START_TAG)
            manifest.appendText(genManifestHosts(list))
            manifest.appendText(" ".repeat(IDENT) + END_TAG + System.lineSeparator())
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
    ) { " ".repeat(IDENT) + "<data android:host=\"$it\"/>" }
}

