publish := {}
publishLocal := {}

val sjsFiles = Def.taskDyn {
  (fastOptJS in (client, Compile)).map { _ =>
    val root = (crossTarget in client).value / "server-resources" / "META-INF" / "resources"
    Seq(
      root,
      root / "sjs",
      root / "sjs" / "client-fastopt.js",
      root / "sjs" / "client-opt.js",
      root / "sjs" / "client-jsdeps.js",
      root / "sjs" / "client-jsdeps.min.js",
      root / "sjs" / "client-launcher.js"
    )
  }
}

lazy val server = project.settings(
  organization := "com.lynbrookrobotics",
  name := "funky-dashboard",
  version := "0.1.0-SNAPSHOT",
  resourceDirectories in Compile ++= Seq(
    (crossTarget in client).value / "server-resources"
  ),
  managedResources in Compile ++= sjsFiles.value,
  publishMavenStyle := true,
  publishTo := Some(Resolver.file("gh-pages-repo", baseDirectory.value / ".." / "repo"))
)

lazy val client = project.settings(
  Seq(packageScalaJSLauncher, fastOptJS, fullOptJS, packageJSDependencies, packageMinifiedJSDependencies) map { packageJSKey =>
    crossTarget in (Compile, packageJSKey) := crossTarget.value / "server-resources" / "META-INF" / "resources"/ "sjs"
  },
  publish := {},
  publishLocal := {}
)

lazy val blackbox = project.dependsOn(server).settings(
  publish := {},
  publishLocal := {}
)