package se.grupp4.minbusskompis.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;
import se.grupp4.minbusskompis.parsebuss.BussParseSyncMessenger;
import se.grupp4.minbusskompis.parsebuss.BussSyncCodeGenerator;
import se.grupp4.minbusskompis.parsebuss.BussSyncer;
import se.grupp4.minbusskompis.parsebuss.SyncTaskCompleteCallback;

/*
    ChildChildCode
    Settings menu for children, used for matching devices
    Generates ChildCode
 */
public class ChildChildCode extends AppCompatActivity {

    protected Context context = this;
    protected SharedPreferences sharedPreferences;
    private ViewHolder viewHolder;

    private static class ViewHolder{
        Button nextButton;
        Button resetButton;
        TextView generatedCode;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_child_code);
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_APPEND);
        viewHolder = new ViewHolder();
        initViews();
        addButtonListeners();
        generateCode();
    }

    private void initViews() {
        viewHolder.generatedCode = (TextView) findViewById(R.id.child_code_code_textview);
        viewHolder.nextButton = (Button)findViewById(R.id.child_code_next_button);
        viewHolder.resetButton = (Button)findViewById(R.id.child_code_reset_button);
    }

    /**
     * Add button listeners, on reset button a confirm diaog is shown
     */
    public void addButtonListeners(){
        viewHolder.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChildDestinations.class);
                startActivity(intent);
            }
        });

        viewHolder.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.all_reset_dialog_title)
                        .setMessage(R.string.all_reset_dialog_message)
                        .setPositiveButton(R.string.all_dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent intent = new Intent(ChildChildCode.this, StartSelectMode.class);
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


    /**
     * Generates a 4 number sync key
     */
    private void generateCode() {
        BussSyncCodeGenerator generator = new BussSyncCodeGenerator(4);
        viewHolder.generatedCode.setText(generator.getCode());
        BussSyncer sync = new BussSyncer(new BussParseSyncMessenger());
        sync.waitForSyncRequest(generator, new SyncTaskCompleteCallback() {
            @Override
            public void onSyncTaskComplete(boolean success, String installationId) {
                if (success) {
                    Toast.makeText(context, R.string.child_code_toast_text_received + installationId, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChildChildCode.this, ChildDestinations.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, R.string.child_code_toast_text_notreceived, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Makes a full reset, deletes data from Parse, resets phone settings
     */
    private void resetApp(){
        sharedPreferences.edit().clear().apply();
        if (!(ParseCloudManager.getInstance() == null)) {
            ParseCloudManager.getInstance().clearParseData();
        }
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

}
