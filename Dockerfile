FROM openjdk:11.0.5-jre

RUN mkdir -p /opt/http-crawler-spring-boot

WORKDIR "/opt/http-crawler-spring-boot"

COPY ./target/http-crawler-0.0.1-SNAPSHOT.jar /opt/http-crawler-spring-boot

RUN chmod -R o+x /opt/http-crawler-spring-boot

RUN apt-get update -y && apt-get install -y curl

ENTRYPOINT [ "java", "-jar", "/opt/http-crawler-spring-boot/http-crawler-0.0.1-SNAPSHOT.jar" ]