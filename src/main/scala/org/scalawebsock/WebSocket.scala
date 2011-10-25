package org.scalawebsock

import java.net.{Socket, URI}
import java.io._
import protocols.{ClientProperties, Hixie75Protocol, WebSocketProtocol}

/**
 * 
 */
class WebSocket(uri: URI,
                handler: WebSocketEventHandler,
                protocol: WebSocketProtocol = Hixie75Protocol) {

  private var socket: Socket = null
  private var _state: WebsocketState = WebsocketStartup
  private var inputStream: BufferedReader = null
  private var outputStream: Writer = null

  def state: WebsocketState  = _state

  /**
   * Connects and listens to incoming traffic.  Blocks until the connection is closed.
   */
  def connect(clientProperties: ClientProperties) {
    _state = WebsocketConnecting

    try {

      // TODO: Pass the part after the host to the protocol connection routines..

      socket = new Socket(uri.getHost , uri.getPort)
      inputStream = new BufferedReader( new InputStreamReader(socket.getInputStream))
      outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))

      protocol.clientHandshake(clientProperties, inputStream, outputStream)

      _state = WebsocketConnected

      handler.onOpen(this)

      if (state == WebsocketConnected) { // (open handler might have closed connection for some reason, so check)
        var message = protocol.readMessage(inputStream)
        while (state == WebsocketConnected && message != null) {
          handler.onMessage(message, this)
          message = protocol.readMessage(inputStream)
        }
      }

      doClose(ClosedByRemote)
    }
    catch {
      case e: Throwable => {
        doClose(ClosedBecauseOfError(e))
        throw e
      }
    }
  }

  def send(message: String) {
    try {
      protocol.sendMessage(message, outputStream)
      outputStream.flush()
    } catch {
      case e: Throwable => {
        doClose(ClosedBecauseOfError(e))
        throw e
      }
    }
  }

  def close() {
    doClose(ClosedByLocal)
  }

  private def doClose(reason: CloseReason) {
    _state = WebsocketClosing

    if (inputStream != null) inputStream.close()
    if (outputStream != null) outputStream.close()
    if (socket != null) socket.close()

    socket = null
    inputStream = null
    outputStream = null

    _state = WebsocketClosed

    handler.onClose(this, reason)
  }

}



