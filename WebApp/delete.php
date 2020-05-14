<?php
$table = $_POST['table'];
$key = $_POST['key'];
$id = $_POST['id'];

include_once "./creds/creds.php";
include_once "./pdo_utils.php";
include_once "./input_util.php";

try {
    $sql = 'DELETE FROM '.$table.' WHERE '.$key.'='.$id;
    echo $sql."\n";
    $dbconn = new PDO("mysql:host=$host;dbname=$db",$user,$pw);
    $stmt = $dbconn->prepare($sql);
    $stmt->bindParam(':'.$key, $id);
    $stmt->execute();
} catch(PDOException $e) {
    print 'Error!: '.$e->getMessage().'<br/>';
}

$db = null;
?>