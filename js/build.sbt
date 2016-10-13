enablePlugins(ScalaJSPlugin)

scalaVersion := "2.11.7"

persistLauncher := true

libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.1"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "0.11.0"

libraryDependencies += "com.payalabs" %%% "scalajs-react-mdl" % "0.2.0-SNAPSHOT"

jsDependencies ++= Seq(
  "org.webjars.bower" % "react" % "15.0.1" / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
  "org.webjars.bower" % "react" % "15.0.1" / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
  "org.webjars" % "flot" % "0.8.3-1" / "jquery.flot.min.js"
)