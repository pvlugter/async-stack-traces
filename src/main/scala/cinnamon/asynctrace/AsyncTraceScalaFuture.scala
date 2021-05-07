package cinnamon.asynctrace

import cinnamon.instrument.scala.future.{ ScalaFutureInstrumentation, ScalaFutureMetadata }

import scala.concurrent.{ ExecutionContext, Future }

object AsyncTraceScalaFuture {
  def create(metadata: ScalaFutureMetadata): ScalaFutureInstrumentation = new AsyncTraceScalaFuture(metadata)
}

class AsyncTraceScalaFuture(metadata: ScalaFutureMetadata) extends ScalaFutureInstrumentation {

  override def futureCreated(future: Future[_]): Unit =
    metadata.attachTo(future, AsyncLocation.capture())

  override def futureCompleting(future: Future[_]): AnyRef =
    AsyncTrace.current.add(AsyncLocation.capture(operator = "complete")) // to capture completes outside of future operators

  override def futureRunnableScheduled(future: Future[_], executor: ExecutionContext): AnyRef =
    AsyncTrace.current

  override def futureRunnableStarted(future: Future[_], executor: ExecutionContext, scheduledContext: AnyRef): AnyRef =
    enter(metadata.extractFrom(future), scheduledContext)

  override def futureRunnableCompleted(future: Future[_], executor: ExecutionContext, startedContext: AnyRef): Unit =
    restore(startedContext)

  override def futureCallbackAdded(future: Future[_], executor: ExecutionContext): AnyRef =
    null // AsyncTrace.current // not used

  override def futureCallbackScheduled(future: Future[_], executor: ExecutionContext, callbackContext: AnyRef, completingContext: AnyRef): AnyRef =
    completingContext // only follow the completing contexts

  override def futureCallbackStarted(future: Future[_], executor: ExecutionContext, scheduledContext: AnyRef): AnyRef =
    enter(metadata.extractFrom(future), scheduledContext)

  override def futureCallbackCompleted(future: Future[_], executor: ExecutionContext, startedContext: AnyRef): Unit =
    restore(startedContext)

  def enter(location: AnyRef, trace: AnyRef): AnyRef = (location, trace) match {
    case (location: AsyncLocation, trace: AsyncTrace) => AsyncTrace.enter(location, trace)
    case _ => null
  }

  def restore(trace: AnyRef): Unit = trace match {
    case trace: AsyncTrace => AsyncTrace.activate(trace)
    case _ =>
  }
}
