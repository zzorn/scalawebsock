package org.scalawebsock.examples

import java.net.URI
import org.scalawebsock.{CloseReason, WebSocketEventHandler, WebSocket}

/**
 * 
 */
class WebsocketExample {
  def main(args: Array[String]) {

    val url = new URI("ws://127.0.0.1:8080/test")
    val socket = new WebSocket(url, new WebSocketEventHandler {
      def onClose(socket: WebSocket, reason: CloseReason) {

      }

      def onMessage(message: String, socket: WebSocket) {

      }

      def onOpen(socket: WebSocket) {
        
      }
    })

    socket.connect()

    // TODO: Pass in message handler to the connect (or start) method, optionally pass in a handler for open and close.


  }
}