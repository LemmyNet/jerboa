import java.io.OutputStreamWriter
import java.net.URL
import java.net.HttpURLConnection

// We can't import libraries here for some reason, so we must use what is provided
// by gradle, which isn't much. The groovy JSON library is meant for use by groovy code,
// so we need some creativity to use it in Kotlin.
import org.apache.groovy.json.internal.LazyMap
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

val endpointUrl = "https://the-federation.info/v1/graphql"
val instancesFilePath = "src/main/java/com/jerboa/DefaultInstances.kt"
val maxInstancesCount = 10

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

tasks.register("fetchInstances") {
    description = "Fetches a list of popular Lemmy instances and writes it to the DefaultInstances.kt file"

    doFirst {
        val url = URL(endpointUrl)
        val query = """
{
    plat: thefederation_platform_by_pk(id: 73) {
        nodes: thefederation_nodes {
            name
            agg: thefederation_stats_aggregate {
                aggregate {
                    avg {
                        users_monthly
                    }
                }
            }
        }
    }
}"""

        // Format JSON request body
        val body = JsonOutput.toJson(mapOf("query" to query))

        // Create POST request
        val req = url.openConnection() as HttpURLConnection
        req.requestMethod = "POST"
        req.doOutput = true

        // Write body to request
        OutputStreamWriter(req.outputStream, "UTF-8").use {
            it.write(body)
        }

        // Get response and JSON parse it
        val json = JsonSlurper().parse(req.inputStream.reader()) as LazyMap

        // Get sorted list of nodes
        val nodes = json
            .getMap("data")
            .getMap("plat")
            .getArray("nodes")
            .map {
                val name = (it as LazyMap)["name"] as String
                val users = it
                    .getMap("agg")
                    .getMap("aggregate")
                    .getMap("avg")["users_monthly"] as BigDecimal?

                Pair(name, users?.toFloat())
            }
            .filter {
                it.second != null
            }
            .sortedBy {
                it.second
            }
            .reversed()
            .take(maxInstancesCount)

        // Create output file and write header
        val outFile = file(instancesFilePath)
        outFile.writeText(
            """package com.jerboa

val DEFAULT_LEMMY_INSTANCES = arrayOf(
""")

        // Write each node's name, one per line
        for (n in nodes) {
            outFile.appendText("    \"${n.first}\", // ${n.second!!.toInt()} monthly users\n")
        }

        outFile.appendText(")\n")
    }
}
