enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "0.11.3"

libraryDependencies += "com.payalabs" %%% "scalajs-react-mdl" % "0.2.0-SNAPSHOT"

npmDependencies in Compile += "react" -> "15.4.0"
npmDependencies in Compile += "react-dom" -> "15.4.0"
npmDependencies in Compile += "moment" -> "2.16.0"
npmDependencies in Compile += "chart.js" -> "2.3.0"
