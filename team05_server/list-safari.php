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
* This file is here to list all the videos available 
* and to provide link to the HLS version playble on safari
*/


/* To get the videos and the links we connect to the database */

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
$result = $conn->query("SELECT title, description, url_HLS FROM information_table LIMIT 100");


/* We then display the results in a table */ 

echo "<table>";
// The title of the 2 columns
echo "<tr><td>Title</td><td>Description</td></tr>";
echo "<tr><td></td><td></td></tr>";
// We loop through the results
while($row = mysqli_fetch_array($result)){
	echo "<tr><td>"; 
	// We hide the link in the title of the videos
	echo "<a href=" . $row['url_HLS'] . ">" . $row['title'] . "</a>";
	echo "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	echo "</td><td>" . $row['description'] . "</td></tr>";
}
echo "</table>";

?>
