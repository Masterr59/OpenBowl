<?php

    include_once "./creds/creds.php";
    include_once "./pdo_utils.php";
    include_once "./input_util.php";

    $fieldArray = array();
    $valueArray = array();

    $fields = "";
    $values = "";

    $table = $_POST['table'];

try {
    
    foreach($_POST as $key=>$value)
    {
        if ($key != "table")
        {
            if ($value != "")
            {
                array_push($fieldArray, $key);
                array_push($valueArray, $value);
            }
        }
    }
    for ($i = 0; $i < count($fieldArray); $i++)
    {
        if ($valueArray[$i].is_string(true))
        {
            $values .= "'".$valueArray[$i]."'";
        }
        else
        {
            $values .= $valueArray[$i];
        }
        $fields .= $fieldArray[$i];
        if (count($fieldArray) - $i > 1)
        {
            $fields .= ",";
            $values .= ",";
        }
    }

    $sql="INSERT INTO ".$table." (".$fields.") VALUES (".$values.")";
    echo $sql."\n";

    $db = new PDO("mysql:host=$host;dbname=$db",$user,$pw);
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $stmt = $db->prepare($sql);

    if ($stmt->execute($fieldArray))
    {
        echo "You successfully inserted a new record into ".$table;
    }
    else {
        echo "An error occured when insert a new record into ".$table;
    }
}
catch (PDOException $e) {
    print 'Error!: '.$e->getMessage().'<br/>';
}

?>