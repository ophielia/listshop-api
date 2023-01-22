FROM openjdk:11.0.7-jre-slim
RUN addgroup --system listshop && adduser --system listshop --ingroup listshop
USER listshop:listshop
ARG DEPENDENCY=target/dependency
ARG STATIC_FILES=src/main/docker
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

COPY ${STATIC_FILES}/celebrations.json /sidecar/static

RUN mkdir /home/listshop/tomcat
RUN chmod 777 /home/listshop/tomcat


EXPOSE 4000

USER root
ENTRYPOINT ["java","-cp","app:app/lib/*","com.meg.listshop.Application"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]