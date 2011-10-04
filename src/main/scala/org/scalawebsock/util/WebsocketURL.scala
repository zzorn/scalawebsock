package org.scalawebsock.util
import java.net.URI
import org.scalawebsock.WebsocketException

/**
 * 
 */
case class WebsocketURL(host: String, port: Int, resourceName: String, secure: Boolean) {

  override def toString: String =  {
    val s = new StringBuilder()

    // Protocol
    s.append(if (secure) "wss://" else "ws://")

    // Host
    s.append(host)

    // Port
    if (port >= 0) {
      if ((!secure && port != 80) ||
           (secure && port != 443)) s.append(":").append(port)
    }

    // Resource
    if (!resourceName.startsWith("/")) s.append("/")
    s.append(resourceName)

    s.toString()
  }

}

object WebsocketURL {

  def apply(uri: String) : WebsocketURL = apply(new URI(uri))

  def apply(uri: URI): WebsocketURL = {
    def makeEx(message: String): WebsocketException = new WebsocketException("Not a valid websocket url: '"+uri.toString+"', " + message)

    if (uri.getFragment != null && !uri.getFragment.isEmpty) throw makeEx("Fragments not allowed (#anchors)")

    // Secure
    val scheme: String = uri.getScheme.toLowerCase
    if (scheme != "ws" && scheme != "wss") throw makeEx("Scheme should be ws or wss")
    val secure = scheme == "wss"

    // Host
    val host = uri.getHost.toLowerCase

    // Port
    var port = uri.getPort
    if (port < 0) port = if (secure) 443 else 80

    // Resource name
    var resourceName = if (uri.getPath == null) "" else uri.getPath
    if (resourceName.isEmpty) resourceName = "/"
    if (uri.getQuery != null && !uri.getQuery.isEmpty) resourceName += "?" + uri.getQuery

    val websocketUrl: WebsocketURL = new WebsocketURL(host, port, resourceName, secure)
    if (websocketUrl.toString.contains(" ")) throw makeEx("No spaces allowed")
    websocketUrl
  }
}