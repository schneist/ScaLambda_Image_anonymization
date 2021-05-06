
enablePlugins(ScalaJSPlugin)
enablePlugins(ScalablyTypedConverterPlugin)



lazy val copyRes = TaskKey[Unit]("copyRes") //.dependsOn(fullOptJS)

name := "ScaLambda-Poll"

version := "1.0.0"


scalaVersion := "2.13.5"

libraryDependencies += "net.exoego" %%% "aws-lambda-scalajs-facade" % "0.11.0"
libraryDependencies += "net.exoego" %%% "scala-js-nodejs-v12" % "0.13.0"
libraryDependencies += "net.exoego" %%% "aws-sdk-scalajs-facade" % "0.33.0-v2.892.0"
libraryDependencies +="com.lihaoyi" %%% "upickle" % "1.3.12"
libraryDependencies += "org.typelevel" %%% "cats-core" % "2.6.0"
val cologVersion = "0.1.1"

libraryDependencies ++= Seq(
  "com.github.alonsodomin.colog" %% "colog-core"       % cologVersion,
  "com.github.alonsodomin.colog" %% "colog-standalone" % cologVersion
)

libraryDependencies += "me.shadaj" %%% "slinky-core" % "0.6.7" // core React functionality, no React DOM
libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.6.7" // React DOM, HTML and SVG tags
libraryDependencies += "me.shadaj" %%% "slinky-native" % "0.6.7" // React Native components
libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.6.7" // Hot loading, requires react-proxy package
libraryDependencies += "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.6.7" // Interop with japgolly/scalajs-react
libraryDependencies += "me.shadaj" %%% "slinky-react-router" % "0.6.7"

scalacOptions += "-Ymacro-annotations"

scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))

enablePlugins(ScalaJSBundlerPlugin)
enablePlugins(ScalablyTypedConverterPlugin)

copyRes := {
  import Path._
  val src = (Compile / target).value / "scala-2.13" / "scalajs-bundler" / "main"
  val jsFiles: Seq[File] = (src ** "scalambda*.js").get()
  val pairsJS = jsFiles pair rebase(src, baseDirectory.value / "handler")
  val mapFiles: Seq[File] = (src ** "scalambda*.map").get()
  val pairsMap = mapFiles pair rebase(src, baseDirectory.value / "handler")
  println("Copied:")
  IO.copy(pairsJS ++ pairsMap , CopyOptions.apply(overwrite = true, preserveLastModified = true, preserveExecutable = false)).map(println)
}

scalaJSUseMainModuleInitializer := true

webpackBundlingMode := BundlingMode.LibraryAndApplication("appLibrary")

Compile / npmDependencies ++= Seq(
  "@types/node" -> " 14.14.14",
  "react" -> "17.0.2",
  "react-dom" -> "17.0.2",
  "@types/react" -> "17.0.4",
  "@types/react-dom" -> "17.0.3",
)