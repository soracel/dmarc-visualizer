FROM openjdk:17-jdk-slim
VOLUME /tmp

ARG JAR_FILE_AbuseChecker
COPY ${JAR_FILE_AbuseChecker} /ip-abuse-checker/app.jar
ADD ../ip-abuse-checker/run-abuseChecker.sh /ip-abuse-checker/run-abuseChecker.sh
RUN chmod +x /ip-abuse-checker/run-abuseChecker.sh

RUN apt update
RUN apt install cron -y

RUN crontab -l | { cat; echo "* */1 * * * /bin/bash /ip-abuse-checker/run-abuseChecker.sh >> /ip-abuse-checker/crontab.log"; } | crontab -
RUN service cron start

ARG JAR_FILE_API
COPY ${JAR_FILE_API} /api/app.jar
ENTRYPOINT ["/bin/bash","-c","java -jar /api/app.jar; cron"]