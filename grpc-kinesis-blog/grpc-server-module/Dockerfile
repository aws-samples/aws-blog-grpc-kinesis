FROM openjdk:8-jre-slim
COPY ./grpc-server-module-1.0-SNAPSHOT-jar-with-dependencies.jar ./grpc-streaming.jar
ENTRYPOINT java -jar ./grpc-streaming.jar
EXPOSE 9090/udp
EXPOSE 9090/tcp
