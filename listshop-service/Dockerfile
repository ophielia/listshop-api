FROM openjdk:11.0.7-jre-slim
RUN addgroup --system listshop && adduser --system listshop --ingroup listshop
USER listshop:listshop
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","Application"]