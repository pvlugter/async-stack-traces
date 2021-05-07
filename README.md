# Async stack traces

Experiments with async stack traces using Cinnamon.

**Note**: Requires Lightbend commercial credentials to be added.

Running the example with `sbt run` currently produces an async stack trace across Akka Actors and Scala Futures as:

```
Test async trace
  at map @ example.Example$.example(Example.scala:24)
  at complete @ example.ExampleActor$$anonfun$receive$1.applyOrElse(Example.scala:55)
  at pipeTo @ example.ExampleActor$$anonfun$receive$1.applyOrElse(Example.scala:53)
  at map @ example.ExampleActor$$anonfun$receive$1.applyOrElse(Example.scala:52)
  at map @ example.ExampleActor$$anonfun$receive$1.$anonfun$applyOrElse$2(Example.scala:51)
  at apply @ example.ExampleActor.two(Example.scala:44)
  at flatMap @ example.ExampleActor$$anonfun$receive$1.applyOrElse(Example.scala:51)
  at map @ example.ExampleActor$$anonfun$receive$1.applyOrElse(Example.scala:50)
  at apply @ example.ExampleActor.one(Example.scala:43)
  at tell @ example.Example$.example(Example.scala:24)
  at example.Example$.example(Example.scala:24)
  at example.Main$.delayedEndpoint$example$Main$1(Example.scala:12)
  at example.Main$delayedInit$body.apply(Example.scala:11)
  at scala.Function0.apply$mcV$sp(Function0.scala:39)
  at scala.Function0.apply$mcV$sp$(Function0.scala:39)
  at scala.runtime.AbstractFunction0.apply$mcV$sp(AbstractFunction0.scala:17)
  at scala.App.$anonfun$main$1(App.scala:76)
  at scala.App.$anonfun$main$1$adapted(App.scala:76)
  at scala.collection.IterableOnceOps.foreach(IterableOnce.scala:563)
  at scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:561)
  at scala.collection.AbstractIterable.foreach(Iterable.scala:919)
  at scala.App.main(App.scala:76)
  at scala.App.main$(App.scala:74)
  at example.Main$.main(Example.scala:11)
  at example.Main.main(Example.scala)
```
