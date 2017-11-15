============>>>>> Team 05 streaming server code <<<<<============

This server code includes the following files:

 -A "new.php" file that is called by the uploading client
  to add a new video on the server (given a title & description)

 -A "upload.php" file that is used to upload each 3 seconds
  long parts of the video

 -A "list-safari.php" that is used to get all the video uploaded
  on the server and play them if the web browser is safari

 -A "video-info-DASH.php" file that is called by the player
  client to get the videos available and the links to the
  DASH playlists

 -An "information_table.sql" that can generate the database
  required to keep track of the video that are uploaded on the
  server

 -A "home" file which is an html file giving more informations
  about all the files available on the server


NB: All file available on the server are not included here as
they are not necessary for the project, or were just here for
debugging purposes

