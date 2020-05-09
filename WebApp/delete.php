<?php
$dptID = $_POST['depart_id'];
$sql = 'DELETE FROM department WHERE depart_id='.$dptID.'';

include_once "./creds/creds.php";
include_once "./pdo_utils.php";
include_once "./input_util.php";

try {
    $dbconn = new PDO("mysql:host=$host;dbname=$db",$user,$pw);
    $stmt = $dbconn->prepare($sql);
    $stmt->bindParam(':depart_id', $dptID);
    $stmt->execute();

    echo "Record deleted successfully";
} catch(PDOException $e) {
    print 'Error!: '.$e->getMessage().'<br/>';
}

$db = null;
?>