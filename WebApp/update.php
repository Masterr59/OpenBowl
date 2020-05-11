<?php

$parameters = array(
                    ':depart_id' => $_POST['depart_id'],
                    ':depart_name' => $_POST['depart_name'],
                    ':depart_identifier' => $_POST['depart_identifier'],
                    ':exclude_from_sales' => $_POST['exclude_from_sales']
                    );

$sql = "UPDATE department SET depart_name = :depart_name, depart_identifier = :depart_identifier, exclude_from_sales = :exclude_from_sales WHERE depart_id = :depart_id";

include_once "./creds/creds.php";
include_once "./pdo_utils.php";
include_once "./input_util.php";

try
{
    $dbconn = new PDO("mysql:host=$host;dbname=$db",$user,$pw);
    $stmt = $dbconn->prepare($sql);
    if($stmt->execute($parameters)){
        echo "Record updated successfully";
    }
    else{
        echo "Record update failed";
    }
}
catch(PDOException $e)
{
    print 'Error!: '.$e->getMessage().'<br/>';
}

$dbconn = null;
?>