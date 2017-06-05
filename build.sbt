name := "ndelius2"
organization := "uk.gov.justice.digital"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  filters,
  "org.projectlombok" % "lombok" % "1.16.16" % "provided"
)
