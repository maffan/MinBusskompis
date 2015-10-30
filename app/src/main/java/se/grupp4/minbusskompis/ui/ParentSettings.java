package se.grupp4.minbusskompis.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseInstallation;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;

public class ParentSettings extends AppCompatActivity {

    private static class ViewHolder {
        TextView installId;
    }

    private Button resetButton;
    private Switch soundSwitch;
    private Context context = this;
    protected SharedPreferences sharedPreferences;
    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_settings);
        viewHolder = new ViewHolder();

        //Init views
        viewHolder.installId = (TextView) findViewById(R.id.parent_settings_install_id_textview);

        //Set install id
        viewHolder.installId.setText(ParseInstallation.getCurrentInstallation().getInstallationId());


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
                        .setTitle(R.string.all_reset_dialog_title)
                        .setMessage(R.string.all_reset_dialog_message)
                        .setPositiveButton(R.string.all_dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent intent = new Intent(getApplicationContext(), StartSelectMode.class);
                                new ResetAppTask().doInBackground();
                                ParseCloudManager.getInstance().fetchLatestDataFromCloud(new AsyncTaskCompleteCallback() {
                                    @Override
                                    public void done() {
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.all_dialog_no, null)
                        .show();
            }
        });
    }

    public void addSwitchListener(){
        soundSwitch = (Switch)findViewById(R.id.notification_switch);
        soundSwitch.setChecked(sharedPreferences.getBoolean("soundsetting", true));
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("soundsetting", true).apply();
                    Toast.makeText(getApplicationContext(), R.string.parent_settings_toast_notifications_on, Toast.LENGTH_SHORT).show();
                } else {
                    sharedPreferences.edit().putBoolean("soundsetting", false).apply();
                    Toast.makeText(getApplicationContext(), R.string.parent_settings_toast_notifications_off, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class ResetAppTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ParseCloudManager.getInstance().fetchLatestDataFromCloud(new AsyncTaskCompleteCallback() {
                @Override
                public void done() {
                    resetApp();
                }
            });
            return null;
        }
    }
    //clears information stored in sharedpreferences and parse.
    private void resetApp(){
        sharedPreferences.edit().clear().apply();
        if (!(ParseCloudManager.getInstance() == null)) {
            ParseCloudManager.getInstance().clearParseData();
        }
    }
}