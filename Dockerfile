FROM openjdk:11-jdk

WORKDIR /app

#jar 복사
COPY build/libs/*.jar /app/app.jar

#컨테이너가 실행될 때 명령어 수행
##--로 시작되는 명령행 인수를 spring 환경으로 변환
ENTRYPOINT ["java","-jar","/app/app.jar", "--spring.config.location=app/application.yml"]