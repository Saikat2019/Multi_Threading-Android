package com.kajal.downloader;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private String pdfUrl = "https://firebasestorage.googleapis.com/v0/b/myblog-18d9c.appspot.com/o/2019.pdf?alt=media&token=2be31c93-6e64-4584-9505-8af188523c60";
    private ProgressBar progressBar;
    private LinearLayout loadingSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.downloadURL);
//        progressBar = findViewById(R.id.downloadProgress);
        loadingSection = findViewById(R.id.loadingSection);

        editText.setText(pdfUrl);
    }


    public void downloadImage(View view){
        String url = editText.getText().toString();
        Thread myThread = new Thread(new DownloadImagesThread(url));
        myThread.start();

    }

    public boolean downloadImageUsingThreads(String url){

        boolean successful = false;
        URL downloadURL = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        File file = null;
        try {
            downloadURL = new URL(url);
            connection = (HttpURLConnection) downloadURL.openConnection();
            inputStream = connection.getInputStream();
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .getAbsolutePath()+"/abcd.pdf");
            fileOutputStream = new FileOutputStream(file);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1){
                Log.d("XXXMainActivity", "0 - "+read);
                fileOutputStream.write(buffer,0,read);
            }
            successful = true;
        } catch (MalformedURLException e) {
            Log.d("XXXMainActivity", "1 - "+e.getMessage());
        } catch (IOException e) {
            Log.d("XXXMainActivity", "2 - "+e.getMessage());
        }
        finally {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingSection.setVisibility(View.GONE);
                }
            });
            if(connection != null){
                connection.disconnect();
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {

                }
            }
        }

        return successful;
    }

    private class DownloadImagesThread implements Runnable {

        private String url;
        public DownloadImagesThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingSection.setVisibility(View.VISIBLE);
                }
            });
            downloadImageUsingThreads(url);
        }
    }

}
