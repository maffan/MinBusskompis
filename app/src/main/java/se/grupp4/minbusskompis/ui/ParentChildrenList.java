package se.grupp4.minbusskompis.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussRelationMessenger;
import se.grupp4.minbusskompis.ui.adapters.ChildAdapter;
import se.grupp4.minbusskompis.ui.adapters.ChildData;


/*
    ParentChildrenList
    Populates list fetched from Parse with your relationships (children)
    Fetches data, populates list, puts listener on list items.
    If child is active you can click to view more information.
    You can enter child specific settings on each child listed.

    Check Childadapter for populated data info

 */
public class ParentChildrenList extends AppCompatActivity implements AdapterView.OnItemClickListener, Observer {

    private static final String TAG = "PARENT_CHILDREN_LIST";
    private ChildAdapter childrenListAdapter;
    private ViewHolder viewHolder;
    private Context context = this;

    private static class ViewHolder {
        ListView childrenListView;
        TextView loadingTextView;
        Button buttonAddChildView;
    }

    /**
     * Creates viewholder, initiates views
     * Connects adapter to listview, fetches data and populates relations (children)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_children);
        initViews();
        addButtonListeners();
        initList();
        populateList();
        BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                BussRelationMessenger.getInstance().setRelationships(BussData.getInstance().getChildren());
            }
        });
        BussRelationMessenger.getInstance().addObserver(this);
    }

    /**
     * Initate viewholder views, connect viewholder items to GUI objects
     */
    private void initViews() {
        viewHolder = new ViewHolder();
        viewHolder.childrenListView = (ListView) findViewById(R.id.parent_children_list);
        viewHolder.loadingTextView = (TextView) findViewById(R.id.parent_children_loading_text);
        viewHolder.buttonAddChildView = (Button)findViewById(R.id.button_addchild);
    }

    /**
     * Initate buttonlisteners
     */
    public void addButtonListeners(){
        viewHolder.buttonAddChildView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentChildrenList.this, ParentChildrenAdd.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initiates childrenlist, connect adapter to list
     */
    private void initList() {
        ArrayList<ChildData> childrenList = new ArrayList<>();
        childrenListAdapter =
                new ChildAdapter(
                        this,
                        R.layout.fragment_parent_child_list_item,
                        childrenList
                );

        viewHolder.childrenListView.setAdapter(childrenListAdapter);
        viewHolder.childrenListView.setOnItemClickListener(this);
    }

    /**
     * Repopulate list on observer update
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        populateList();
    }

    private void populateList() {
        new PopulateChildrenListTask().execute();
    }

    /**
     * Repopulate list on resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        populateList();
    }

    /**
     * Settings dropdown
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parent_children, menu);
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

    /**
     * Fetch data from parse, on callback populate list with relations(children)
     */
    private class PopulateChildrenListTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            getAndShowChildrenListOrMessage();
            return null;
        }

    }

    /**
     * Used from background task in PopulateChildrenListTask to fetch data
     */
    public void getAndShowChildrenListOrMessage() {
        BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                ArrayList<ChildData> tempList = getChildrenListFromParse();
                showContentOrMessage(tempList);
            }
        });
    }

    /**
     * Converts parse json data to arraylist
     * @return
     */
    private ArrayList<ChildData> getChildrenListFromParse() {
        return BussData.getInstance().getChildren().getAsChildDataList();
    }

    /**
     * Populate relationslist, shows empty message if no relations is available.
     * @param tempList
     */
    private void showContentOrMessage(ArrayList<ChildData> tempList) {
        if(tempList.isEmpty()){
            viewHolder.loadingTextView.setText(R.string.parent_children_list_no_children_text);
        }else{
            childrenListAdapter.clear();
            childrenListAdapter.addAll(tempList);
            childrenListAdapter.notifyDataSetChanged();
            showChildrenList();
        }
    }

    /**
     * Removes "Loading text", replaces with list
     */
    private void showChildrenList(){
        viewHolder.loadingTextView.setVisibility(View.GONE);
        viewHolder.childrenListView.setVisibility(View.VISIBLE);
    }

    /**
     * Enable users to click on each active child to view more specific information about the ongoing trip.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChildData child = (ChildData) parent.getAdapter().getItem(position);

        if(child.isActive()){
            Intent intent = new Intent(this, ParentActiveChild.class);
            intent.putExtra("child_id", child.getId());
            startActivity(intent);
        }else{
            //Log.v("CHILDADAPTER","Child inactive: " +child.getId());
        }

    }
}
