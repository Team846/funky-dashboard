enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "0.11.3"

libraryDependencies += "com.payalabs" %%% "scalajs-react-mdl" % "0.2.0-SNAPSHOT"

libraryDependencies += "com.payalabs" %%% "scalajs-react-bridge" % "0.3.0-SNAPSHOT"

npmDependencies in Compile += "react" -> "15.4.0"
npmDependencies in Compile += "react-dom" -> "15.4.0"
npmDependencies in Compile += "chart.js" -> "2.4.0"
npmDependencies in Compile += "react-json-edit" -> "0.2.7"

npmDevDependencies in Compile += "expose-loader" -> "0.7.1"

webpackConfigFile := Some(baseDirectory.value / "webpack.config.js")
