<?php
	
	
	function PDOQuery($sql, $varArray) {
		include "./creds/creds.php";
		
		$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
		$opt = [
			PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
			PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
			PDO::ATTR_EMULATE_PREPARES   => false
		];
		
		$pdo = new PDO($dsn, $user, $pw, $opt);

		if (count($varArray) == 0) 
		{
			// no variables to bind
			$stmt = $pdo->query($sql);
		} 
		else 
		{
			// bind variables
			$stmt = $pdo->prepare($sql);
			$stmt->execute($varArray);			
		}
		return $stmt->fetchAll(PDO::FETCH_ASSOC);
	}
?>