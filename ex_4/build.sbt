name := "ex_4"
 
version := "1.0" 
      
lazy val `ex_4` = (project in file(".")).enablePlugins(PlayScala)

      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.13"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )
      