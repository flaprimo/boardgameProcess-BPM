Boardgame Process for Camunda
==========================================

https://github.com/camunda/camunda-bpm-examples/tree/master/deployment/servlet-pa

This example demonstrates how to deploy a simple servlet process application.

An api endpoint to use in conjuction with this example is available here: https://github.com/flaprimo/api_endpoint_BPM

How to use it
-----------------------------

1. Checkout the project with Git
2. Import the project into your IDE
3. Set appropriate email properties and change its name `email.example.properties` -> `email.properties`
4. Build it with maven
5. Deploy with docker:
    * `docker run
 -p 8080:8080
 -v /home/flaprimo/Development/Workspaces/Java/boardgame-process/target/boardgame-process-1.0-SNAPSHOT.war:/camunda/webapps/boardgame-process.war
 --env TZ=Europe/Berlin
 --name boardgameProcess
 camunda/camunda-bpm-platform:latest`
    * to remove example data: `docker build -t <image_tag> .
 && docker run
 -p 8080:8080
 -v /home/flaprimo/Development/Workspaces/Java/boardgame-process/target/boardgame-process-1.0-SNAPSHOT.war:/camunda/webapps/boardgame-process.war
 --env TZ=Europe/Berlin
 --name boardgameProcess
 <image_tag> `
     * to remove example data and map the net to localhost (if need to interface with other localhost services): `docker build -t <image_tag> .
                                && docker run
                                -v /home/flaprimo/Development/Workspaces/Java/boardgame-process/target/boardgame-process-1.0-SNAPSHOT.war:/camunda/webapps/boardgame-process.war
                                --env TZ=Europe/Berlin
                                --name boardgameProcess
                                --net host
                                <image_tag> `
