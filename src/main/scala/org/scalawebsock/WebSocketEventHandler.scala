package org.scalawebsock

/**
 * 
 */
trait WebSocketEventHandler {

  def onOpen(socket: WebSocket)

  def onMessage(message: String, socket: WebSocket)

  def onClose(socket: WebSocket, reason: CloseReason)

}
