import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSLinkerConfig
import sbt.Keys.{libraryDependencies, scalaVersion}
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.additionalNpmConfig
import scalajsbundler.util.JSON._


lazy val commonSettings = Seq(
  version := "1.0.0",
  scalaVersion := "2.13.6",
  name := "ScaLambda-Poll",
  libraryDependencies ++= { Seq(
    "com.lihaoyi" %%% "upickle" % "1.3.15",
    "org.typelevel" %%% "cats-core" % "2.6.1",
    "me.shadaj" %%% "slinky-core" % "0.6.7",
    "me.shadaj" %%% "slinky-web" % "0.6.7",
    "me.shadaj" %%% "slinky-native" % "0.6.7",
    "me.shadaj" %%% "slinky-hot" % "0.6.7",
    "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.6.7",
    "me.shadaj" %%% "slinky-react-router" % "0.6.7",
  )},
  stFlavour := Flavour.Slinky,
  scalacOptions += "-Ymacro-annotations",
  scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
  webpackBundlingMode := BundlingMode.LibraryAndApplication("appLibrary"),
  stFlavour := Flavour.Slinky,
  Compile / additionalNpmConfig := Map(
    "name"        -> str("Emil"),
    "version"     -> str("1.0.0"),
    "description" -> str("Awesome ScalaJS project..."),
    "other"       -> obj(
      "value0" -> bool(true),
      "value1" -> obj(
        "foo" -> str("bar")
      )
    )
  )
)

lazy val shared = (project in file("shared"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterPlugin)
  .settings(commonSettings)
  .settings(
    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.2",
      "react-dom" -> "17.0.2",
      "@types/react" -> "17.0.4",
      "@types/react-dom" -> "17.0.3"
    )
  )

lazy val backend = (project in file("backend"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= { Seq(
      "net.exoego" %%% "aws-lambda-scalajs-facade" % "0.11.0",
      "net.exoego" %%% "scala-js-nodejs-v12" % "0.13.0",
      "net.exoego" %%% "aws-sdk-scalajs-facade" % "0.33.0-v2.892.0"
    )},
    Compile / npmDependencies ++= Seq(
      "@types/node" -> " 14.14.14",
      "react" -> "17.0.2",
      "react-dom" -> "17.0.2",
      "@types/react" -> "17.0.4",
      "@types/react-dom" -> "17.0.3",
    )
  )
  .dependsOn(shared)

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies += "com.comcast" %%% "ip4s-core" % "3.0.3",
    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.2",
      "react-dom" -> "17.0.2",
      "@types/react" -> "17.0.4",
      "@types/react-dom" -> "17.0.3",
      "xlsx" -> "0.17.0",
      "js-yaml" ->  "4.1.0",
      "@types/js-yaml" ->  "4.0.2"
    ),
    scalaJSUseMainModuleInitializer := true

  )
  .dependsOn(shared)
