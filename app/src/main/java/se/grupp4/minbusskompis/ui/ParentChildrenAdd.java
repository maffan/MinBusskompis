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
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;
import se.grupp4.minbusskompis.parsebuss.BussParseSyncMessenger;
import se.grupp4.minbusskompis.parsebuss.BussSyncer;
import se.grupp4.minbusskompis.parsebuss.SyncTaskCompleteCallback;
/*
    ParentChildrenAdd
    Add a new child by child id
 */
public class ParentChildrenAdd extends AppCompatActivity {

    private Context context = this;
    private ViewHolder viewHolder;

    private class ViewHolder{
        private Button addChildButton;
        private TextView codeTextView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_add);
        viewHolder = new ViewHolder();
        initiateViews();
        addButtonListeners();
    }

    private void initiateViews() {
        viewHolder.codeTextView = (TextView) findViewById(R.id.parent_add_child_hint_text);
        viewHolder.addChildButton=(Button)findViewById(R.id.parent_add_child_add_child_button);
    }

    /**
     * When pressing add button a syncrequest will be sent via parse, if child acknowledges user will be sent into the settings for that child
     */
    public void addButtonListeners(){
        viewHolder.addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Sending request
                BussSyncer sync = new BussSyncer(new BussParseSyncMessenger());
                if (!(viewHolder.codeTextView.getText().toString().equals(""))) {
                    Toast.makeText(context, R.string.parent_children_add_busssync_toast_sync, Toast.LENGTH_LONG).show();
                    sync.syncWithSyncCode(viewHolder.codeTextView.getText().toString(), new SyncTaskCompleteCallback() {
                        @Override
                        public void onSyncTaskComplete(boolean success, String installationId) {
                            if (success) {
                                Log.v("ParentChildrenAdd", "Successfully added");
                                //Save child to parse data
                                ParseCloudManager.getInstance().addRelationshipToSelf(installationId, ParseCloudManager.CHILD);
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
                } else {
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
