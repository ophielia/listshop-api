FROM openjdk:17-jdk-slim
RUN addgroup --system listshop && adduser --system listshop --ingroup listshop
USER listshop:listshop
ARG DEPENDENCY=target/dependency
ARG STATIC_FILES=src/main/docker
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

RUN mkdir /home/listshop/tomcat
RUN chmod 777 /home/listshop/tomcat


EXPOSE 4000

USER root
ENTRYPOINT exec java $JAVA_OPTS -cp app:app/lib/* com.meg.listshop.Application
#ENTRYPOINT ["java","-cp","app:app/lib/*", "-Dspring.config.additional-location=" ,"com.meg.listshop.Application"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]