# Open Search Playground

This project sets up a local open search instance, including the open search dashboard, and imports a basic data set. It
functions as a playground for topics around AWS' open search and to try out the interfaces and APIs.

## Commands

Use following commands to interact with the system.

### Boot up Open Search via Docker

```sh
docker-compose up
```

Open Search runs on <http://localhost:9200>. Open Search Dashboard runs on <http://localhost:5601>.

The Open Search Dashboard is started in developer mode, which results in the deactivation of the security plugin and the
usage of plain HTTP calls.

>***This is not recommended for production setups.***