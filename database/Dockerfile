FROM postgres:15.4-alpine3.18

ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD postgres
ENV POSTGRES_DB sakujj_db

COPY ddl.sql dml1.sql /docker-entrypoint-initdb.d/