package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import se.grupp4.minbusskompis.R;

public class StartSplash extends Activity {

    private static final String TAG = "StartSplash";
    protected SharedPreferences sharedPreferences;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_APPEND);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_splash);

        AsyncTask asTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                //if(sharedPreferences.getBoolean("data_enabled",false)){
                if(true){
                    Log.d(TAG,"Data is enabled, starting application");
                    Intent intent;
                    if(sharedPreferences.getBoolean("setaschild", false)){
                        intent = new Intent(getApplicationContext(), ChildDestinations.class);
                    }
                    else if(sharedPreferences.getBoolean("setasparent", false)){
                        intent = new Intent(getApplicationContext(), ParentChildrenList.class);
                    }
                    else
                        intent = new Intent(getApplicationContext(), StartSelectMode.class);

                    startActivity(intent);
                }
                else{
                    Log.d(TAG,"Data is disabled, calling exit");
                    disabledData();
                }
            }
        };

        asTask.execute();

    }

    @Override
    public void onPause(){
        super.onPause();
        finish();
    }

    private void disabledData(){
        Log.d(TAG, "Data disabled, Toast");
        Toast.makeText(context,"Enable data to start application",Toast.LENGTH_LONG).show();
        AsyncTask mftask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                System.exit(0);
            }
        };
        mftask.execute();
    }
}
