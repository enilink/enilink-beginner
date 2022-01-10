# enilink-beginner
A simple beginner project for eniLINK web applications.

## Building
* This is a plain Maven project
* a full build can be executed via `mvn package`

## Running
* change to the folder `launch`
* run `mvn test -Pconfigure -DskipTests` to initialize or update a launch configuration
* run `mvn test` to (re-)start the eniLINK platform
* The application should now be available at: [http://localhost:8080/beginner/](http://localhost:8080/beginner/)
* The LDP service (with default configuration) should be available at:  [http://localhost:8080/ldp/](http://localhost:8080/ldp/)

## Developing
* The project can be developed with any IDE supporting Java and Scala projects
* **IDEA:** `File > Project from existing sources...`
* **Eclipse:** `File > Import > Maven > Existing Maven Projects`