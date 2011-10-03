package org.scalawebsock.protocols

import java.io._
import org.scalawebsock.WebsocketException
import javax.xml.ws.WebServiceException
import org.scalawebsock.util.WebsocketURL

/**
 *
 *
 * Reference: http://tools.ietf.org/html/draft-hixie-thewebsocketprotocol-75
 */

object Hixie75Protocol extends WebSocketProtocol {

  private val clientHeaderPrefix: String = "GET "
  private val clientHeaderPostfix: String = " HTTP/1.1"
  private val serverHeader: String = "HTTP/1.1 101 Web Socket Protocol Handshake"

  private val upgradeProperty: String = "Upgrade"
  private val upgradeValue: String = "WebSocket"

  private val connectionProperty: String = "Connection"
  private val connectionValue: String = "Upgrade"

  val subProtocolProperty: String = "WebSocket-Protocol"

  private val frameStart = 0x00
  private val frameEnd = 0xFF

  private def makeClientHeader(host: WebsocketURL): String = {
    clientHeaderPrefix + host.resourceName + clientHeaderPostfix
  }

  def clientHandshake(clientProperties: ClientProperties,
                      input: BufferedReader,
                      output: Writer): Map[String, String] = {

    sendHandshake(output, makeClientHeader(clientProperties.host), clientProperties)

    val receivedProperties = receiveHandshake(input, serverHeader, "server")

    verifyRequestedSubProtocolOnClient(clientProperties, receivedProperties)

    receivedProperties
  }

  def serverHandshake(serverProperties: ServerProperties,
                      input: BufferedReader,
                      output: Writer): Map[String, String] = {

    val clientProperties = receiveHandshake(input, makeClientHeader(serverProperties.host), "client")

    val updatedServerProps = verifyRequestedSubProtocolOnServer(serverProperties, clientProperties, serverProperties.supportedSubProtocols)

    sendHandshake(output, serverHeader, updatedServerProps)

    clientProperties
  }

  def sendMessage(message: String, output: Writer) {
    // Send start char
    output.write(frameStart)

    // Send message
    output.write(message)

    // Send end char
    output.write(frameEnd)
  }

  def readMessage(input: BufferedReader): String = {
    // Read start char
    val frameType = input.read()

    if (frameType == frameStart) {
      // Read message until end char
      val s = new StringBuilder()
      var c = input.read()
      while (c != -1 && c != frameEnd) {
        s.append(c.toChar)
        c = input.read()
      }

      if (c == -1) throw new WebsocketException("Connection closed in the middle of a frame")

      s.toString()
    }
    else {
      throw new WebsocketException("Unexpected start of a frame ('"+frameType+"')")
    }
  }

  private def verifyRequestedSubProtocolOnClient(clientProperties: Map[String, String], receivedProperties: Map[String, String]) {
    // Verify that any requested subprotocol is supported
    if (clientProperties.contains(subProtocolProperty)) {
      val subProtocol: String = clientProperties(subProtocolProperty)
      if (!receivedProperties.contains(subProtocolProperty)) {
        throw makeException("server", "Client requested to use the '" + subProtocol + "', but the server did not specify any suprotocol")
      }
      else {
        val serverSubProtocol = receivedProperties(subProtocolProperty)
        if (serverSubProtocol != subProtocol) {
          throw makeException("server", "Client requested to use the '" + subProtocol + "', but the server returned suprotocol '" + serverSubProtocol + "'")
        }
      }
    }
  }

  private def verifyRequestedSubProtocolOnServer(serverProperties: Map[String, String], clientProperties: Map[String, String], supportedSubProtocols: Set[String]): Map[String, String] = {
    // Check if there is a requested subprotocol, and if it is supported
    var updatedServerProps = serverProperties
    if (clientProperties.contains(subProtocolProperty)) {
      val subProtocol: String = clientProperties(subProtocolProperty)
      if (!supportedSubProtocols.contains(subProtocol)) {
        throw makeException("client", "Client requested to use unsupported sub-protocol '" + subProtocol + "', but " +
                (if (!supportedSubProtocols.isEmpty) "only subprotocols " + supportedSubProtocols.mkString(", ") else "no subprotocols") +
                " are supported by the server")
      }
      else {
        // Acknowledge support for the subprotocol
        updatedServerProps += subProtocolProperty -> subProtocol
      }
    }
    updatedServerProps
  }


  private def sendHandshake(output: Writer, header: String, properties: ClientProperties) {
    output.write(header + "\n")
    output.write("Upgrade: WebSocket\n")
    output.write("Connection: Upgrade\n")

    properties.asMap foreach {
      property: (String, String) =>
        val name = property._1
        val value = property._2
        output.write(name)
        output.write(": ")
        output.write(value)
        output.write("\n")
    }

    output.flush()
  }

  private def receiveHandshake(input: BufferedReader, expectedResponseHeader: String, remoteParty: String): Map[String, String] = {
    // Response should start with fixed header line
    val headerLine = input.readLine()
    val expectedHeaderLine = expectedResponseHeader
    if (!headerLine.equals(expectedHeaderLine)) throw makeException(remoteParty, "Unexpected header line, expected '" + expectedHeaderLine + "', but got '" + headerLine + "'")

    // Followed by a sequence of "parameterName: parameterValue" -pairs, ending with an empty line.
    var remoteParameters: Map[String, String] = Map()
    var line: String = input.readLine()
    while (line != null && !line.trim().isEmpty) {
      remoteParameters += parseHeaderLine(line, remoteParty)
      line = input.readLine()
    }

    // Make sure the mandatory parameters are correct
    verifyProperty(upgradeProperty, upgradeValue, remoteParameters, remoteParty)
    verifyProperty(connectionProperty, connectionValue, remoteParameters, remoteParty)

    remoteParameters
  }

  private def parseHeaderLine(line: String, otherPart: String): (String, String) = {
    val separatorPos = line.indexOf(":")
    if (separatorPos < 0) throw makeException(otherPart, "Expected parameter name, colon, and parameter value, but found no colon", line)

    val parameter = line.substring(0, separatorPos).trim()
    val value = line.substring(separatorPos + 1).trim()

    if (parameter.isEmpty) throw makeException(otherPart, "Empty parameter name", line)
    if (value.isEmpty) throw makeException(otherPart, "Empty parameter value", line)

    parameter -> value
  }

  private def verifyProperty(property: String, expectedValue: String, properties: Map[String, String], otherParty: String) {
    val value: Option[String] = properties.get(property)
    if (value == None) throw makeException(otherParty, property + " property missing")
    if (value.get != expectedValue) throw makeException(otherParty, "Expected "+property+" property to be '" + expectedValue + "', but it was '" + value.get + "'")
  }

  private def makeException(otherPart: String, desc: String, line: String = null): Throwable = {
    new WebsocketException("Error during handshake with "+otherPart+": "+desc+ (if (line != null) " on line '" + line + "'" else ""))
  }



}