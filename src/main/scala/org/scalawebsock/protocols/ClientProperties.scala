package org.scalawebsock.protocols

import java.net.URI
import org.scalawebsock.util.WebsocketURL

/**
 * 
 */
case class ClientProperties(location: WebsocketURL, origin: String, subProtocol: String = null ) {
}
