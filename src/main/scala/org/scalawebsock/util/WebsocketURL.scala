package org.scalawebsock.util

/**
 * 
 */
case class WebsocketURL(host: String, port: Int, resourceName: String, secure: Boolean) {

  override def toString: String =  {
    val s = new StringBuilder()
    s.append(if (secure) "wss://" else "ws://")
    s.append(host)
    if ((!secure && port != 80) || (secure && port != 443)) s.append(":").append(port)
    if (!resourceName.startsWith("/")) s.append("/")
    s.append(resourceName)
    s.toString()
  }

}