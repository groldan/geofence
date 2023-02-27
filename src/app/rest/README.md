# GeoFence REST API Application

This is a (spring-boot) application for the standalone GeoFence REST API.

The OpenAPI 3 specification is split into these files:

- [../../openapi/geofence-model.yaml](../../openapi/geofence-model.yaml)
- [../../openapi/geofence-api.yaml](../../openapi/geofence-api.yaml)

With the application running at [http://localhost:8080/geofence](http://localhost:8080/geofence):

- The base URL will redirect the browser to the HTML API Swagger test page at [http://localhost:8080/geofence/api/v2/swagger-ui/index.html](http://localhost:8080/geofence/api/v2/swagger-ui/index.html).
- The OpenAPI spec is exposed in JSON format at [http://localhost:8080/geofence/api/v2/api-docs](http://localhost:8080/geofence/api/v2/api-docs)


## Build

```
mvn clean install
```

will create a single-jar executable at `target/geofence-rest-app-<version>-exec.jar`.

## Run

Run in development mode with an in-memory H2 database, either with

	mvn spring-boot:run -Dspring-boot.run.profiles=dev

or

	java -jar target/geofence-rest-app-4.0-SNAPSHOT-exec.jar --spring.profiles.active=dev

