package org.scalawebsock

import protocols.{Hixie75Protocol, WebSocketProtocol}
import java.net.{URI}

/**
 * 
 */
class WebSocket(uri: URI,
                handler: WebSocketEventHandler,
                protocol: WebSocketProtocol = Hixie75Protocol) {

  def connect()

  def send(message: String)

  def close()

}