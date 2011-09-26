package org.scalawebsock.protocols

import java.io.{PrintStream, DataInputStream}

/**
 * 
 */

object Hixie75Protocol extends WebSocketProtocol {
  
  def shakeHands(input: DataInputStream, output: PrintStream) {

    // TODO: Parametrize parameters
    output.println("GET /demo HTTP/1.1")
    output.println("Upgrade: WebSocket")
    output.println("Connection: Upgrade")
    output.println("Host: example.com")
    output.println("Origin: http://example.com")
    output.println("WebSocket-Protocol: sample")

    // TODO: Wait for server response

  }

  def sendMessage(message: String, output: PrintStream) {

  }

  def readMessage(input: DataInputStream): String = {

  }

}