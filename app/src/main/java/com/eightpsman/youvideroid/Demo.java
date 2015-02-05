package com.eightpsman.youvideroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eightpsman.youvider.EncodedStream;
import com.eightpsman.youvider.Utils;
import com.eightpsman.youvider.Youvider;
import com.eightpsman.youvider.YouviderInfo;

import java.io.File;
import java.io.IOException;


public class Demo extends ActionBarActivity implements View.OnClickListener{

    EditText linkEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        linkEditor = (EditText) findViewById(R.id.editor_link);

        findViewById(R.id.action_download).setOnClickListener(this);
    }

    private void onActionDownload(){
        String link = linkEditor.getText().toString();
        if (TextUtils.isEmpty(link)){
            showMessage("Please input video link");
            return ;
        }
        onDownloadLink(link);
    }

    private void onDownloadLink(String link){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting video info ...");
        progressDialog.show();
        new AsyncTask<String, Void, YouviderInfo>() {
            @Override
            protected YouviderInfo doInBackground(String... links) {
                try {
                    return Youvider.getVideoInfo(links[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(YouviderInfo youviderInfo) {
                super.onPostExecute(youviderInfo);
                if (youviderInfo == null){
                    showMessage("Could not parse video info");
                    return;
                }
                if (youviderInfo.encodedStreams == null || youviderInfo.encodedStreams.size() == 0){
                    showMessage("Could not parse video stream");
                    return;
                }
                progressDialog.hide();
                onReceiveDownloadInfo(youviderInfo);
            }
        }.execute(link);
    }

    private void onReceiveDownloadInfo(final YouviderInfo info){
        String[] streams = new String[info.encodedStreams.size()];
        for (int i=0; i<info.encodedStreams.size(); i++)
            streams[i] = info.encodedStreams.get(i).itag.toString();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Download video")
                .setItems(streams, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        showMessage("Choose " + position);
                        EncodedStream stream = info.encodedStreams.get(position);
                        String fileName = info.videoTitle == null ? "Unknown title video" : Utils.getSafeFileNameFor(info.videoTitle);
                        fileName += "." + stream.itag.format.toLowerCase();
                        String path = getExternalStoragePath() + File.separator + fileName;
                        onChooseDownloadVideo(stream, path);
                    }
                })
                .create();
        dialog.show();
    }

    private void onChooseDownloadVideo(final EncodedStream stream, final String path){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Downloading...");
        progressDialog.setMax(100);
        progressDialog.show();

        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return Youvider.downloadEncodedStream(stream, path, new Youvider.OnDownloadingProgress() {
                    @Override
                    public void onDownloadingProgress(int percent) {
                        publishProgress(percent);
                    }
                });
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressDialog.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                progressDialog.hide();
                if (result){
                    showMessage("Video saved: " + path);
                }else{
                    showMessage("Error downloading video");
                }
            }
        }.execute();

    }

    public static String getExternalStoragePath(){
        String path = null;
        try{
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return path;
    }

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.action_download){
            onActionDownload();
        }
    }
}
