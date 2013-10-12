import sbt._
object MyApp extends Build {
  lazy val root =
    Project("root", file(".")) dependsOn(unfilteredScalate)
  lazy val unfilteredScalate =
    uri("git://github.com/unfiltered/unfiltered-scalate")
}
