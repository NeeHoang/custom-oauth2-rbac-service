FROM postgres:15
COPY src/main/resources/schema.sql /docker-entrypoint-initdb.d/schema.sql