#! /bin/bash

ENVFILE=./api/ip-abuse-checker.env

if ! test -f "$ENVFILE"
then
    echo "ABUSE_API_KEY=\nELASTIC_URL=\nUPDATE_INTERVAL=" >> ./api/ip-abuse-checker.env
    echo "please paste your api key and the url of the elasticsearch instance in the env file then start the script again"
    exit
else
    echo "file $ENVFILE already present, not overwriting it"
fi


if command -v mvn &> /dev/null
then
    echo "compiling api component"
    mvn -f ./api clean install

    echo "compiling ip-abuse-checker component"
    mvn -f ./ip-abuse-checker clean install

else
    echo "maven is not installed. please install maven to build the components"
    exit
fi

cp ./ip-abuse-checker/grafana/dashboard.json ./grafana/Grafana-DMARC_Reports.json
cp ./ip-abuse-checker/grafana/datasources.yml ./grafana/grafana-provisioning/datasources/all.yml





if command -v docker &> /dev/null
then
    if command -v docker-compose &> /dev/null
    then
        echo "building docker images and staring up containers"
        docker-compose build && docker-compose up -d
    else
        echo "docker-compose is not installed. Please install docker-compose to use the dmarcDeamon."
        exit
    fi
else
    echo "docker is not installed. Please install docker to use the dmarcDeamon."
    exit
fi