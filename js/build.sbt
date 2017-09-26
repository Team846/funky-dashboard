enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "me.shadaj" %%% "slinky-core" % "0.2.0-SNAPSHOT" // core React functionality, no React DOM
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full) // required for the @react macro annotation

libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.2.0-SNAPSHOT" // React DOM, HTML and SVG tags

npmDependencies in Compile += "react" -> "16.0.0"
npmDependencies in Compile += "react-dom" -> "16.0.0"
npmDependencies in Compile += "chart.js" -> "2.6.0"
npmDependencies in Compile += "react-json-edit" -> "0.2.7"

webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack.config.js")

scalaJSUseMainModuleInitializer := true
