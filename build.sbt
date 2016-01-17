publish := {}
publishLocal := {}

val sjsFiles = Def.taskDyn {
  (fullOptJS in (client, Compile)).map { _ =>
    val root = (crossTarget in client).value / "server-resources" / "META-INF" / "resources"
    Seq(
      root,
      root / "sjs",
      root / "sjs" / "client-opt.js",
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
  managedResources in Compile ++= sjsFiles.value
)

lazy val client = project.settings(
  Seq(packageScalaJSLauncher, fullOptJS, packageJSDependencies) map { packageJSKey =>
    crossTarget in (Compile, packageJSKey) := crossTarget.value / "server-resources" / "META-INF" / "resources"/ "sjs"
  },
  publish := {},
  publishLocal := {}
)
