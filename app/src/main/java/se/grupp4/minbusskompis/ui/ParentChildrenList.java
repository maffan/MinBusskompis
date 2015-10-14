package se.grupp4.minbusskompis.ui;

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

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.ui.adapters.ChildAdapter;
import se.grupp4.minbusskompis.ui.adapters.ChildData;

public class ParentChildrenList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static class ViewHolder {
        ListView childrenListView;
        TextView loadingTextView;
        Button buttonAddChildView;
    }

    private static final String TAG = "PARENT_CHILDREN_LIST";
    private ChildAdapter childrenListAdapter;
    private ArrayList<ChildData> childrenList;
    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_children);
        viewHolder = new ViewHolder();
        //initate views
        viewHolder.childrenListView = (ListView) findViewById(R.id.parent_children_list);
        viewHolder.loadingTextView = (TextView) findViewById(R.id.parent_children_loading_text);
        viewHolder.buttonAddChildView = (Button)findViewById(R.id.button_addchild);

        //Initate listeners
        addButtonListeners();

        //Initiate listadapter
        childrenList = new ArrayList<>();
        childrenListAdapter =
                new ChildAdapter(
                        this,
                        R.layout.fragment_parent_child_list_item,
                        childrenList
                );

        viewHolder.childrenListView.setAdapter(childrenListAdapter);
        viewHolder.childrenListView.setOnItemClickListener(this);
        new PopulateChildrenListTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChildrenList();
    }

    public void addButtonListeners(){
        //Add child button
        viewHolder.buttonAddChildView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentChildrenList.this, ParentChildrenAdd.class);
                startActivity(intent);
            }
        });
    }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //hämta data från parse
    public void getChildrenList() {
        BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                childrenListAdapter.clear();
                ArrayList<ChildData> tempList = BussData.getInstance().getChildren().getAsChildDataList();
                if(tempList.isEmpty()){
                    viewHolder.loadingTextView.setText("No children added");
                }else{
                    childrenListAdapter.addAll(tempList);
                    childrenListAdapter.notifyDataSetChanged();
                    populateChildren();
                }
            }
        });
    }

    private void populateChildren(){
        viewHolder.loadingTextView.setVisibility(View.GONE);
        viewHolder.childrenListView.setVisibility(View.VISIBLE);
    }

    private class PopulateChildrenListTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            getChildrenList();

            return null;
        }
    }

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
