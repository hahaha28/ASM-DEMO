package com.example.asmdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

//    private HashMap<String, Long> ASM_lastClickTimeRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        Button button = findViewById(R.id.button);

        textView.setOnClickListener(v -> {
//            if(ASM_lastClickTimeRecorder == null){
//                ASM_lastClickTimeRecorder = new HashMap<>();
//            }
//            Long ASM_lastClickTime = ASM_lastClickTimeRecorder.get("key");
//            ASM_lastClickTimeRecorder.put("key", System.currentTimeMillis());
//            if (ASM_lastClickTime == null) {
//                ASM_lastClickTime = 0L;
//            }
//            if (System.currentTimeMillis() - ASM_lastClickTime < 1000L) {
//                return;
//            }
//
//            Log.e("tag", "click");
//            Log.e("tag", "click2");
//            Log.e("tag", "click3");
//            Log.e("tag", "click4");
//            Log.e("tag", "click5");
//            Log.e("tag", "click6");


        });

        button.setOnClickListener(v -> {

            Log.e("tag", "click button");

        });


    }

    public void  test(View v){
        Log.e("tag","test");
    }


}