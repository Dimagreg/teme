#!/bin/bash

# MySQL credentials
DB_NAME="studentdb"
TABLE_NAME="students"
USER="root"
PASSWORD="my-secret-pw"
HOST="localhost"

# Number of rows per batch insert
BATCH_SIZE=5000
TOTAL_ROWS=1000000

# Function to generate random names
generate_random_name() {
  local NAMES=("Alice" "Bob" "Charlie" "David" "Eve" "Frank" "Grace" "Hank" "Ivy" "John")
  echo "${NAMES[$RANDOM % ${#NAMES[@]}]}"
}

# Generate records in batches and insert them in bulk
insert_records_in_batches() {
  echo "Inserting $TOTAL_ROWS records into the table '$TABLE_NAME' in batches of $BATCH_SIZE..."

  for ((start=1; start<=TOTAL_ROWS; start+=BATCH_SIZE)); do
    SQL="INSERT INTO $TABLE_NAME (name, age) VALUES "
    # Generate batch of records
    for ((i=0; i<BATCH_SIZE && start+i<=TOTAL_ROWS; i++)); do
      NAME=$(generate_random_name)
      AGE=$((RANDOM % 50 + 18)) # Random age between 18 and 67

      # Add the record to the SQL statement
      SQL+="('$NAME', $AGE)"
      # Add comma for all except the last record of the batch
      if (( i < BATCH_SIZE - 1 && start + i < TOTAL_ROWS )); then
        SQL+=", "
      fi
    done

    # Finalize the batch insert SQL
    SQL+=";"

    # Execute the SQL for the current batch
    mysql -h $HOST -u $USER -p$PASSWORD $DB_NAME -e "$SQL" 2>> error.log

    # Show progress every 100,000 records
    if (( start % 100000 == 1 )); then
      echo "Inserted $((start + BATCH_SIZE - 1)) records..."
    fi
  done

  echo "Completed inserting $TOTAL_ROWS records!"
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
insert_records_in_batches
