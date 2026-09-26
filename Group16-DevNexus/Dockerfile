FROM mysql/mysql-server:latest
LABEL authors="Han Htal Sein"
WORKDIR /tmp
COPY test_db.sql /docker-entrypoint-initdb.d
COPY user.sql /docker-entrypoint-initdb.d
ENV MYSQL_ROOT_PASSWORD root
