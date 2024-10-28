<?php
$host = 'db';
$dbname = 'testdb';
$user = 'user';
$pass = 'password';

try {
    echo "This is first page!";
    echo "<br>";

    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $user, $pass);
    echo "Connected to MySQL successfully!";
} catch (PDOException $e) {
    echo "Connection failed: " . $e->getMessage();
}
?>