<?php
    require_once('officepages/dbconn.php');
    $sth = $dbconn->prepare("SELECT depart_name FROM department");
    $sth->execute();

    $row = $sth->fetch(PDO::FETCH_ASSOC);
    $dbconn = null; 
    echo "<select name='id'>";

    if($sth->rowCount()):
      while($row = $sth->fetch(PDO::FETCH_ASSOC)){ ?>
          <option value="test1">test subdptment 1</option>
          <?php } ?>
        <?php endif; ?><?php
    echo "</select>";
?> 
<p>yo</p>