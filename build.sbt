name := "async-stack-traces"

enablePlugins(Cinnamon)

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.14",
  Cinnamon.library.akkaSPI % Provided,
  Cinnamon.library.scalaFutureSPI % Provided
)

run / cinnamon := true
