enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

libraryDependencies += "me.shadaj" %%% "slinky-core" % "0.3.0" // core React functionality, no React DOM
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full) // required for the @react macro annotation

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.3.0" // React DOM, HTML and SVG tags

npmDependencies in Compile += "react" -> "16.2.0"
npmDependencies in Compile += "react-dom" -> "16.2.0"
npmDependencies in Compile += "chart.js" -> "2.6.0"
npmDependencies in Compile += "react-json-edit" -> "0.2.7"

webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack.config.js")

scalaJSUseMainModuleInitializer := true
