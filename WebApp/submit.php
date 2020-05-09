<?php
    include_once "./creds/creds.php";
    include_once "./pdo_utils.php";
    include_once "./input_util.php";

    if(isset($_POST['dpt_desc']))
    {
        $db = new PDO("mysql:host=$host;dbname=$db",$user,$pw);
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        
        $insertSQL="INSERT INTO department (depart_name,created_by,depart_identifier,exclude_from_sales) VALUES ('".$_POST["dpt_desc"]."',4,'',0)";
        $stmt = $db->prepare($insertSQL);
        $stmt->execute(array($_POST['dpt_desc']));
        if($stmt){
        echo "You successfully inserted the value " . $_POST['dpt_desc'];
        }
        else {
        echo "Error";
        }
    }
    else {
        echo "You did not submit a value";
    }
?>