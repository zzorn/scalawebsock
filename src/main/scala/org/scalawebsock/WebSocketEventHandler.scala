package org.scalawebsock

/**
 * 
 */
trait WebSocketEventHandler {

  def onOpen()

  def onMessage(message: String)

  def onClose()

}