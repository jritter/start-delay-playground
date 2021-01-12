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

# Our Goal

The reason why this repository has been created is to find a good solution how we can implement health checking of a (containerized) application orchestrated by systemd on Linux. Systemd offers the [sd_notify](https://www.freedesktop.org/software/systemd/man/sd_notify.html) mechanism, which works great when the application implements the healthchecking logic itself. This health checking feature can be essential in case of service dependencies, for instance if `service-two` depends on `service-one` and can only be started when `service-one` is up and **ready to serve**.

[Podman](https://podman.io/) recently added some features to take containerized applications towards this goal (healthchecks, --sdnotify option).

This project includes two systemd unit files, which models two services [delayed-service-one](delayed-service-one.service) and [delayed-service-two](delayed-service-two.service), based on the same container image, containing the small quarkus application in this repo. delayed-service-two depends on delayed-service-one, and therefore delayed-service-one should be started first.

# How to test

## Prerequisites

* Linux distribution running [systemd](https://www.freedesktop.org/wiki/Software/systemd/)
* Podman >= v2.1.0
* No running services on Port 8081 and Port 8082 - of course you can modify these ports in the systemd unit files

## Running the service

```bash
$ git clone https://github.com/jritter/start-delay-playground
$ cp start-delay-playground/delayed-service-*.service ~/.config/systemd/user/
$ systemctl --user daemon-reload
$ systemctl --user start delayed-service-two.service
$ podman ps
CONTAINER ID  IMAGE                                          COMMAND  CREATED         STATUS             PORTS                   NAMES
0c33fb4f30bd  quay.io/jritter/start-delay-playground:latest           20 seconds ago  Up 20 seconds ago  0.0.0.0:8082->8080/tcp  delayed-service-two
22d73b7d3857  quay.io/jritter/start-delay-playground:latest           21 seconds ago  Up 21 seconds ago  0.0.0.0:8081->8080/tcp  delayed-service-one
```

## Running podman health check

```bash
$ podman healthcheck run delayed-service-one
healthy
$ podman healthcheck run delayed-service-two
healthy
```

## Access podman health information

```bash
$ podman inspect delayed-service-one | jq '.[0].State.Healthcheck'
{
  "Status": "healthy",
  "FailingStreak": 0,
  "Log": [
    {
      "Start": "2021-01-12T14:25:15.146768585+01:00",
      "End": "2021-01-12T14:25:15.180849716+01:00",
      "ExitCode": 0,
      "Output": ""
    },
    {
      "Start": "2021-01-12T14:25:31.09430974+01:00",
      "End": "2021-01-12T14:25:31.128751109+01:00",
      "ExitCode": 0,
      "Output": ""
    },
    {
      "Start": "2021-01-12T14:25:47.119073198+01:00",
      "End": "2021-01-12T14:25:47.200766071+01:00",
      "ExitCode": 0,
      "Output": ""
    },
    {
      "Start": "2021-01-12T14:26:03.181938698+01:00",
      "End": "2021-01-12T14:26:03.256994746+01:00",
      "ExitCode": 0,
      "Output": ""
    },
    {
      "Start": "2021-01-12T14:26:19.193355147+01:00",
      "End": "2021-01-12T14:26:19.259866591+01:00",
      "ExitCode": 0,
      "Output": ""
    }
  ]
}
```

## Accessing the health endpoint

```bash
$ curl http://localhost:8081/health

{
    "status": "UP",
    "checks": [
        {
            "name": "Startup Delay Health Check",
            "status": "UP"
        }
    ]
}
$ curl http://localhost:8082/health
{
    "status": "UP",
    "checks": [
        {
            "name": "Startup Delay Health Check",
            "status": "UP"
        }
    ]
}
```



## Current status

Right now we have a solution that when starting delayed-service-two waits until the container image of delayed-service-one is pulled and the container started, but it doesn't wait until the health endpoint reports "UP", which is the ultimate goal.

## Discussion

Please join us in the following discussions:

* https://github.com/containers/podman/issues/6160
* https://github.com/systemd/systemd/issues/9075