# Home assignment #3

## A simple HTTP API

### Introduction

We define a mock JSON HTTP API for a minimalist job board application. 

### Objective

The API exposes the following routes:

* GET /jobs: Returns a map of open positions in the job board.
* POST /jobs: Inserts a new open position in the job board and returns the updated map.
* DELETE /jobs/:id: Removes an open position from the job board and returns the updated map.

### Details 

Job IDs should be textual representations of UUIDs. Jobs should have a company, title, and description field.
Assignment details

The project should be delivered as a leiningen project, starting the API server on localhost port 8080 when launched with lein run.

Storage of jobs should be limited to in-memory, and functional boundaries should be split across clearly defined components within the daemon.

No automated tests will be ran, and attention will be given to choices made in terms of dependencies and code architecture.


## Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/status) to get the current status of the API.
3. Publish a new job sending a POST, for example with curl

curl -X POST http://localhost:8080/jobs -H 'Content-type: application/json' -d '{"company":"exoscale", "title": "software developer", "description": "those guys who code"}' -i

4. You can now check out the new job you just published

curl http://localhost:8080/jobs 


## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).


