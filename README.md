# start-delay-playground project

This small application can be used to play around with health checks which monitor the health of applications. The healthcheck is accessible through http://localhost:8080/health when running locally and with the default port 8080.

The startup delay is set to 10 seconds by default, but can be modified by passing in the environment variable STARTUP_DELAY_SEC.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `start-delay-playground-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/start-delay-playground-1.0.0-SNAPSHOT-runner.jar`.

## Build Container

```shell script
podman build -t start-delay-playground:latest .
```

## Run container

```shell script
podman run -p 8080:8080 -it start-delay-playground:latest
```

## Access health check

```shell script
curl http://localhost:8080/health
```

## Run container with custom startup delay

```shell script
podman run -p 8080:8080 -e STARTUP_DELAY_SEC=20 -it start-delay-playground:latest
```

## Use Prebuilt Image

There is also a prebuilt image which can be pulled from quay.io

```shell script
podman run -p 8080:8080 -e STARTUP_DELAY_SEC=20 -it quay.io/jritter/start-delay-playground:latest
```