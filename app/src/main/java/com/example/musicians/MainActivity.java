package com.example.musicians;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textView;
    Button button;
    ArrayList<AudioModel> songsList=new ArrayList<>();

    @SuppressLint({"MissingInflatedId", "SuspiciousIndentation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recycler_view);
        textView=findViewById(R.id.no_songs_text);


        if(checkPermission()==false){
            requestPermission();
            return;
        }
        String[] projection ={
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };


        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";

        Cursor cursor= getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
        while (cursor.moveToNext()){

            AudioModel songsData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));

            if (new File(songsData.getPath()).exists())
            songsList.add(songsData);
        }
        if(songsList.size()==0){
            textView.setVisibility(View.VISIBLE);
        }else {
           recyclerView.setLayoutManager(new LinearLayoutManager(this));
           recyclerView.setAdapter(new MusicAdapter(songsList,getApplicationContext()));
        }
    }

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result== PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }
    Void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Please allow permission", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);

        return null;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (recyclerView!=null){
            recyclerView.setAdapter(new MusicAdapter(songsList,getApplicationContext()));
        }
    }
}