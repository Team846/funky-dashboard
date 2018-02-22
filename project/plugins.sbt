resolvers += "Funky-Repo" at "http://lynbrookrobotics.com/repo"

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.21")

addSbtPlugin("org.portable-scala" % "sbt-crossproject" % "0.3.0")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.3.0")

addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.3.7-arm-jni-threads" exclude("org.scala-native", "sbt-crossproject"))

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.9.0")

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "2.0.0")
