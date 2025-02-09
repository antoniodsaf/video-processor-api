FROM amazoncorretto:21-alpine-jdk

RUN apk update 

WORKDIR /app

RUN apk update && \
    apk add --no-cache curl && \
    apk add --no-cache ffmpeg

COPY build/libs/video-processor-rest-api.jar video-processor-rest-api.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "video-processor-rest-api.jar"]