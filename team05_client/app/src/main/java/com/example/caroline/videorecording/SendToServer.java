package com.example.caroline.videorecording;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by caroline on 17/10/2017.
 */
public class SendToServer extends AsyncTask<Void, String, String> {

    private String title;
    private String description ;

    private int numberOfSegments;
    private int numberOfSegmentsUploaded;


    private Context context;

    private ArrayList<String> listFilePath;



    private String URLnew = "http://monterosa.d2.comp.nus.edu.sg/~team05/new.php"; // POST, Json (title + descritpion)

    private String URLupload = "http://monterosa.d2.comp.nus.edu.sg/~team05/upload.php"; //

    public boolean hasResponseChanged = false ;

    private String id ;

    public SendToServer(String title, String description, ArrayList<String> listFilePath, Context context){
        this.title=title;
        this.description=description;
        this.listFilePath = listFilePath;
        this.context = context;
    }


    @Override
    protected void onProgressUpdate(String... string){
        Toast.makeText(context, string[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String string){
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected String doInBackground(Void... arg0) {
        numberOfSegmentsUploaded = 0;
        // try to connect to the server
        deleteTempFiles(Variables.getFilePath());
        id = POST(URLnew, title, description);

        if (id == null){
            Variables.setResponseUpload("Sorry, your file has not been uploaded. Please check your wifi connection");
        }

        //String responseUploadOk = "OK" ;


        numberOfSegments = listFilePath.size();
        System.out.println("NNNNNNNNNNNNNNNNN " + numberOfSegments);

        // rename the videos
        renameAllSegment(id, numberOfSegments);

        for (int i=numberOfSegmentsUploaded; i<numberOfSegments; i++){

            System.out.println("LLLLLLLLLLLLL " + listFilePath);

            String response = POST(URLupload, listFilePath.get(i));
            System.out.println("RESPONSEEEEEEE" + " : " + response);
            if (response.startsWith("OK") || response.startsWith("Sorry, file already exists")){
                //listFilePathUpload.add(copy.getAbsolutePath());
                deleteTempFiles(listFilePath.get(i));
                //System.out.println("liiisttt : " + listFilePathUpload);
                numberOfSegmentsUploaded +=1;
                publishProgress("segment "+(i+1)+" has been uploaded");
                //Toast.makeText(context, "segment "+i+" has been uploaded",Toast.LENGTH_SHORT).show();
            }
            else {
                publishProgress("segment " + (i + 1) + " has not been uploaded");
                i--;
                try {
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                continue;
                //Toast.makeText(context, "segment "+i+" has not been uploaded",Toast.LENGTH_SHORT).show();
            }
            /*String[] list = response.split(" ");
            String responseServer = list[0];
            System.out.println("RESPONSEEEEEEE" + " : " + responseServer);
*/
            // if one segment has not been uploaded well
            //if (responseServer != responseUploadOk){
            //    setHasResponseChanged(true);
            //}

        }
        if (numberOfSegmentsUploaded==numberOfSegments){
            //publishProgress("Your file has been uploaded");
            Variables.setResponseUpload("Your file has been uploaded");
        } else {
            //publishProgress("Sorry, your file has not been uploaded. Please check your wifi connection");
            Variables.setResponseUpload("Sorry, your file has not been uploaded. Please check your wifi connection and send ");
        }

        //deleteTempFiles(Variables.getWorkingPath());

        //Variables.setSending(false);

        //Toast.makeText(context, Variables.getResponseUpload(),Toast.LENGTH_SHORT).show();
        return Variables.getResponseUpload();
    }

    private void renameAllSegment (String id, int numberOfSegments){
        for (int i=0 ; i<numberOfSegments; i++){
            File copy = null ;
            try {
                System.out.println("LLLLLLLLLLLLL " + listFilePath);
                System.out.println(listFilePath.get(i));
                copy = exportFile(listFilePath.get(i), Variables.getWorkingPath()+ "/" + id + "-" + (i+1) + ".mp4");
                deleteTempFiles(listFilePath.get(i));
                //System.out.println(copy.getPath());

            }catch (Exception e){
                e.printStackTrace();
            }
            listFilePath.set(i, Variables.getWorkingPath() + "/" + id + "-" + (i + 1) + ".mp4");
        }
    }

    private File exportFile(String src, String dst) throws IOException {

        File expFile = new File(dst);
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(expFile).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }

        return expFile;
    }

    public static String POST(String urlString, String title, String description) {
        String id = "";
        String inputLine;

        HttpURLConnection connectionNew = null;
        try {
            URL url = new URL(urlString);
            connectionNew = (HttpURLConnection) url.openConnection();
            connectionNew.setRequestMethod("POST");
            connectionNew.setDoOutput(true);

            connectionNew.setRequestProperty("Content-Type", "application/json"); // charset=UTF-8");

            // create jsonObject to send title and description
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", title);
            jsonObject.put("description", description);

            System.out.println("JSONNNN : " + jsonObject.toString());

            OutputStream outputStream = connectionNew.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            InputStreamReader in = new InputStreamReader(connectionNew.getInputStream());
            BufferedReader reader = new BufferedReader(in);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            //Close our InputStream and Buffered reader
            reader.close();
            in.close();

            //connectionNew.disconnect();
            //Set our result equal to our stringBuilder
            id = stringBuilder.toString();
            System.out.println("IIDDDDDD " + id);

            return id;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }finally {
            if(connectionNew != null) // Make sure the connection is not null.
                connectionNew.disconnect();
        }

        return id;
    }

    public static String POST(String urlString, String filePath){
        String result = "";
        String inputLine;

        //String filePath = Variables.getWorkingPath() + "/"+ fileName ;
        File file = new File(filePath);
        DataOutputStream dos = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        HttpURLConnection connectionUpLoad = null;

        int serverResponseCode;

        /*if (!file.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }*/

        try{

            FileInputStream inputStream = new FileInputStream(file);

            URL url = new URL(urlString);
            connectionUpLoad = (HttpURLConnection) url.openConnection();
            connectionUpLoad.setRequestMethod("POST");
            connectionUpLoad.setDoOutput(true);
            connectionUpLoad.setDoInput(true);

            connectionUpLoad.setRequestProperty("Connection", "Keep-Alive");
            connectionUpLoad.setRequestProperty("ENCTYPE", "multipart/form-data");
            connectionUpLoad.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connectionUpLoad.setRequestProperty("fileToUpload",filePath);

            //OutputStreamWriter out = new OutputStreamWriter(connectionUpLoad.getOutputStream());
            //out.write(filePath); // TODO Maybe to change

            //OutputStream out = connectionUpLoad.getOutputStream();
            //out.write(file.getBytes());

            dos = new DataOutputStream(connectionUpLoad.getOutputStream());


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"fileToUpload\";filename=\"" + filePath + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = inputStream.available();
            //selecting the buffer size as minimum of available bytes or 1 MB
            bufferSize = Math.min(bytesAvailable,maxBufferSize);
            //setting the buffer as byte array of size of bufferSize
            buffer = new byte[bufferSize];

            //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
            bytesRead = inputStream.read(buffer,0,bufferSize);

            //loop repeats till bytesRead = -1, i.e., no bytes are left to read
            while (bytesRead > 0){
                //write the bytes read from inputstream
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = inputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                bytesRead = inputStream.read(buffer,0,bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = connectionUpLoad.getResponseCode();

            inputStream.close();
            dos.flush();
            dos.close();


            if (serverResponseCode==200){
                InputStreamReader in = new InputStreamReader(connectionUpLoad.getInputStream());
                BufferedReader reader = new BufferedReader(in);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }

                //Close our InputStream and Buffered reader
                reader.close();
                in.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();

                connectionUpLoad.disconnect();


                return result ;
            }
            else {
                return "could not upload";
            }

        }catch (Exception e){
            e.printStackTrace();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }finally {
            if(connectionUpLoad != null) // Make sure the connection is not null.
                connectionUpLoad.disconnect();
        }

        return result;
    }

    private void deleteTempFiles(String filePath){
        File file = new File(filePath);
        //File[] files = root.listFiles();
        if (file != null){
            file.delete();
        }
    }
}
