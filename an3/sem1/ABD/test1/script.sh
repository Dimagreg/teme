# Connect to slave 1
docker exec -it mysql-slave1 mysql -uroot -ppassword -e "
CHANGE MASTER TO
  MASTER_HOST='mysql-master',
  MASTER_USER='repl_user',
  MASTER_PASSWORD='repl_pass',
  MASTER_LOG_FILE='mysql-bin.000004',  -- Replace with the actual log file name from SHOW MASTER STATUS
  MASTER_LOG_POS= 932;  -- Replace with the actual position from SHOW MASTER STATUS
START SLAVE;"

# Connect to slave 2 similarly
docker exec -it mysql-slave2 mysql -uroot -ppassword -e "
CHANGE MASTER TO
  MASTER_HOST='mysql-master',
  MASTER_USER='repl_user',
  MASTER_PASSWORD='repl_pass',
  MASTER_LOG_FILE='mysql-bin.000004',  -- Replace with the actual log file name from SHOW MASTER STATUS
  MASTER_LOG_POS= 932;  -- Replace with the actual position from SHOW MASTER STATUS
START SLAVE;"

# Connect to slave 3 similarly
docker exec -it mysql-slave3 mysql -uroot -ppassword -e "
CHANGE MASTER TO
  MASTER_HOST='mysql-master',
  MASTER_USER='repl_user',
  MASTER_PASSWORD='repl_pass',
  MASTER_LOG_FILE='mysql-bin.000004',  -- Replace with the actual log file name from SHOW MASTER STATUS
  MASTER_LOG_POS= 932;  -- Replace with the actual position from SHOW MASTER STATUS
START SLAVE;"