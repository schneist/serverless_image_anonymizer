addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.8.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.1")
resolvers += Resolver.bintrayRepo("oyvindberg", "converter")
libraryDependencies += "org.scala-js" %% "scalajs-env-nodejs" % "1.2.1"
// Current, for Scala.js 1.x.x
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta37")