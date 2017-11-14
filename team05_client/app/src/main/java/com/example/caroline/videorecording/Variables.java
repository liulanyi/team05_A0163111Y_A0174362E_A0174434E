package com.example.caroline.videorecording;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by caroline on 17/10/2017.
 */
public class Variables {

    private static String title ;
    private static String description ;

    private static String filePath;
    private static ArrayList<String> listFilePath ;
    private static String workingPath;
    private static String filePathWithoutExt;

    private static String responseUpload = null;
    private static Context context ;

    // Getter and Setter of title
    public static String getTitle(){return title;}
    public static void setTitle(String title){Variables.title = title;}

    // Getter and Setter of description
    public static String getDescription(){return description;}
    public static void setDescription(String description){Variables.description = description;}

    // Getter and Setter of filepath
    public static String getFilePath() {return filePath;}
    public static void setFilePath(String filePath) {Variables.filePath = filePath;}

    // Getter and Setter of listFilepath
    public static ArrayList<String> getListFilePath() {return listFilePath;}
    public static void setListFilePath(ArrayList<String> listFilePath) {Variables.listFilePath = listFilePath;}

    // Getter and Setter of workingPath
    public static String getWorkingPath(){return workingPath;}
    public static void setWorkingPath(String workingPath){Variables.workingPath = workingPath;}

    // Getter and Setter of filePathWithoutExt
    public static String getFilePathWithoutExt(){return filePathWithoutExt;}
    public static void setFilePathWithoutExt(String filePathWithoutExt){Variables.filePathWithoutExt = filePathWithoutExt;}

    // Getter and Setter of responseUpload
    public static String getResponseUpload(){return responseUpload;}
    public static void setResponseUpload(String responseUpload){Variables.responseUpload = responseUpload;}

    // Getter and Setter of context
    public static Context getContext() {return context;}
    public static void setContext(Context context) {Variables.context = context;}

}


