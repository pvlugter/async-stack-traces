package example

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import cinnamon.asynctrace.AsyncTrace

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

object Main extends App {
  Await.ready(Example.example, 10.seconds)
}

object Example {
  implicit val ec = ExecutionContext.global

  def example = {
    val system = ActorSystem("test")
    val actor = system.actorOf(Props(new ExampleActor))
    implicit val timeout = Timeout(5.seconds)
    val request = ExampleActor.Request(1)
    println("sending request: " + request)
    actor ? request map { response =>
      println("received response: " + response)
      AsyncTrace.current.print("Test async trace")
    }
    system.terminate
    system.whenTerminated
  }
}

object ExampleActor {
  case class Request(x: Int)
  case class Response(x: Int)
  case class Result(x: Int, replyTo: ActorRef)
}

class ExampleActor extends Actor {
  import ExampleActor._
  import context.dispatcher

  def one = Future(1)
  def two = Future(2)

  def receive = {
    case Request(x) =>
      val replyTo = sender()
      one
        .map(_ + x)
        .flatMap(y => two.map(_ - y))
        .map(z => Result(z, replyTo))
        .pipeTo(self)
    case Result(x, replyTo) =>
      replyTo ! Response(x)
  }
}
