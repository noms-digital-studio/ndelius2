// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.18")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.4")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.11")
addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.1.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.2")
addSbtPlugin("com.github.ddispaltro" % "sbt-reactjs" % "0.6.8")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")
dependencyOverrides += "org.webjars.npm" % "graceful-readlink" % "1.0.1"
dependencyOverrides += "org.webjars.npm" % "minimatch" % "3.0.4"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"