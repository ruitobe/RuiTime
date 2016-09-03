/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.rui.ruitime.activity;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;


import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.rui.ruitime.*;

import java.io.File;

/**
 * Created by rhuang on 12/7/15.
 * Modified the original source AppUsageStatisticsActivity.java
 */
public class MainActivity extends AppCompatActivity {

    static final String FTP_HOST= "104.43.226.72";
    //static final String FTP_USER = "Rui";
    //static final String FTP_PASS  ="66666666";

    //static final String FTP_HOST= "metropolia-gce03dj6.cloudapp.net";
    static final String FTP_USER = "metro";
    static final String FTP_PASS  ="Welcome123!";
    public void uploadFile(File[] files) {
    //public void uploadFile(File file) {

        FTPClient client = new FTPClient();
        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/RuiTime/";

        try {
            client.connect(FTP_HOST, 21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            //client.changeDirectory("/Share/");

            for (int index = 0; index < files.length; index++) {

                if(files[index].isFile()) {
                    String fileName = files[index].getName();
                    File file = new File(path + fileName);
                    client.upload(file, new FileTransferListener());
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();

            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public class FileTransferListener implements FTPDataTransferListener {
        public void started() {
            //Toast.makeText(getBaseContext(), "Uploading started...", Toast.LENGTH_SHORT).show();
        }

        public void transferred(int length) {
            //Toast.makeText(getBaseContext(), "Transferred...", Toast.LENGTH_SHORT).show();
        }

        public void completed() {
            //Toast.makeText(getBaseContext(), "Transferred...", Toast.LENGTH_SHORT).show();
        }

        public void aborted() {
            //Toast.makeText(getBaseContext(), "Transferring aborted, please try again...", Toast.LENGTH_SHORT).show();

        }

        public void failed() {
            //Toast.makeText(getBaseContext(), "Uploading failed...", Toast.LENGTH_SHORT).show();
        }
    }

    public class UploadFileAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... param) {
            String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/RuiTime/";
            //String filePath = path + "RuiTimeAnalysisData2015-12-15-20-33-29.csv";
            // Get all files under RuiTime
            File allFile = new File(path);
            File[] files = allFile.listFiles();
            uploadFile(files);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.rui.ruitime.R.layout.activity_main);



        // Setup floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement ftp uploading
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                new UploadFileAsync().execute((Void[])null);
                Toast.makeText(getBaseContext(), "Uploading files finished...", Toast.LENGTH_LONG).show();

            }
        });



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(com.rui.ruitime.R.id.container, AppUsageStatisticsFragment.newInstance())
                    .commit();
        }

    }

}
