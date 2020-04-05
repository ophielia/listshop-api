FROM openjdk:8-jdk-alpine
RUN addgroup -S listshop && adduser -S listshop -G listshop
USER listshop:listshop
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.meg.listshop.Application"]