ThisBuild / scalaVersion := "3.3.6"
ThisBuild / organization := "example"
ThisBuild / version := "0.1.0"

Compile / fullLinkJS / scalaJSLinkerConfig ~= {
  _.withSourceMap(false)
}

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "scala-tictactoe",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.8.0",
      "com.lihaoyi" %%% "ujson" % "4.0.2"
    )
  )
