package org.scalawebsock.protocols

import java.net.URI
import org.scalawebsock.util.WebsocketURL

/**
 * 
 */
case class ServerProperties(host: WebsocketURL, supportedSubProtocols: Set[String]) {

}