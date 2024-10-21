#!/bin/bash

# MySQL credentials
DB_NAME="studentdb"
TABLE_NAME="students"
USER="root"
PASSWORD="my-secret-pw"
HOST="localhost"

# Function to generate random names
generate_random_name() {
  local NAMES=("Alice" "Bob" "Charlie" "David" "Eve" "Frank" "Grace" "Hank" "Ivy" "John")
  echo "${NAMES[$RANDOM % ${#NAMES[@]}]}"
}

# Insert records into the MySQL table
insert_records() {
  echo "Inserting 1 million records into the table '$TABLE_NAME'..."

  for i in {1..1000000}; do
    # Generate random name and age
    NAME=$(generate_random_name)
    AGE=$((RANDOM % 50 + 18)) # Random age between 18 and 67

    # Insert into MySQL table
    mysql -h $HOST -u $USER -p$PASSWORD $DB_NAME -e \
    "INSERT INTO $TABLE_NAME (name, age) VALUES ('$NAME', $AGE);" > /dev/null 2>&1

    # Show progress every 10000 records
    if (( $i % 10000 == 0 )); then
      echo "Inserted $i records..."
    fi
  done

  echo "Completed inserting 1 million records!"
}

# Check if table exists, if not create it
create_table_if_not_exists() {
  TABLE_EXISTS=$(mysql -h $HOST -u $USER -p$PASSWORD $DB_NAME -e \
  "SHOW TABLES LIKE '$TABLE_NAME';" | grep "$TABLE_NAME")

  if [ -z "$TABLE_EXISTS" ]; then
    echo "Creating table '$TABLE_NAME'..."
    mysql -h $HOST -u $USER -p$PASSWORD $DB_NAME -e "
    CREATE TABLE $TABLE_NAME (
      id INT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(100),
      age INT
    );"
  else
    echo "Table '$TABLE_NAME' already exists."
  fi
}

# Main script execution
create_table_if_not_exists
insert_records