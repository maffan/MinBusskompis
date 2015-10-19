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
import android.widget.TextView;

import java.util.ArrayList;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussDestination;
import se.grupp4.minbusskompis.ui.adapters.DestinationsAdapter;
import se.grupp4.minbusskompis.ui.map.addLocationOnMap;

public class ParentChildDestinations extends AppCompatActivity {
    private DestinationsAdapter destinationsAdapter;
    private ArrayList<BussDestination> destinations;
    private String childId;
    private Context context;
    private ViewHolder viewHolder;

    private static class ViewHolder {
        ListView destinationsView;
        TextView loadingTextView;
        Button addDestinationButtonView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_destinations);
        this.context = this;
        //Initiate views
        viewHolder = new ViewHolder();
        viewHolder.destinationsView = (ListView) findViewById(R.id.parent_destinations_list);
        viewHolder.loadingTextView = (TextView) findViewById(R.id.parent_destinations_loading_text);
        viewHolder.addDestinationButtonView = (Button) findViewById(R.id.parent_destinations_add_destination_button);
        //Get child id
        childId = getIntent().getStringExtra("child_id");
        //Add button listeners
        addButtonListener();
        //Add destinations adapter
        destinations = new ArrayList<>();
        destinationsAdapter =
                new DestinationsAdapter(
                    this,
                    R.layout.fragment_destinations_list_item,
                    destinations,
                    childId);
        viewHolder.destinationsView.setAdapter(destinationsAdapter);
        new PopulateDestinationListTask().execute();
    }



    public void addButtonListener(){
        viewHolder.addDestinationButtonView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentChildDestinations.this, addLocationOnMap.class);
                intent.putExtra("child_id", childId);
                startActivity(intent);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new PopulateDestinationListTask().execute();
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

    private class PopulateDestinationListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
                @Override
                public void done() {
                    populateDestinations();
                }
            });
            return null;
        }
    }

    private void populateDestinations() {
        destinationsAdapter.clear();
        ArrayList<BussDestination> destList = BussData.getInstance().getDestinationsForChild(childId);
        if(destList.isEmpty()){
            viewHolder.loadingTextView.setText(R.string.parent_child_destinations_viewholder_defaulttext);
            showMessage();
        }else{
            destinationsAdapter.addAll(destList);
            destinationsAdapter.notifyDataSetChanged();
            showContent();
        }
    }

    private void showMessage() {
        viewHolder.loadingTextView.setVisibility(View.VISIBLE);
        viewHolder.destinationsView.setVisibility(View.GONE);
    }

    private void showContent() {
        viewHolder.loadingTextView.setVisibility(View.GONE);
        viewHolder.destinationsView.setVisibility(View.VISIBLE);
    }
}
