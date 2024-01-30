# DMARC Deamon

## Local setup
### Requirements
The following packages have to be installed:
- docker
- docker-compose
- maven

### Environment variables
For the ip-abuse-checker to run the following environment variables have to be set:
- ABUSE_API_KEY -> the api key can be optained from here https://www.abuseipdb.com/register
- ELASTIC_URL -> the url to the elasticsearch cluster

Example for an .env-file:
```
ABUSE_API_KEY=17[...]04
ELASTIC_URL=http://elasticsearch:9200
```

These two variables can be set in a file called "ip-abuse-checker.env" or directly in the docker-compose.yml file.

### How to run
To install the app one needs to prepare the .env-file as described and then run the script "install.sh".

### How to use
Important URLs (while running docker on localhost):
- localhost:3000 (Grafana)
- localhost:8080 (API)

## Team

| Person                               | Role              |
|--------------------------------------|-------------------|
|  Schenoni Riccardo                   | Developer, Product Owner|
|  Sorace Loris                        | Developer         |
|  Zaugg Fabian                        | Developer, Scrum Master|

## More information
This is a fork of https://github.com/debricked/dmarc-visualizer. Consolidate for further informations about the base project.