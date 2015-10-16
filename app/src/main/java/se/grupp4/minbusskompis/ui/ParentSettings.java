package se.grupp4.minbusskompis.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.api.BusData;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;

public class ParentSettings extends AppCompatActivity {

    private Button resetButton;
    private Switch soundSwitch;
    private Context context = this;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_settings);
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_APPEND);
        addButtonListener();
        addSwitchListener();
    }

    public void addButtonListener(){

        resetButton=(Button) findViewById(R.id.parent_reset_button);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Reset")
                        .setMessage("This will reset your app, and you will lose all connections" +
                                "do you wish to continue?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                sharedPreferences.edit().clear().apply();
                                if (!(BussData.getInstance() == null)) {
                                    BussData.getInstance().clearParseData();
                                }
                                BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
                                    @Override
                                    public void done() {
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
    }

    public void addSwitchListener(){

        soundSwitch = (Switch)findViewById(R.id.sound_switch);
        soundSwitch.setChecked(sharedPreferences.getBoolean("soundsetting", true));
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPreferences.edit().putBoolean("soundsetting", true).apply();
                    Toast.makeText(getApplicationContext(), "Sound is ON", Toast.LENGTH_SHORT).show();
                }else{
                    sharedPreferences.edit().putBoolean("soundsetting", false).apply();
                    Toast.makeText(getApplicationContext(), "Sound is OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
