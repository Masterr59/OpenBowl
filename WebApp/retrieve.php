<?php
$resultset = array();
$dptID = $_POST['depart_id'];
$sql = 'SELECT * FROM department WHERE depart_id='.$dptID.'';

include_once "./creds/creds.php";
include_once "./pdo_utils.php";
include_once "./input_util.php";

try{
    $dbconn = new PDO("mysql:host=$host;dbname=$db",$user,$pw);
    $stmt = $dbconn->prepare($sql);
    $stmt->bindParam(':depart_id', $dptID);
    $stmt->execute();

    while($row = $stmt->fetch(PDO::FETCH_ASSOC))
        $resultset[] = $row;

    $resultset = json_encode($resultset);
    print_r($resultset);

} catch(PDOException $e){
    print 'Error!: '.$e->getMessage().'<br/>';
}

$db = null;
?>