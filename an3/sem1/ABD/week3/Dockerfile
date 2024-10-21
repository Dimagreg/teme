FROM mysql:latest

WORKDIR app

COPY mysql_config/script.sh app/script.sh
COPY mysql_config/my.cnf /etc/mysql/conf.d/my.cnf

RUN mysql -u root -p dima -e "CREATE DATABASE IF NOT EXISTS studentdb;"

ENTRYPOINT ["sh", "app/script.sh"]