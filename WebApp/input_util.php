<?php


	// For the PUT and DELETE requests...
	function parseInput($requestType)
	{
		foreach ($requestType as $key => $value)
		{
			unset($requestType[$key]);
			$requestType[str_replace('amp;', '', $key)] = $value;
		}

		$_REQUEST = array_merge($_REQUEST, $requestType);
		return $_REQUEST;
	}
	
	function test_input($data) 
	{
	  $data = trim($data);
	  $data = stripslashes($data);
	  $data = htmlspecialchars($data);
	  return $data;
	}
?>