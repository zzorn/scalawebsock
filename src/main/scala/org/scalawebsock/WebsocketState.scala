package org.scalawebsock

/**
 * 
 */

sealed trait WebsocketState {
}

case object WebsocketStartup extends WebsocketState
case object WebsocketConnecting extends WebsocketState
case object WebsocketConnected extends WebsocketState
case object WebsocketClosing extends WebsocketState
case object WebsocketClosed extends WebsocketState