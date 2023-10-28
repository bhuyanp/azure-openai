FROM amazoncorretto:17
ARG JARFILE=target/*jar
COPY ${JARFILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]

