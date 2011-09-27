package org.scalawebsock

/**
 * Reason why a websocket was closed
 */
sealed trait CloseReason

sealed trait ClosedNormally extends CloseReason

case object ClosedByLocal extends ClosedNormally
case object ClosedByRemote extends ClosedNormally

case class ClosedBecauseOfError(error: Throwable) extends CloseReason
