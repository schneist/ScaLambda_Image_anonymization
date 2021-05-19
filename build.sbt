import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSLinkerConfig
import sbt.Keys.{libraryDependencies, scalaVersion}


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
  scalacOptions += "-Ymacro-annotations",
  scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),

)

lazy val shared = (project in file("shared"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(

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
    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.2",
      "react-dom" -> "17.0.2",
      "@types/react" -> "17.0.4",
      "@types/react-dom" -> "17.0.3",
    ),
    webpackBundlingMode := BundlingMode.LibraryAndApplication("appLibrary"),
    stFlavour := Flavour.Slinky
  )
  .dependsOn(shared)




