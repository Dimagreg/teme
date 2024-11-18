#!/bin/bash

# USE FOLLOWING INSTEAD OF THIS SCRIPT:
# LOAD DATA LOCAL INFILE '/home/data/name.basics.tsv' INTO TABLE name_basics.persons;

# SELECT * FROM persons WHERE primaryName LIKE "Amber%"; 7038 rows in set (4.35 sec)
# SELECT * FROM persons WHERE primaryName LIKE "%Cleese%"; 30 rows in set (4.60 sec)

# CREATE INDEX idx_primaryName ON persons(primaryName);
# SELECT * FROM persons WHERE primaryName LIKE "Amber%"; 7038 rows in set (0.08 sec)
# SELECT * FROM persons WHERE primaryName LIKE "%Cleese%"; 30 rows in set (4.44 sec)

# ALTER TABLE persons ADD FULLTEXT INDEX `fulltext_primaryName`(primaryName);
# SELECT * FROM persons WHERE primaryName LIKE "Amber%"; 7038 rows in set (0.13 sec)
# SELECT * FROM persons WHERE primaryName LIKE "%Cleese%"; 30 rows in set (5.59 sec)

# Database credentials
DB_HOST="localhost"
DB_USER="user"
DB_PASS="password"
DB_NAME="name_basics"

# Path to the TSV file
TSV_FILE="/home/data/name.basics.tsv"

# Initialize counter and batch variables
counter=0
batch_size=1000
batch_values=""

# Function to execute the batch insert
execute_batch_insert() {
  if [ -n "$batch_values" ]; then
    mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" 2>/dev/null <<EOF
INSERT INTO persons (nconst, primaryName, birthYear, deathYear, primaryProfession, knownForTitles)
VALUES $batch_values;
EOF
    batch_values=""
  fi
}

# Read the TSV file and insert data into the database
while IFS=$'\t' read -r nconst primaryName birthYear deathYear primaryProfession knownForTitles; do
  # Increment counter
  counter=$((counter + 1))

  # Replace \N with NULL for birthYear and deathYear
  [ "$birthYear" == "\\N" ] && birthYear="NULL" || birthYear="'$birthYear'"
  [ "$deathYear" == "\\N" ] && deathYear="NULL" || deathYear="'$deathYear'"
  
  # Escape single quotes in primaryName
  primaryName=$(echo "$primaryName" | sed "s/'/''/g")
  
  # Convert comma-separated values to JSON arrays
  primaryProfession=$(echo "$primaryProfession" | sed 's/,/","/g')
  primaryProfession='["'"$primaryProfession"'"]'
  
  knownForTitles=$(echo "$knownForTitles" | sed 's/,/","/g')
  knownForTitles='["'"$knownForTitles"'"]'
  
  # Accumulate values for batch insert
  if [ -n "$batch_values" ]; then
    batch_values="$batch_values, ('$nconst', '$primaryName', $birthYear, $deathYear, '$primaryProfession', '$knownForTitles')"
  else
    batch_values="('$nconst', '$primaryName', $birthYear, $deathYear, '$primaryProfession', '$knownForTitles')"
  fi

  # Execute batch insert every 1000 iterations
  if (( counter % batch_size == 0 )); then
    execute_batch_insert
    echo "Processed $counter records..."
  fi
done < "$TSV_FILE"

# Execute any remaining batch insert
execute_batch_insert

# Print final count
echo "Finished processing $counter records."