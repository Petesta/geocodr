import com.typesafe.sbt.SbtStartScript
import AssemblyKeys._

assemblySettings

seq(SbtStartScript.startScriptForClassesSettings: _*)

SbtStartScript.stage in Compile := Unit

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.4",
  "org.scalaz" %% "scalaz-effect" % "7.0.4",
  "org.scalaz" %% "scalaz-concurrent" % "7.0.4",
  "org.scalaz.stream" %% "scalaz-stream" % "0.2-SNAPSHOT",
  "io.argonaut" %% "argonaut" % "6.0",
  "io.argonaut" %% "argonaut-unfiltered" % "6.0",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "net.databinder" %% "unfiltered" % "0.7.0",
  "net.databinder" %% "unfiltered-filter" % "0.7.0",
  "net.databinder" %% "unfiltered-jetty" % "0.7.0",
  "org.clapper" %% "avsl" % "1.0.1",
  "com.github.nscala-time" %% "nscala-time" % "0.6.0"
)

resolvers ++= Seq(Resolver.sonatypeRepo("snapshots"), Resolver.sonatypeRepo("releases"))

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
    case x => old(x)
  }
}
