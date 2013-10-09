scalaVersion := "2.10.2"

organization := "com.despegar"

name := "soffheap"

// we need to add the runtime classpath as a "-cp" argument to the `javaOptions in run`, otherwise caliper
// will not see the right classpath and die with a ConfigurationException
javaOptions in run <++= (fullClasspath in Runtime) map { cp => Seq("-cp", sbt.Build.data(cp).mkString(":")) }

fork in run := true

publishMavenStyle := true

resolvers += "Nexus resolver" at "http://nexus.despegar.it:8080/nexus/content/groups/proxies"

crossPaths := false

publishTo := Some("Nexus releases" at "http://nexus.despegar.it:8080/nexus/content/repositories/releases")
    
libraryDependencies ++= Seq(
    "com.esotericsoftware.kryo"     %  "kryo"           % "2.22",
    "nl.grons" %% "metrics-scala" % "3.0.2" exclude("com.typesafe.akka","akka-actor"),
    "com.google.guava" % "guava" % "15.0",
    "org.scalatest" % "scalatest_2.10" % "2.0.M6" % "test",
    "junit" % "junit" % "4.8.1" % "test",
    "com.google.code.java-allocation-instrumenter" % "java-allocation-instrumenter" % "2.0" % "test",
    "com.google.code.gson" % "gson" % "1.7.1"  % "test"
) 

EclipseKeys.withSource := true

releaseSettings