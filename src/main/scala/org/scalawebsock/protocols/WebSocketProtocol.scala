package org.scalawebsock.protocols

import java.io._

/**
 * Protocol version specific functionality.
 */
trait WebSocketProtocol {

  /**
   * Client side handshake.  Returns map with server properties.
   */
  def clientHandshake(clientProperties: Map[String, String],
                      input: BufferedReader,
                      output: Writer): Map[String, String]

  /**
   * Server side handshake.  Returns map with client properties.
   */
  def serverHandshake(serverProperties: Map[String, String],
                      input: BufferedReader,
                      output: Writer,
                      supportedSubProtocols: Set[String]): Map[String, String]

  def sendMessage(message: String, output: Writer)

  def readMessage(input: BufferedReader): String

}