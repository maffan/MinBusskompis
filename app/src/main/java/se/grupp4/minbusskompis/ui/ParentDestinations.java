package se.grupp4.minbusskompis.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.parse.ParseInstallation;

import java.util.ArrayList;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussDestination;
import se.grupp4.minbusskompis.ui.adapters.DestinationsAdapter;

public class ParentDestinations extends AppCompatActivity {

    private Button buttonAddDestination;
    private ListView destinationListView;
    private DestinationsAdapter destinationsAdapter;
    private ArrayList<BussDestination> destinations;
    private String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        childId = getIntent().getStringExtra("child_id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_destinations);
        addButtonListener();
        destinations = new ArrayList<>();
        destinationsAdapter = new DestinationsAdapter(getApplicationContext(),
                R.layout.fragment_parent_destinastions_list_item,destinations,
                childId);
        destinationListView = (ListView) findViewById(R.id.parent_destinations_list);
        destinationListView.setAdapter(destinationsAdapter);

    }

    public void addButtonListener(){

        final Context context = this;

        buttonAddDestination = (Button) findViewById(R.id.Button_AddDestination);

        buttonAddDestination.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentDestinations.this, ParentDestinationsAdd.class);
                startActivity(intent);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parent_destinations, menu);
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

    private class PopulateDestinationListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            destinationsAdapter.addAll(BussData.getInstance().getDestinationsForChild(childId));
            return null;
        }
    }
}
