lazy val root = (project in file(".")).settings(
  name := "smoothdst",

  libraryDependencies ++= Seq(
    "joda-time"     % "joda-time"      % "2.9.4",
    "org.joda"      % "joda-convert"   % "1.8.1",
    "org.twitter4j" % "twitter4j-core" % "4.0.4"),

  scalaVersion := "2.11.8")
