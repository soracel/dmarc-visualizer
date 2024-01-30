#! /bin/bash

java -jar /ip-abuse-checker/app.jar -abuseApiKey $ABUSE_API_KEY -elasticUrl $ELASTIC_URL -updateInterval $UPDATE_INTERVAL
