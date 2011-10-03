package org.scalawebsock.protocols

import java.net.URI
import org.scalawebsock.util.WebsocketURL

/**
 * 
 */
case class ClientProperties(host: WebsocketURL, origin: URI, subProtocol: String = null ) {

  // TODO: Move the serialization logic to the protocol?

  def asMap: Map[String, String] = {
    var props = Map(
      "Host" -> host.host + ":" + host.port,
      "Origin" -> origin.toString
    )
    if (subProtocol != null && !subProtocol.isEmpty) {
      props += Hixie75Protocol.subProtocolProperty -> subProtocol
    }
    props
  }


}