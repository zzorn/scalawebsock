package org.scalawebsock.examples

import java.net.URI
import org.scalawebsock.WebSocket

/**
 * 
 */
class WebsocketExample {
  def main(args: Array[String]) {

    val url = new URI("ws://127.0.0.1:8080/test")
    val socket = new WebSocket(url)

    socket


  }
}