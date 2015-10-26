package se.grupp4.minbusskompis.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.ParseCloudData;
import se.grupp4.minbusskompis.parsebuss.BussParseSyncMessenger;
import se.grupp4.minbusskompis.parsebuss.BussSync;
import se.grupp4.minbusskompis.parsebuss.SyncTaskCompleteCallback;

public class ParentChildrenAdd extends AppCompatActivity {

    private Button addChildButton;
    private TextView codeTextView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_parent_child_add);
        findViews();
        addButtonListener();
    }

    private void findViews() {
        codeTextView = (TextView) findViewById(R.id.parent_add_child_hint_text);
    }

    public void addButtonListener(){

        addChildButton=(Button)findViewById(R.id.parent_add_child_add_child_button);

        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Sending request
                BussSync sync = new BussSync(new BussParseSyncMessenger());
                if (!(codeTextView.getText().toString().equals(""))) {
                    Toast.makeText(context, R.string.parent_children_add_busssync_toast_sync, Toast.LENGTH_LONG).show();
                    sync.syncWithSyncCode(codeTextView.getText().toString(), new SyncTaskCompleteCallback() {
                        @Override
                        public void onSyncTaskComplete(boolean success, String installationId) {
                            if (success) {
                                Log.v("ParentChildrenAdd", "Successfully added");
                                //Save child to parse data
                                ParseCloudData.getInstance().addRelationshipToSelf(installationId, ParseCloudData.CHILD);
                                Toast.makeText(context, R.string.parent_children_add_busssync_toast_success + installationId, Toast.LENGTH_LONG).show();

                                //Switch to ChildSettings, pass on installation id
                                Intent intent = new Intent(context, ParentChildSettings.class);
                                intent.putExtra("child_id", installationId);
                                startActivity(intent);
                            } else {
                                Log.v("ParentChildrenAdd", "NOT Successfully added");
                                Toast.makeText(context, R.string.parent_children_add_busssync_toast_failed, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(context, R.string.parent_children_add_busssynt_toast_noinput, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parent_children_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), ParentSettings.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about)    {
            Intent intent = new Intent(getApplicationContext(), ParentInfoAbout.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
