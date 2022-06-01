FROM openjdk:17
COPY ./build/libs /usr/app
COPY ./static-content /usr/app/static-content
WORKDIR /usr/app
#RUN psql -f /src/main/sql/createSchema.sql
ENV JDBC_DATABASE_URL="jdbc:postgresql://ec2-52-30-67-143.eu-west-1.compute.amazonaws.com:5432/d5u0hegbmbgh20?user=gkmxjjgguhrfra&password=55868afffc1b0baad009361c7a2e3728d3e50c01795f1caa7d1636201a36054e"
ENV APP_ENV_TYPE="PROD"
CMD ["java", "-jar", "2122-2-LEIC42D-G04.jar"]