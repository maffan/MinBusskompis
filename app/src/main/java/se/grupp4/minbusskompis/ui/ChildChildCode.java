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
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussParseSyncMessenger;
import se.grupp4.minbusskompis.parsebuss.BussSyncCodeGenerator;
import se.grupp4.minbusskompis.parsebuss.BussSync;
import se.grupp4.minbusskompis.parsebuss.SyncTaskCompleteCallback;

public class ChildChildCode extends AppCompatActivity {

    protected Button nextButton;
    protected Button resetButton;
    protected TextView generatedCode;
    protected Context context = this;

    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_APPEND);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_child_code);
        findViews();
        addButtonListeners();
        generateCode();
    }

    private void findViews() {
        generatedCode = (TextView) findViewById(R.id.child_code_code_textview);
    }

    public void addButtonListeners(){


        nextButton=(Button)findViewById(R.id.child_code_next_button);
        resetButton=(Button)findViewById(R.id.child_code_reset_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChildDestinations.class);
                startActivity(intent);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.all_reset_dialog_title)
                        .setMessage(R.string.all_reset_dialog_message)
                        .setPositiveButton(R.string.all_reset_dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent intent = new Intent(ChildChildCode.this, StartSelectMode.class);
                                new ResetAppTask().doInBackground();
                                BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
                                    @Override
                                    public void done() {
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.all_reset_dialog_no, null)
                        .show();
            }
        });
    }


    private void generateCode() {
        BussSyncCodeGenerator generator = new BussSyncCodeGenerator(4);
        generatedCode.setText(generator.getCode());
        BussSync sync = new BussSync(new BussParseSyncMessenger());
        sync.waitForSync(generator, new SyncTaskCompleteCallback() {
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

    private class ResetAppTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
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
        if (!(BussData.getInstance() == null)) {
            BussData.getInstance().clearParseData();
        }
    }
}
