<?php
/*####################################################*\
#   CS5248       NUS School of Computing        2017   #
#                     DASH Project                     #
#                  Task 2: Server side                 #
#                                                      #
#                 By:   Loïc PERACHE                   #
#                       perache@comp.nus.edu           #
#                       A0174362E                      #
\*####################################################*/

/*
* This file is used to return to the client the title, the description
* and the link to the DASH playlist of the videos available on the server
*/

// The results will be return in a JSON
header("Content-Type: application/json; charset=UTF-8");

// We connect to the database to get the informations
$servername = "localhost";
$username = "team05";
$password = "bestteam05!";
$databasename = "team05";
// Create connection
$conn = new mysqli($servername, $username, $password,$databasename);
// Check connection
if ($conn->connect_error) {
   die("The connection failed =/ \n" . $conn->connect_error);
}
$result = $conn->query("SELECT title, description, url_DASH FROM information_table LIMIT 100");
$outp = array();
$outp = $result->fetch_all(MYSQLI_ASSOC);
echo json_encode($outp);
?>
