package org.scalawebsock.protocols

import java.io.{DataInputStream, PrintStream}

/**
 * Protocol version specific functionality.
 */
trait WebSocketProtocol {

  def shakeHands(input: DataInputStream, output: PrintStream)

  def sendMessage(message: String, output: PrintStream)

  def readMessage(input: DataInputStream): String

}