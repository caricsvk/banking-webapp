# Simple banking api example
This is a demonstration of a simple banking web-api. Using Java EE 7, running on wildfly swarm and H2 in-memory database.

API doucmentation is here: https://github.com/caricsvk/banking-webapp/blob/master/rest-resources.full.adoc

Prerequisites: JDK8, maven (maven only for building and running tests)

Running released jar

	1. Download JAR from one of the releases https://github.com/caricsvk/banking-webapp/releases
	2. java -jar banking-webapp-swarm-1.0.0.jar
	3. Open browser: http://localhost:8080/api/accounts

Build, package, run tests & run app from command line

	1. Clone repo: `git clone https://github.com/caricsvk/banking-webapp.git`
	4. Compile, package and run api tests: `mvn install`
	5. Run `mvn wildfly-swarm:run`
	6. Open browser: http://localhost:8080/api/accounts

You should be seeing XML response with few accounts on the url. When testing as a JSON api don't forget to provide "application/json" Accept HTTP header.
