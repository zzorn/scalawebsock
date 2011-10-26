package org.scalawebsock.examples

import java.net.URI
import org.scalawebsock.{CloseReason, WebSocketEventHandler, WebSocket}
import org.scalawebsock.protocols.ClientProperties
import org.scalawebsock.util.WebsocketURL

/**
 * 
 */
object WebsocketExample {
  def main(args: Array[String]) {

    val location = WebsocketURL("http://localhost:8080/test")
    val origin: String = "null"
    val clientProperties: ClientProperties = new ClientProperties(location, origin)

    val socket = new WebSocket(location, new WebSocketEventHandler {
      def onClose(socket: WebSocket, reason: CloseReason) {

      }

      def onMessage(message: String, socket: WebSocket) {

      }

      def onOpen(socket: WebSocket) {
        
      }
    }, debug = true)

    socket.connect(clientProperties)

    // TODO: Pass in message handler to the connect (or start) method, optionally pass in a handler for open and close.


  }
}