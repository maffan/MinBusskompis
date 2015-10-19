package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import se.grupp4.minbusskompis.R;

public class StartSplash extends Activity {

    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_APPEND);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent intent;
                    if(sharedPreferences.contains("setaschild")){
                        intent = new Intent(getApplicationContext(), ChildDestinations.class);
                    }
                    else if(sharedPreferences.contains("setasparent")){
                        intent = new Intent(getApplicationContext(), ParentChildrenList.class);
                    }
                    else
                        intent = new Intent(getApplicationContext(), StartSelectMode.class);

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
