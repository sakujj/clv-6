FROM tomcat:9.0.84-jdk17-temurin-jammy
ENV appName=task-servlets
COPY gradlew build.gradle settings.gradle ./
COPY gradle/ ./gradle/
COPY ./src/ ./src/
RUN ./gradlew war
RUN cp -r ./build/libs/$appName.war ./
RUN jar -xvf $appName.war
RUN mkdir /usr/local/tomcat/webapps/$appName
RUN cp -r ./*-INF /usr/local/tomcat/webapps/$appName
CMD  ["catalina.sh", "run"]
