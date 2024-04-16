package org.odl

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

fun main() {
    val port = 8888;
    val server = ServerSocket(port)
    println("Server running locally on port $port")

    while (true) {
        val client = server.accept()
        println("Client connected from ${client.inetAddress.hostAddress}:${client.port}")
        val writer = client.getOutputStream()

        while (true) {
            val reader =
                BufferedReader(InputStreamReader(client.getInputStream()))

            // TODO: What are the raw incoming bytes when I hit enter in nc?
            val line = mutableListOf<Int>()
            var char = reader.read()
            while (char != -1) {
                line.add(char)
                char = reader.read()
                if (!reader.ready()) {
                    break
                }
            }

            val chars = line.map { it.toChar() }.joinToString("").uppercase()
            println(chars)
            writer.write(chars.toByteArray())
            writer.write("\n".toByteArray())

            if (Regex("(EXIT|QUIT)").matches(chars)) {
                writer.write("Goodbye!".toByteArray())
                reader.close()
                break
            } else {
                continue
            }
        }

        // TODO: Still need this?
        client.close()
        server.close()
        break
    }
}