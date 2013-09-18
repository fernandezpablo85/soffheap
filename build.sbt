scalaVersion := "2.10.2"

name := "soffheap"

libraryDependencies ++= Seq(
    "com.esotericsoftware.kryo"     %  "kryo"           % "2.16",
    "nl.grons" %% "metrics-scala" % "3.0.2",
    "org.scalatest" % "scalatest_2.10" % "2.0.M6" % "test",
    "junit" % "junit" % "4.8.1" % "test"
)

EclipseKeys.withSource := true
