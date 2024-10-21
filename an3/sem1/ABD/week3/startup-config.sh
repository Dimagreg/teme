docker run --name mysql-lab-config \
    -e MYSQL_ROOT_PASSWORD=my-secret-pw \
    -v ~/mysql-config/my.cnf:/etc/mysql/conf.d/my.cnf \
    -v /mnt/c/Users/dmitrii.grigori/Documents/teme/an3/sem1/ABD/week3/script.sh:/script.sh \
    -v mysql_data:/var/lib/mysql \
    -d mysql:latest