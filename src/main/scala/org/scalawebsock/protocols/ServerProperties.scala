package org.scalawebsock.protocols

import java.net.URI
import org.scalawebsock.util.WebsocketURL

/**
 * Location is the webscket address of the websocket script, e.g. ws://example.com/demo
 *
 * Origin is the webpage connecting scripts are running on, that the server is ready to accept connections from.
 * If the websocket is not to be used from a javascript on a webpage, it can be "*".
 */
case class ServerProperties(location: WebsocketURL, origin: String, supportedSubProtocols: Set[String]) {

}