package org.scalawebsock

import protocols.{Hixie75Protocol, WebSocketProtocol}
import java.net.{Socket, URI}
import javax.xml.ws.WebServiceClient
import java.io.{PrintStream, DataInputStream, InputStream}
import org.scalawebsock.WebsocketConnected

/**
 * 
 */
class WebSocket(uri: URI,
                handler: WebSocketEventHandler,
                protocol: WebSocketProtocol = Hixie75Protocol) {

  private var socket: Socket = null
  private var _state: WebsocketState = WebsocketStartup
  private var inputStream: DataInputStream = null
  private var outputStream: PrintStream = null

  def state: WebsocketState  = _state

  /**
   * Connects and listens to incoming traffic.  Blocks until the connection is closed.
   */
  def connect() {
    _state = WebsocketConnecting

    socket = new Socket(uri)
    inputStream = new DataInputStream(socket.getInputStream)
    outputStream = new PrintStream(socket.getOutputStream)

    protocol.shakeHands(inputStream, outputStream)

    _state = WebsocketConnected

    handler.onOpen()

    if (state == WebsocketConnected) {
      var message = protocol.readMessage(inputStream)
      while (state == WebsocketConnected && message != null) {
        handler.onMessage(message)
        message = protocol.readMessage(inputStream)
      }
    }

    close()
  }

  def send(message: String) {
    protocol.sendMessage(message, outputStream)
  }

  def close() {
    _state = WebsocketClosing

    if (socket != null) {
      inputStream.close()
      outputStream.close()
      socket.close()
    }
    socket = null
    inputStream = null
    outputStream = null

    _state = WebsocketClosed

    handler.onClose()
  }

}

