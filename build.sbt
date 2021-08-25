import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSLinkerConfig
import sbt.Keys.{libraryDependencies, scalaVersion}
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.additionalNpmConfig
import scalajsbundler.util.JSON._

version := "1.0.0"
scalaVersion := "2.13.6"
name := "MWahlers"

scalacOptions += "-Ymacro-annotations"
scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
webpackBundlingMode := BundlingMode.LibraryAndApplication("appLibrary")
scalaJSUseMainModuleInitializer := true

enablePlugins(ScalaJSPlugin)
enablePlugins(ScalablyTypedConverterPlugin)

Compile / additionalNpmConfig := Map(
  "name"        -> str(name.value),
  "version"     -> str(version.value),
  "description" -> str("Awesome ScalaJS project..."),
)

libraryDependencies ++= { Seq(
  "net.exoego" %%% "aws-lambda-scalajs-facade" % "0.11.0",
  "net.exoego" %%% "scala-js-nodejs-v12" % "0.13.0",
  "net.exoego" %%% "aws-sdk-scalajs-facade" % "0.33.0-v2.892.0",
  "com.lihaoyi" %%% "upickle" % "1.4.0",
  "org.typelevel" %%% "cats-core" % "2.6.1",
  "com.comcast" %%% "ip4s-core" % "3.0.3"
)}

Compile / npmDependencies ++= Seq(
  "@types/node" -> " 14.14.14",
  "xlsx" -> "0.17.1",
)

