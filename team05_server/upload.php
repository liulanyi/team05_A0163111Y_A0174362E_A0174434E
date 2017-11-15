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
* This file is used to upload the 3 seconds long parts of the video
*/

/* Getting the information about the file and creating the directories */

// Parsing the input file name
$name=basename($_FILES['fileToUpload']['name']);
$slice = explode("-", $name);
$video_ID = $slice[0];
$slice2 = explode(".",$slice[1]);
$part_number=$slice2[0];

// creating the directories
$create_dir="mkdir uploads/".$video_ID;
echo shell_exec($create_dir);
$create_dir_part="mkdir uploads/".$video_ID."/".$part_number;
echo shell_exec($create_dir_part);

// Defining the ouput directory
$target_dir = "uploads/";
$target_file = $target_dir.$video_ID."/".$part_number."/".basename($_FILES["fileToUpload"]["name"]);


/* We now check that the file is as expected  */

$uploadOk = 1;

// Check if file already exists
if (file_exists($target_file)) {
    echo "Sorry, file already exists.<br>";
    $uploadOk = 0;
    }

// Check file size
if ($_FILES["fileToUpload"]["size"] > 6000000) {
    echo "Sorry, your file is too large.<br>";
    $uploadOk = 0;
}

// Allow only mp4 files
$videoFileType = pathinfo($target_file,PATHINFO_EXTENSION);
if($videoFileType != "mp4"){
    echo "Sorry, only MP4 files are allowed.<br>";
    $uploadOk = 0;
}

// Check if $uploadOk is set to 0 by an error
if ($uploadOk == 0) {
    echo "Sorry, your file was not uploaded.<br>";

// if everything is ok, try to upload file
} else {
    if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file)) {
        echo "OK the file ". basename( $_FILES["fileToUpload"]["name"]). " has been uploaded";
    } else {
        echo "Sorry, there was an error uploading your file.<br>";
	$uploadOk = 0;
    }
}

// Adding this upload to the log
$log_file = "log/upload.txt";
$current_log = file_get_contents($log_file);
$time = date("Y-m-d h:i:sa");
$current_log .= $time ." : File ". $name . " status " . $uploadOk ."\n"; 
file_put_contents($log_file,$current_log);


/* Start the computations to do on the video part received */

// Only if the file upload was successful we do the other operations
if ($uploadOk){
	// Connecting to the data base to update its content with this new part
	$servername = "localhost";
	$username = "team05";
	$password = "bestteam05!";
	$databasename = "team05";
	// Create connection
	$conn = new mysqli($servername, $username, $password,$databasename);
	// Check connection
	if ($conn->connect_error) {
	  die("Connection to the data base failed =/" . $conn->connect_error);
	}
//	$destination = "https://monterosa.d2.comp.nus.edu.sg/~team05/" . $target_file;
//	$txt="INSERT INTO `video_table`(`videoID`, `partID`, `partPath`) VALUES (" . $video_ID . "," . $part_number . ",\"" . $destination . "\");";
	$url_HLS = "http://monterosa.d2.comp.nus.edu.sg/~team05/playlist/" . $video_ID . ".m3u8";
	$url_DASH = "http://monterosa.d2.comp.nus.edu.sg/~team05/uploads/" . $video_ID ."/" . $video_ID . "-playlist.mpd";
	// This request is to update the DB with the playlist url 
	$txt = "UPDATE `information_table` SET `url_DASH`= \"" . $url_DASH . "\" WHERE `ID`=" . $video_ID .";";
	$txt .= "UPDATE `information_table` SET `url_HLS`= \"" . $url_HLS . "\" WHERE `ID`=" . $video_ID ; 

	// Executing the 2 previous requests
	if ($conn->multi_query($txt)) {
	  do {
	    if ($result = $conn->store_result()) {
	      while ($row = $result->fetch_row()) {
		printf("%s", $row[0]);
		}
	      $result->free();
	    }
	  } while ($conn->next_result());
	}


	// Creating the different quality videos
	$base_command = "ffmpeg -i ". $target_file . " -vf scale=";
	$base_name = $target_dir."/".$video_ID."/".$part_number."/".$video_ID."-".$part_number;
	$high_command = $base_command . "854x480 " . $base_name . "-high.mp4 &";
	echo shell_exec($high_command);
	$medium_command = $base_command . "640x360 " . $base_name . "-medium.mp4 &";
	echo shell_exec($medium_command);
	$low_command = $base_command . "426x240 " . $base_name . "-low.mp4 &";
	echo shell_exec($low_command);


	// Adding the line in low/medium/high quality 
	add_line_txt_file("low",$video_ID,$part_number);
	add_line_txt_file("medium",$video_ID,$part_number);
	add_line_txt_file("high",$video_ID,$part_number);

	// Create the new video in different quality
	create_video("low",$video_ID);
	create_video("medium",$video_ID);
	create_video("high",$video_ID);

	// Create the new m3u8 playlist in different quality
	create_playlist("low","426x240",$video_ID);
	create_playlist("medium","640x360",$video_ID);
	create_playlist("high","854x480",$video_ID);

	// Separate audio and video to be able to play it using DASH
	separate_audio_video("low",$video_ID);
	separate_audio_video("medium",$video_ID);
	separate_audio_video("high",$video_ID);
	
	// Create the mpd playlist using MP4box 
	$command= "MP4Box -dash 3000 -profile dashavc264:live -bs-switching multi -url-template ";
	$command .= "uploads/$video_ID/onlyvideo-high.mp4#trackID=1:id=vid0:role=vid0:bandwidth=2000000 ";
	$command .= "uploads/$video_ID/onlyvideo-medium.mp4#trackID=7:id=vid1:role=vid1:bandwidth=1000000 ";
        $command .= "uploads/$video_ID/onlyvideo-low.mp4#trackID=3:id=vid2:role=vid2:bandwidth=700000 ";
        $command .= "uploads/$video_ID/audio-high.mp4#trackID=4:id=aud0:role=aud0:bandwidth=128000 ";
        $command .= "uploads/$video_ID/audio-medium.mp4#trackID=5:id=aud1:role=aud1:bandwidth=128000 ";
        $command .= "uploads/$video_ID/audio-low.mp4#trackID=6:id=aud2:role=aud2:bandwidth=64000 -out uploads/$video_ID/$video_ID-playlist.mpd";
	echo shell_exec($command);
}



/* Defining the function used just above */

// Create/Update the m3u8 playlist with the received part
function create_playlist($quality,$resolution,$video_ID){
	$command="ffmpeg -i uploads/".$video_ID."/".$video_ID."-".$quality.".mp4 -profile:v baseline -s ".$resolution." -start_number 0 -hls_time 3 -hls_list_size 0 -f hls uploads/".$video_ID."/". $video_ID . "-" . $quality . ".m3u8";
	echo shell_exec($command);
}

// Add the part in the text file that list the different parts in the right order (not always the upload order)
function add_line_txt_file($quality,$video_ID,$part_number){
	$file = 'uploads/'.$video_ID."/" . $video_ID . '-'.$quality.'.txt';
	$line_i_am_looking_for = $part_number ;
	$lines = file( $file , FILE_IGNORE_NEW_LINES );
	$nb_lines = count($lines);
	// If the line we look for isn't a previousely missing line
	while($line_i_am_looking_for > count($lines)){
		$lines[]="# Still waiting for this part";
	}
	$lines[$line_i_am_looking_for -1] = "file ". $part_number."/".$video_ID."-".$part_number."-".$quality.".mp4";
	$content = implode( "\n", $lines );
	$content .= "\n";
	file_put_contents( $file , $content );
} 

// Creating the different quality of the video requiered
function create_video($quality,$video_ID){
        $command="ffmpeg -f concat -i uploads/".$video_ID."/".$video_ID."-".$quality.".txt -c copy -y uploads/".$video_ID."/".$video_ID."-".$quality.".mp4";
        echo shell_exec($command);
}
//function to separate audio and video content as requested by the DASH client
function separate_audio_video($quality,$video_ID){
	$command = "ffmpeg -y -i uploads/$video_ID/$video_ID-$quality.mp4 -map 0:a uploads/$video_ID/audio-$quality.mp4 -map 0:v uploads/$video_ID/onlyvideo-$quality.mp4";
	echo shell_exec($command);
}

?>
