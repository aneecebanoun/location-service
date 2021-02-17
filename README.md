This project to demo Rest endpoint calling another remote Rest endpoint and apply some filtering.
It uses Spring boot WebFlux

To run the project run:
mvn install
mvn package

then switch to target directory and run:
java -jar location-service-0.0.1-SNAPSHOT.jar

open up the browser and type: http://localhost:8080/swagger-ui
this page is a Swagger documentation for this project, you may see the endpoints & test them
