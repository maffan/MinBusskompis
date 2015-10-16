package se.grupp4.minbusskompis.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.BussData;

/**
 * Created by Tobias on 2015-10-13.
 */
public class ParentChildSettings extends AppCompatActivity{

    private EditText nameEdit;
    private String currentInstallationId;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_settings);
        this.context = this;

        Intent intent = getIntent();
        currentInstallationId = intent.getStringExtra("child_id");

        //Set default name
        nameEdit = (EditText) findViewById(R.id.parent_child_settings_name);
        nameEdit.setText(BussData.getInstance().getNameFromId(currentInstallationId));

        //Set installation id
        TextView instId = (TextView) findViewById(R.id.parent_child_settings_installationid);
        instId.setText(currentInstallationId);

        //Set save name button
        Button saveNameButton = (Button) findViewById(R.id.parent_child_settings_savename_button);
        saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeNameTask().execute();
            }
        });

        //Set destinations button
        Button destinationsButton = (Button) findViewById(R.id.parent_child_settings_destinations_button);
        destinationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentChildSettings.this,ParentChildDestinations.class);
                intent.putExtra("child_id", currentInstallationId);
                startActivity(intent);
            }
        });

        Button deleteChildButton = (Button) findViewById(R.id.parent_child_settings_delete_child_button);
        deleteChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove child")
                        .setMessage("Do you really want to remove this child?")
                        .setPositiveButton("Hells yes!", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                BussData.getInstance().removeRelationship(currentInstallationId,BussData.CHILD);
                                Intent intent = new Intent(getApplicationContext(),ParentChildrenList.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No way!", null)
                        .show();
            }
        });

        Button saveAndExitButton = (Button) findViewById(R.id.parent_child_settings_exit_child_button);
        saveAndExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ParentChildrenList.class);
                startActivity(intent);
            }
        });
    }

    private void changeName() {
        String newName = nameEdit.getText().toString();
        BussData.getInstance().setNameForId(newName, currentInstallationId);

    }

    private class ChangeNameTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            changeName();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ParentChildSettings.this, "New name saved!", Toast.LENGTH_SHORT).show();
        }
    }
}
