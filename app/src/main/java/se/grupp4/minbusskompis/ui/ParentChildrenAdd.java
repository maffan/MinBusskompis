package se.grupp4.minbusskompis.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.bussparse.BussParseSyncMessenger;
import se.grupp4.minbusskompis.bussparse.BussSync;
import se.grupp4.minbusskompis.bussparse.SyncTaskCompleteCallback;

public class ParentChildrenAdd extends AppCompatActivity {

    private Button addChildButton;
    private TextView codeTextView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_parent_children_add);
        findViews();
        addButtonListener();
    }

    private void findViews() {
        codeTextView = (TextView) findViewById(R.id.childCodeEditText);
    }

    public void addButtonListener(){

        addChildButton=(Button)findViewById(R.id.button_addchildcode);

        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BussSync sync = new BussSync(new BussParseSyncMessenger());
                sync.syncWithSyncCode(codeTextView.getText().toString(), new SyncTaskCompleteCallback() {
                    @Override
                    public void onSyncTaskComplete(boolean success, String installationId) {
                        if(success){
                            Toast.makeText(context,"Succesfully synced with device with Installation ID: "+installationId,Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(context,"Could not sync with device",Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
