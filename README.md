Simple Servlet Process Application Example
==========================================

https://github.com/camunda/camunda-bpm-examples/tree/master/deployment/servlet-pa

This example demonstrates how to deploy a simple servlet process application.

How to use it
-----------------------------

1. Checkout the project with Git
2. Import the project into your IDE
3. Build it with maven
4. Deploy with docker:
`docker run
 -p 8080:8080
 -v /home/flaprimo/Development/Workspaces/Java/boardgame-process/target/boardgame-process-1.0-SNAPSHOT.war:/camunda/webapps/boardgame-process.war
 --env TZ=Europe/Berlin
 --name boardgameProcess
 camunda/camunda-bpm-platform:latest`
or
`docker build -t <image_tag> .
 && docker run
 -p 8080:8080
 -v /home/flaprimo/Development/Workspaces/Java/boardgame-process/target/boardgame-process-1.0-SNAPSHOT.war:/camunda/webapps/boardgame-process.war
 --env TZ=Europe/Berlin
 --name boardgameProcess
 <image_tag> ` if want to remove example data