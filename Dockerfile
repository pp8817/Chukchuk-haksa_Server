FROM azul/zulu-openjdk:17-latest
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/app.jar"]