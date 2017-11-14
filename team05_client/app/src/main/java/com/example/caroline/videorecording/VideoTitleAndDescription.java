package com.example.caroline.videorecording;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VideoTitleAndDescription extends AppCompatActivity {

    private TextView titleTextView;
    private TextView desTextView;

    private EditText titleEditText;
    private EditText desEditText;

    private Button enterBtn;

    private String title;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_title_and_description);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        desTextView = (TextView) findViewById(R.id.desTextView);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        desEditText = (EditText) findViewById(R.id.desEditText);

        enterBtn = (Button) findViewById(R.id.enter);

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleEditText.getText().toString();
                description = desEditText.getText().toString();

                Variables.setTitle(title);
                Variables.setDescription(description);

                //Toast.makeText(VideoTitleAndDescription.this, "Title : "+ title + " and Description : " + description, Toast.LENGTH_SHORT).show();

                // we send the information to the server
                SendToServer sendToServer = new SendToServer(Variables.getTitle(),Variables.getDescription(),Variables.getListFilePath(), VideoTitleAndDescription.this);
                sendToServer.execute();

                // return to the main activity
                Intent returnIntent = new Intent(VideoTitleAndDescription.this, MainActivity.class);
                startActivity(returnIntent);
            }
        });
    }

}
