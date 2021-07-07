addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.5.1")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.3")
resolvers += Resolver.bintrayRepo("oyvindberg", "converter")

// Current, for Scala.js 1.x.x
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta34")