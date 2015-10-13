package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import se.grupp4.minbusskompis.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    Intent intent;
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
