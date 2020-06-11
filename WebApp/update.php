<?php

    include_once "./creds/creds.php";
    include_once "./pdo_utils.php";
    include_once "./input_util.php";

    $fieldArray = array();
    $valueArray = array();

    $result = "";

    $table = $_POST['table'];
    $key = $_POST['key'];
    $id = $_POST['id'];

try {

    foreach($_POST as $keyx=>$value)
    {
        if ($keyx != "table" && $keyx != "key" && $keyx != "id")
        {
            array_push($fieldArray, $keyx);
            array_push($valueArray, $value);
        }
    }
    for ($i = 0; $i < count($fieldArray); $i++)
    {
        $result .= $fieldArray[$i]." = '".$valueArray[$i]."'";
        if (count($fieldArray) - $i > 1)
        {
            $result .= ",";
        }
    }
    $sql = "UPDATE ".$table." SET ".$result." WHERE ".$key." = ".$id;
    $dbconn = new PDO("mysql:host=$host;dbname=$db",$user,$pw);
    $stmt = $dbconn->prepare($sql);
    if($stmt->execute($fieldArray)){
        echo "Record updated successfully"."\n";
        echo $sql;
    }
    else{
        echo "Record update failed";
        echo $sql;
    }
}
catch(PDOException $e)
{
    print 'Error!: '.$e->getMessage().'<br/>';
}

$dbconn = null;
?>