import sbt._

object Dependencies {
  val akkaVersion = "2.3.9"
  val akkaDeps = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  )

  val commonDeps: Seq[ModuleID] = Seq(
    "com.google.guava" % "guava" % "18.0",
    "org.json" % "json" % "20141113",
    "org.slf4j" % "slf4j-log4j12" % "1.7.7",
    "log4j" % "log4j" % "1.2.17",
    "com.github.nscala-time" %% "nscala-time" % "1.8.0",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test"
  )

  val sprayV = "1.3.2"
  val sprayDeps = Seq(
    "io.spray" %% "spray-json" % "1.3.1",
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-client" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-testkit" % "1.3.1" % "test"
  )
}
