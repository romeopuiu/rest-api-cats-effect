ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "rest-api-cats-effect",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.6.3",
      "org.http4s" %% "http4s-dsl" % "0.23.33",
      "org.http4s" %% "http4s-ember-server" % "0.23.33",
      "org.http4s" %% "http4s-circe" % "0.23.33",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC12",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC12",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC12",
      "org.postgresql" % "postgresql" % "42.7.10",
      "io.circe" %% "circe-core" % "0.14.15",
      "io.circe" %% "circe-generic" % "0.14.15",
      "io.circe" %% "circe-parser" % "0.14.15"
    )
  )
import sbtassembly.MergeStrategy

assembly / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.discard
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
