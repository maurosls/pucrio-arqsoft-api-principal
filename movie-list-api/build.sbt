name := "movie-list-api"
version := "1.0"
scalaVersion := "2.13.12"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

// Use standard Scala source directories
Compile / scalaSource := baseDirectory.value / "src" / "main" / "scala"
Test / scalaSource := baseDirectory.value / "src" / "test" / "scala"

libraryDependencies ++= Seq(
  guice,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
)

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
