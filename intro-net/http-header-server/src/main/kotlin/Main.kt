package org.odl

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

const val httpHeaderDelimiter = "\r\n"
const val httpHeaderBodyDelimiter = "\r\n\r\n"

fun main() {
  val port = 8888;
  val server = ServerSocket(port)
  println("Server running locally on port $port")

  while (true) {
    val client = server.accept()
    println(
      "Client connected from ${client.inetAddress.hostAddress}:${client.port}"
    )
    val reader = BufferedReader(InputStreamReader(client.getInputStream()))
    val writer = client.getOutputStream()

    val chars = mutableListOf<Char>()
    var charInt = reader.read()
    var headerKey = ""
    // Use MutableMap instead of HashMap to maintain insertion order.
    val headers = mutableMapOf<String, String>();
    var serializedHeaders = "{\n"
    while (charInt != -1) {
      chars.add(charInt.toChar())
      if (chars.size >= 2 && chars.subList(
          chars.size - 2,
          chars.size
        ).joinToString("")
          .contains(httpHeaderDelimiter)
      ) {
        // Handles initial URI line.
        if (headerKey.isNotEmpty()) {
          val headerValue = chars.subList(1, chars.size - 2).joinToString("")
          headers[headerKey] = headerValue
          println("$headerKey: $headerValue")
          // If this is reached, we have reached the end of the header body.
        } else if (chars.size == 2 &&
          chars.joinToString("").contains(httpHeaderDelimiter)
        ) {
          chars.clear()
          break
        }
        headerKey = ""
        chars.clear()
      } else if (chars[chars.size - 1] == ':' && headerKey.isEmpty()) {
        headerKey = chars.subList(0, chars.size - 1).joinToString("")
        chars.clear()
      }

      if (!reader.ready()) {
        break
      } else {
        charInt = reader.read()
        continue
      }
    }

    var i = 0
    for (header in headers) {
      i++
      serializedHeaders += "  \"${header.key}\": \"${header.value}\""
      if (i <= headers.size - 1) serializedHeaders += ","
      serializedHeaders += "\n"
    }
    serializedHeaders += "}"
    println(serializedHeaders)
    // Need the following to conform to HTTP/1.1
    writer.write("HTTP/1.1 200 OK$httpHeaderBodyDelimiter".toByteArray())
    writer.write(serializedHeaders.toByteArray())

    client.close()
    server.close()
    break
  }
}
