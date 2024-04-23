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
      // TODO: Keep calling receive? Or just assume entire request
      val reader =
        BufferedReader(InputStreamReader(client.getInputStream()))

      // TODO: What are the raw incoming bytes when I hit enter in nc?
      val chars = mutableListOf<Char>()
      var charInt = reader.read()
      var headerKey = ""
      val headers = mutableMapOf<String, String>();
      while (charInt != -1) {
        chars.add(charInt.toChar())
        println(chars[0])
        if (chars.size >= 5 && chars.subList(chars.size - 5, chars.size - 1).joinToString { "" }
            .contains("\r\n\r\n")) {
          println(headers)
          println(headers)
          chars.clear()
          break
        } else if (chars.size >= 2 && chars.subList(chars.size - 2, chars.size - 1).joinToString { "" }
            .contains("\r\n")) {
          val headerValue = chars.joinToString("")
          headers[headerKey] = headerValue
          println("$headerKey:$headerValue")
          headerKey = ""
          chars.clear()
        } else if (chars[chars.size - 1] == ':') {
          headerKey = chars.joinToString("")
          println("headerKey = $headerKey")
          chars.clear()
        }

        if (!reader.ready()) {
          break
        } else {
          charInt = reader.read()
          continue
        }
      }

      /*
       * TODO
       * Headers and body separated by \r\n\r\n.
       * Each header separated by \r\n.
       * Can you do it in one pass?
       *
       * "Be liberal in what you accept, and conservative in what you send." - Jon Postel
       */

      val serializedHeaders = "{\n"
      for (header in headers) {
        serializedHeaders.plus("  \"${header.key}\": \"${header.value}\"\n")
      }
      serializedHeaders.plus("}\n")
      println(serializedHeaders)
      // Need the follow to conform to HTTP/1.1
      writer.write("HTTP/1.1 200 OK\r\n\r\n".toByteArray())
      writer.write(serializedHeaders.toByteArray())

      break
    }

    // TODO: Still need this?
    client.close()
    server.close()
    break
  }
}