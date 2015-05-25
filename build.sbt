import Dependencies._

scalaVersion := "2.11.6"

lazy val commonSettings = Seq(
  organization := "com.rda",
  scalaVersion := "2.11.6",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "fitbit_rest",
    version := "0.1",
    libraryDependencies ++= { commonDeps ++ akkaDeps ++ sprayDeps }
  )

resolvers += "spray repo" at "http://repo.spray.io"
