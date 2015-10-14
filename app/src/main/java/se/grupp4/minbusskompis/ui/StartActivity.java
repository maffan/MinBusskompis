package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import se.grupp4.minbusskompis.R;

public class StartActivity extends Activity {

    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_APPEND);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent intent;/*
                    if(sharedPreferences.contains("setaschild") && sharedPreferences.contains("hasparent")){
                        intent = new Intent(StartActivity.this, ChildDestinations.class);
                    }
                    else if(sharedPreferences.contains("setaschild")){
                        intent = new Intent(StartActivity.this, ChildChildCode.class);
                    }
                    else if(sharedPreferences.contains("setasparent")){
                        intent = new Intent(StartActivity.this, ParentChildrenList.class);
                    }
                    else*/
                        intent = new Intent(StartActivity.this, MainActivity.class);

                    startActivity(intent);
                }
            }
        };

        timerThread.start();

    }

    @Override
    public void onPause(){
        super.onPause();
        finish();
    }
}
