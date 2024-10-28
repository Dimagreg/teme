<?php
$host = 'db';
$dbname = 'testdb';
$user = 'user';
$pass = 'password';

$redis_port = '6379';
$redis_password = 'password';


try {
    echo "This is second page!";
    echo "<br>";

    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $user, $pass);
    echo "Connected to MySQL successfully!";

    echo "<br>";

    $redis = new Redis();
    //Connecting to Redis
    $redis->connect("redis");
    $redis->auth($redis_password);

    if ($redis->ping()) {
    echo "Connected to Redis successfully!";
    }
} catch (PDOException $e) {
    echo "Connection failed: " . $e->getMessage();
}
?>