# OpenSearch Playground

This project sets up a local open search instance, including the OpenSearch dashboard, and imports a basic data set. It
functions as a playground for topics around AWS' OpenSearch and to try out the interfaces and APIs.

When you start the application for the first time, an OpenSearch index and test data will be created. Since OpenSearch
persists its data into docker volumes, this will throw an error due to duplicate index and document ids on the second
run. For the moment, delete the volumes if you want to start the application a second time or accept the errors in the
log. OpenSearch will work regardless.

## Commands

Use following commands to interact with the system.

### Boot up OpenSearch via Docker

```sh
docker-compose up
```

OpenSearch runs on <http://localhost:9200>. OpenSearch Dashboard runs on <http://localhost:5601>.

The OpenSearch Dashboard is started in developer mode, which results in the deactivation of the security plugin and the
usage of plain HTTP calls.

> ***This is not recommended for production setups.***