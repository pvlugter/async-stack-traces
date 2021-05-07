package cinnamon.asynctrace

import akka.actor.ActorRef
import cinnamon.instrument.akka.{ ActorInstrumentation, EmptyActorInstrumentation }

object AsyncTraceAkkaActor {
  def create(): ActorInstrumentation = new AsyncTraceAkkaActor()
}

class AsyncTraceAkkaActor() extends EmptyActorInstrumentation {

  override def actorTold(actorRef: ActorRef, message: Any, sender: ActorRef): AnyRef =
    AsyncTrace.current.add(AsyncLocation.capture(operator = "tell"))

  override def actorReceived(actorRef: ActorRef, message: Any, sender: ActorRef, toldContext: AnyRef): AnyRef =
    enter(toldContext)

  override def actorCompleted(actorRef: ActorRef, message: Any, sender: ActorRef, receivedContext: AnyRef): Unit =
    restore(receivedContext)

  def enter(trace: AnyRef): AnyRef = trace match {
    case trace: AsyncTrace => AsyncTrace.enter(trace)
    case _ => null
  }

  def restore(trace: AnyRef): Unit = trace match {
    case trace: AsyncTrace => AsyncTrace.activate(trace)
    case _ =>
  }
}
