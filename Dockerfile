FROM eclipse-temurin:21.0.1_12-jre-jammy
EXPOSE 8080

RUN groupadd -r elastic && useradd -r -g elastic elastic
USER elastic

WORKDIR /home/elastic
ADD target/es-demo-0.0.1-SNAPSHOT.jar elastic.jar

ENTRYPOINT ["java", "-jar","/home/elastic/elastic.jar"]
