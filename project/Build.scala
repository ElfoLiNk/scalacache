import sbt._
import Keys._

object CacheableBuild extends Build {
  
  object Versions {
    val scala = "2.11.0-RC4"
    val project = "0.2-SNAPSHOT"
  }

  lazy val root = Project(id = "cacheable",base = file("."))
    .settings(standardSettings: _*)
    .settings(publishArtifact := false)
    .aggregate(core, guava, memcached, ehcache, redis)

  lazy val core = Project(id = "cacheable-core", base = file("core"))
    .settings(standardSettings: _*)
    .settings(
      libraryDependencies <+= scalaVersion { s =>
        "org.scala-lang" % "scala-reflect" % s
      }
    )

  lazy val guava = Project(id = "cacheable-guava", base = file("guava"))
    .settings(standardSettings: _*)
    .settings(
      libraryDependencies ++= jodaTime ++ Seq(
        "com.google.guava" % "guava" % "16.0.1",
        "com.google.code.findbugs" % "jsr305" % "1.3.9"
      )
    )
    .dependsOn(core)

  lazy val memcached = Project(id = "cacheable-memcached", base = file("memcached"))
    .settings(standardSettings: _*)
    .settings(
      libraryDependencies ++= jodaTime ++ Seq(
        "net.spy" % "spymemcached" % "2.10.6"
      )
    )
    .dependsOn(core)

  lazy val ehcache = Project(id = "cacheable-ehcache", base = file("ehcache"))
    .settings(standardSettings: _*)
    .settings(
      libraryDependencies ++= jodaTime ++ Seq(
        "net.sf.ehcache" % "ehcache" % "2.8.1",
        "javax.transaction" % "jta" % "1.1"
      )
    )
    .dependsOn(core)

  lazy val redis = Project(id = "cacheable-redis", base = file("redis"))
    .settings(standardSettings: _*)
    .settings(
      libraryDependencies ++= jodaTime ++ Seq(
        "net.debasishg" % "redisclient_2.10" % "2.11"
      )
    )
    .dependsOn(core)

  lazy val jodaTime = Seq(
    "joda-time" % "joda-time" % "2.3",
    "org.joda" % "joda-convert" % "1.6"
  )

  lazy val standardSettings = Defaults.defaultSettings ++ mavenSettings ++ Seq(
    organization := "com.github.cb372",
    version      := Versions.project,
    scalaVersion := Versions.scala,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.0.2",
      "org.scalatest" % "scalatest_2.11.0-RC3" % "2.1.2" % "test"
    ),
    parallelExecution in Test := false
  )

  lazy val mavenSettings = Seq(
    pomExtra :=
      <url>https://github.com/cb372/cacheable</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:cb372/cacheable.git</url>
        <connection>scm:git:git@github.com:cb372/cacheable.git</connection>
      </scm>
      <developers>
        <developer>
          <id>cb372</id>
          <name>Chris Birchall</name>
          <url>https://github.com/cb372</url>
        </developer>
      </developers>,
    publishTo <<= version { v =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false }
  )
}


