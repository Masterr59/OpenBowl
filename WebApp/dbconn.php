<?php
include_once "creds/creds.php";
include_once "pdo_utils.php";
include_once "input_util.php";
try {
    $dbconn = new PDO("mysql:host=$host;dbname=$db",$user,$pw);
    
} catch (PDOException $e) {
    print "Error!: " . $e->getMessage() . "<br/>";
    die();
}
?>