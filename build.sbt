import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.util.Base64
import java.util.zip.GZIPOutputStream

import sbtcrossproject.{CrossType, crossProject}

organization := "com.lynbrookrobotics"

name := "funky-dashboard"

scalaVersion in ThisBuild := "2.12.4"

lazy val dashboardRoot = project.in(file(".")).
  aggregate(dashboardJS, dashboardJVM, dashboardNative).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val dashboard = crossProject(JSPlatform, JVMPlatform, NativePlatform).crossType(CrossType.Full).in(file(".")).
  settings(
    organization := "com.lynbrookrobotics",
    name := "funky-dashboard",
    libraryDependencies += "com.chuusai" %%% "shapeless" % "2.3.3",
    libraryDependencies += "io.argonaut" %%% "argonaut" % "6.2.1",
    libraryDependencies += "com.github.alexarchambault" %%% "argonaut-shapeless_6.2" % "1.2.0-M8"
  )

lazy val dashboardJS = dashboard.js.settings(
  Seq(npmUpdate in fullOptJS) map { packageJSKey =>
    crossTarget in (Compile, packageJSKey) := crossTarget.value / "server-resources" / "META-INF" / "resources"/ "sjs"
  },
  publish := {},
  publishLocal := {}
)

lazy val sjsFiles = Def.taskDyn {
  ((webpack in fullOptJS) in (dashboardJS, Compile)).map { _ =>
    val root = (crossTarget in dashboardJS).value / "server-resources" / "META-INF" / "resources"
    Seq(
      root / "sjs" / "funky-dashboard-opt-bundle.js"
    )
  }
}

lazy val jsNativeSettings = Def.settings(
  (unmanagedSourceDirectories in Compile) += baseDirectory.value / ".." / "jvm_native" / "src" / "main" / "scala",
  (unmanagedResourceDirectories in Compile) += baseDirectory.value / ".." / "jvm_native" / "src" / "main" / "resources",
  sourceGenerators in Compile += Def.task {
    val resourceFiles = ((resourceDirectories in Compile).value ** ("*.js" | "*.html" | "*.css" | "*.png")).get ++ sjsFiles.value
    val resourceText = resourceFiles.map { f =>
      val baos = new ByteArrayOutputStream()
      val gzip = new GZIPOutputStream(baos)
      gzip.write(Files.readAllBytes(f.toPath))
      gzip.close()

      val gzippedBytes = baos.toByteArray
      baos.close()

      val base64 = Base64.getEncoder.encodeToString(gzippedBytes)
      s""""${f.getName}" -> com.lynbrookrobotics.funkydashboard.Base64.getDecoder.decode(Seq(${base64.grouped(5000).map(s => '"' + s + '"').mkString(",")}).mkString)"""
    }.mkString(",\n")
    val outFile = (sourceManaged in Compile).value / "resources.scala"
    IO.write(
      outFile,
      s"""|package com.lynbrookrobotics.funkydashboard
          |object Resources {
          |val resources = Map(
          |$resourceText
          |)
          |}""".stripMargin
    )
    Seq(outFile)
  }.taskValue
)

lazy val dashboardJVM = dashboard.jvm.settings(
  publishMavenStyle := true,
  publishTo := Some(Resolver.file("gh-pages-repo", baseDirectory.value / ".." / "repo")),
  jsNativeSettings
)

lazy val dashboardNative = dashboard.native.settings(
  publishMavenStyle := true,
  publishTo := Some(Resolver.file("gh-pages-repo", baseDirectory.value / ".." / "repo")),
  jsNativeSettings,
  scalaVersion := "2.11.12"
)
