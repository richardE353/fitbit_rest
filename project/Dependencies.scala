import sbt._

object Dependencies {
  val akkaVersion = "2.3.11"
  val akkaDeps = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  )

  val commonDeps: Seq[ModuleID] = Seq(
    "com.google.guava" % "guava" % "18.0",
    "com.google.code.findbugs" % "jsr305" % "1.3.+",
    "org.slf4j" % "slf4j-log4j12" % "1.7.12",
    "log4j" % "log4j" % "1.2.17",
    "com.github.nscala-time" %% "nscala-time" % "2.0.0",
    "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test"
  )

  val sprayV = "1.3.3"
  val sprayDeps = Seq(
    "io.spray" %% "spray-json" % "1.3.2",
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-client" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-testkit" % sprayV % "test"
  )
}
