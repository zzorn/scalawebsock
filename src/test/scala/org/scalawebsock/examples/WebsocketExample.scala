package org.scalawebsock.examples

import org.scalawebsock.{CloseReason, WebSocketEventHandler, WebSocket}
import org.scalawebsock.protocols.ClientProperties
import org.scalawebsock.util.WebsocketURL

/**
 * 
 */
object WebsocketExample {
  def main(args: Array[String]) {

    val location = WebsocketURL("ws://localhost/test")
    val origin: String = "null"
    val clientProperties: ClientProperties = new ClientProperties(location, origin)

    val socket = new WebSocket(location, new WebSocketEventHandler {
      def onClose(socket: WebSocket, reason: CloseReason) {
        println("Connection closed")
      }

      def onMessage(message: String, socket: WebSocket) {
        println("Got message "+ message)
      }

      def onOpen(socket: WebSocket) {
        println("Connection opened")
      }
    }, debug = true)

    socket.connect(clientProperties)

    // TODO: Pass in message handler to the connect (or start) method, optionally pass in a handler for open and close.


  }
}