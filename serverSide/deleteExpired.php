<?php
include_once "db_connect.php";

//this must be run in chrontab
//something like this:
//sudo crontab -e
// 0 * * * * php /var/www/android/project/deleteExpired.php
$deleteCoords = $databse->prepare('
delete from markers where
hour(current_time()) >= finishHour and
minute(current_time()) >= finishMinute;
');
$deleteCoords->execute();
$deleteCoords->closeCursor();
?>
