name := "ndelius2"

organization := "uk.gov.justice.digital"

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

ReactJsKeys.harmony := true

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  filters,
  javaWs.exclude("commons-logging", "commons-logging"),
  "org.webjars" %% "webjars-play" % "2.6.1",
  "org.webjars" % "es5-shim" % "4.5.9",
  "org.webjars.bower" % "react" % "0.14.9", // https://facebook.github.io/react/blog/2016/01/12/discontinuing-ie8-support.html
  "org.webjars" % "underscorejs" % "1.8.3",
  ("org.languagetool" % "language-en" % "3.7").exclude("commons-logging", "commons-logging"),
  "org.projectlombok" % "lombok" % "1.16.16" % "provided"
)

mainClass in assembly := Some("play.core.server.ProdServerStart")

fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

assemblyMergeStrategy in assembly := {
  case playWs if playWs.contains("play/api/libs/ws/package") || playWs.endsWith("reference-overrides.conf") => MergeStrategy.last
  case other => (assemblyMergeStrategy in assembly).value(other)
}

assemblyJarName in assembly := "ndelius2.jar"
