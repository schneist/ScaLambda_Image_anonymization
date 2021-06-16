
enablePlugins(ScalaJSPlugin)
enablePlugins(ScalablyTypedConverterPlugin)



lazy val copyRes = TaskKey[Unit]("copyRes") //.dependsOn(fullOptJS)

name := "ScaLambda-ImageAnon"

version := "1.0.0"


scalaVersion := "2.13.5"

libraryDependencies += "net.exoego" %%% "aws-lambda-scalajs-facade" % "0.11.0"
libraryDependencies += "net.exoego" %%% "scala-js-nodejs-v12" % "0.13.0"
libraryDependencies += "net.exoego" %%% "aws-sdk-scalajs-facade" % "0.32.0-v2.798.0"
libraryDependencies +="com.lihaoyi" %%% "upickle" % "1.3.11"
libraryDependencies += "org.typelevel" %%% "cats-core" % "2.6.0"
val cologVersion = "0.1.1"

libraryDependencies ++= Seq(
  "com.github.alonsodomin.colog" %% "colog-core"       % cologVersion,
  "com.github.alonsodomin.colog" %% "colog-standalone" % cologVersion
)

scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))

enablePlugins(ScalaJSBundlerPlugin)
enablePlugins(ScalablyTypedConverterPlugin)



copyRes := {
  import Path._
  val src = (Compile / target).value / "scala-2.13" / "scalajs-bundler" / "main"
  val jsFiles: Seq[File] = (src ** "*.js").get()
  val pairs = jsFiles pair rebase(src, baseDirectory.value / "handler")
  println("Copied:")
  IO.copy(pairs, CopyOptions.apply(overwrite = true, preserveLastModified = true, preserveExecutable = false)).map(println)

}


Compile / npmDependencies ++= Seq(
  "@types/node" -> " 14.14.14",
  "sharp" -> "0.28.0",
  "@types/sharp" -> "0.28.0"
)