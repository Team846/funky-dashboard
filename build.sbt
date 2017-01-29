organization := "com.lynbrookrobotics"

name := "funky-dashboard"

scalaVersion in ThisBuild := "2.12.1"

lazy val dashboardRoot = project.in(file(".")).
  aggregate(dashboardJS, dashboardJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val dashboard = crossProject.in(file(".")).
  settings(
    organization := "com.lynbrookrobotics",
    name := "funky-dashboard",
    version := "0.2.0-SNAPSHOT",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.4.4"
  )

lazy val dashboardJS = dashboard.js.settings(
  Seq(fullOptJS) map { packageJSKey =>
    crossTarget in (Compile, packageJSKey) := crossTarget.value / "server-resources" / "META-INF" / "resources"/ "sjs"
  },
  publish := {},
  publishLocal := {}
)

val sjsFiles = Def.taskDyn {
  ((webpack in fullOptJS) in (dashboardJS, Compile)).map { _ =>
    val root = (crossTarget in dashboardJS).value / "server-resources" / "META-INF" / "resources"
    Seq(
      root,
      root / "sjs",
      root / "sjs" / "funky-dashboard-opt-bundle.js"
    )
  }
}

lazy val dashboardJVM = dashboard.jvm.settings(
  resourceDirectories in Compile ++= Seq(
    (crossTarget in dashboardJS).value / "server-resources"
  ),
  managedResources in Compile ++= sjsFiles.value,
  publishMavenStyle := true,
  publishTo := Some(Resolver.file("gh-pages-repo", baseDirectory.value / ".." / "repo"))
)
