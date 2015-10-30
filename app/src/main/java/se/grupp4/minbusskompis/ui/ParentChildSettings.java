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
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;

/*
    ParentChildSettings
    Child specific settings.
 */
public class ParentChildSettings extends AppCompatActivity{

    private String currentInstallationId;
    private Context context = this;
    private ViewHolder viewHolder;

    private class ViewHolder{
        EditText nameEdit;
        Button saveNameButton;
        Button destinationsButton;
        Button deleteChildButton;
        Button saveAndExitButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_settings);
        viewHolder = new ViewHolder();

        Intent intent = getIntent();
        currentInstallationId = intent.getStringExtra("child_id");

        //Init views
        initViews();
        
        //Set name
        viewHolder.nameEdit.setText(ParseCloudManager.getInstance().getNameFromId(currentInstallationId));

        //Set installation id
        TextView instId = (TextView) findViewById(R.id.parent_child_settings_installationid);
        instId.setText(currentInstallationId);

        //Button listeners
        initButtonListeners();
    }

    private void initViews() {
        viewHolder.nameEdit = (EditText) findViewById(R.id.parent_child_settings_name);
        viewHolder.saveNameButton = (Button) findViewById(R.id.parent_child_settings_savename_button);
        viewHolder.destinationsButton = (Button) findViewById(R.id.parent_child_settings_destinations_button);
        viewHolder.deleteChildButton = (Button) findViewById(R.id.parent_child_settings_delete_child_button);
        viewHolder.saveAndExitButton = (Button) findViewById(R.id.parent_child_settings_exit_child_button);
    }

    private void initButtonListeners() {
        viewHolder.saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeNameTask().execute();
            }
        });

        viewHolder.destinationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentChildSettings.this,ParentChildDestinations.class);
                intent.putExtra("child_id", currentInstallationId);
                startActivity(intent);
            }
        });

        viewHolder.deleteChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.parent_child_settings_delete_child_dialog_title)
                        .setMessage(R.string.parent_child_settings_delete_child_dialog_message)
                        .setPositiveButton(R.string.all_dialog_yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ParseCloudManager.getInstance().removeRelationshipFromSelf(currentInstallationId, ParseCloudManager.CHILD);
                                Intent intent = new Intent(getApplicationContext(),ParentChildrenList.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.all_dialog_no, null)
                        .show();
            }
        });

        viewHolder.saveAndExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ParentChildrenList.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Push name change to parse
     */
    private void changeName() {
        String newName = viewHolder.nameEdit.getText().toString();
        ParseCloudManager.getInstance().setNameForId(newName, currentInstallationId);

    }

    private class ChangeNameTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            changeName();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ParentChildSettings.this, R.string.parent_child_settings_delete_child_toast_post_execute, Toast.LENGTH_SHORT).show();
        }
    }
}
