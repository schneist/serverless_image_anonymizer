import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSLinkerConfig
import sbt.Keys.{libraryDependencies, scalaVersion}
ThisBuild / scalaVersion := "3.1.0"

lazy val commonSettings = Seq(
  version := "1.0.0",

  name := "ScaLambda-Frontend-BluePrint",
  libraryDependencies ++= { Seq(
    "org.typelevel" %%% "cats-core" % "2.7.0",
    "org.typelevel" %%% "cats-effect" % "3.3.4",
    "org.scalatest" %%% "scalatest" % "3.2.10" % Test,
    "org.typelevel" %%% "cats-effect-testing-scalatest" % "1.4.0" % Test

  )},
  scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))

)


lazy val NodeFS = (project in file("nodefs"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .enablePlugins(ScalablyTypedConverterPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.10" % Test,
      "com.github.dwickern" %% "scala-nameof" % "4.0.0" % "provided",
    ),
    Compile / npmDependencies ++= Seq(
      "@types/node" -> " 17.0.8"
    )
  )
  .dependsOn(shared)

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
    //scalaJSUseMainModuleInitializer := true,
    stUseScalaJsDom := true,
    stEnableScalaJsDefined :=  Selection.All,
    stEnableLongApplyMethod := false,
    Compile / npmDependencies ++= Seq(
      "typescript" -> "4.1.4",
      "@types/node" -> " 16.11.9",
      "@types/aws-lambda" -> "8.10.85",
      "cloudevents" -> "5.0.0",
      "sharp" → "0.29.3",
      "canvas" → "2.8.0",
      "@types/sharp" → "0.29.3",
      "@tensorflow/tfjs-node" → "3.11.0",
      "@tensorflow-models/blazeface" → "0.0.7",
      "cross-fetch" → "3.1.4"
    )
  )
  .dependsOn(shared)

lazy val ScalaJsReactVer = "2.0.0"

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(commonSettings)
  .settings(


    libraryDependencies ++= Seq(

      // Optional extensions for Cats / Cats Effect
      // (Note: these need to come before "core")
      "com.github.japgolly.scalajs-react" %%% "core-ext-cats"        % ScalaJsReactVer,
      "com.github.japgolly.scalajs-react" %%% "core-ext-cats_effect" % ScalaJsReactVer,

      // Mandatory
      "com.github.japgolly.scalajs-react" %%% "core"                 % ScalaJsReactVer,

      // Optional utils exclusive to scalajs-react
      "com.github.japgolly.scalajs-react" %%% "extra"                % ScalaJsReactVer,
      "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"   % ScalaJsReactVer,
      // For unit tests
      "com.github.japgolly.scalajs-react" %%% "test"                 % ScalaJsReactVer % Test,
      "dev.optics" %% "monocle-core"  % "3.1.0",
    ),
    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.2",
      "react-dom" -> "17.0.2",
      "@types/react" -> "17.0.33",
      "@types/react-dom" -> "17.0.10",
      "use-file-picker" → "1.4.1"
    ),
    webpackBundlingMode := BundlingMode.LibraryAndApplication("appLibrary"),

  )
  .dependsOn(shared)
  .dependsOn(NodeFS)




