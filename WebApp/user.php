<?php

include_once "./creds/creds.php";
include_once "./pdo_utils.php";
include_once "./input_util.php";


if ($_SERVER["REQUEST_METHOD"] == "POST")
{
	if (isset($_POST["user_id"])) 
	{
		$user_id = $_POST["user_id"];
		$retArray = PDOQuery('SELECT * FROM user WHERE user_id=?', [$user_id]);
		$myJSON = json_encode($retArray);
		http_response_code(200);
		echo $myJSON;
	}
	
	else if (isset($_POST["username"])) 
	{	
		$username = $_POST["username"];
		$retArray = PDOQuery('SELECT * FROM user WHERE username=?', [$username]);
		$myJSON = json_encode($retArray);
		http_response_code(200);
		echo $myJSON;
	}
	
	else
	{
		http_response_code(404);
	}
}

// Entering PUT Requests
else if ($_SERVER["REQUEST_METHOD"] == "PUT")
{	
    parse_str(file_get_contents("php://input"), $_PUT);
    $_REQUEST = parseInput($_PUT); // Handles the jibberish and gives us a usable array...
	
	$token = test_input($_REQUEST["token"]);
	if (isset($_REQUEST["token"]) && isset($_REQUEST["password"]))
	{
		if ($db_token == $_REQUEST["token"])
		{
		   $hash = password_hash($_REQUEST["password"], PASSWORD_DEFAULT);
		   $username = test_input($_REQUEST["username"]);
	       $username = strtolower($username);
		   $dt = new DateTime();
		   
		   PDOQuery("INSERT INTO user (user_id, user_role_id, username, user_hash, active, last_login) 
		      VALUES (NULL, ?, ?, ?, ?, ?)", [$_REQUEST["user_role_id"], $username, 
		      $hash, $_REQUEST["active"], $dt->format('Y-m-d H:i:s')]);
		   
		   http_response_code(201);
		}
	}
	
	else
	{
	   http_response_code(418);	
	}
} 

// Deleting...
else if ($_SERVER["REQUEST_METHOD"] == "DELETE")
{
	if (isset($_REQUEST["token"]) && isset($_REQUEST["username"]) && isset($_REQUEST["password"]))
	{
	
	   parse_str(file_get_contents("php://input"), $_DELETE);
	   $_REQUEST = parseInput($_DELETE);

	   $username = test_input($_REQUEST["username"]);
	   $username = strtolower($username);
	   
	   // Get information from database...
	   $get_user_info = PDOQuery("SELECT user_hash FROM user WHERE username = ?", [$username]);
	   $user_hash = $get_user_info[0]["user_hash"];
	   
	   // Checking if the passwords match...
	   if (password_verify($_REQUEST["password"], $user_hash)) 
	   {
		  PDOQuery("DELETE FROM user WHERE username = ?", [$_REQUEST["username"]]);
	      http_response_code(204);
       }
       else 
	   {
          http_response_code(418);
       }
	  
	   
	}
	
    else { http_response_code(304);  }
}


// No option found...
else
{
	http_response_code(404);
}


?>