val scala3Version = "3.7.3"

inThisBuild(
  List(
    name              := "E5-and-Dragons",
    version           := "0.1.0-SNAPSHOT",
    organization      := "io.github.guihardbastien",
    scalaVersion      := scala3Version,
    semanticdbEnabled := true
  )
)

// LIB VERSIONS
val munitVersion = "1.2.1"

// DEPENDENCIES
val munit = "org.scalameta" %% "munit" % munitVersion % Test

// APPS
lazy val endGame =
  (project in file("app/end-game"))
    .settings(
      name := "endGame",
      libraryDependencies ++= Seq(munit)
    )
    .dependsOn(exploration, combat, socialInteraction, infra, commons)

// COMMONS
lazy val commons =
  (project in file("commons"))
    .settings(
      name := "commons",
      libraryDependencies ++= Seq(munit)
    )

// CORE
lazy val exploration =
  (project in file("core/exploration"))
    .settings(
      name := "exploration",
      libraryDependencies ++= Seq(munit)
    )
    .dependsOn(commons, socialInteraction)

lazy val combat =
  (project in file("core/combat"))
    .settings(
      name := "combat",
      libraryDependencies ++= Seq(munit)
    )
    .dependsOn(commons)

lazy val socialInteraction =
  (project in file("core/social-interaction"))
    .settings(
      name := "socialInteraction",
      libraryDependencies ++= Seq(munit)
    )
    .dependsOn(commons)

// INFRA

lazy val infra =
  (project in file("infra/"))
    .settings(
      name := "infra",
      libraryDependencies ++= Seq(munit)
    )
    .dependsOn(combat, exploration, socialInteraction)