FROM openjdk:11-jdk

WORKDIR /app

COPY build/libs/*.jar /app/app.jar

#컨테이너가 실행될 때 명령어 수행
ENTRYPOINT ["java","-jar","/app/app.jar"]