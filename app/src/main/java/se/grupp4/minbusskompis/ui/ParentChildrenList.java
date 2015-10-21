package se.grupp4.minbusskompis.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class ParentChildrenList extends AppCompatActivity implements AdapterView.OnItemClickListener, Observer {

    @Override
    public void update(Observable observable, Object data) {
        populateList();
    }

    private class StartActiveChildTaskDelayed extends AsyncTask<Void, Void, Void> {
        private int position;
        private int millis;

        public StartActiveChildTaskDelayed(int position, int millis) {
            this.position = position;
            this.millis = millis;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d(TAG, "doInBackground: sleeping...");
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "doInBackground: wake up!");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute: performing click!");
            ListView childrenListView = viewHolder.childrenListView;
            childrenListView.performItemClick(
                    childrenListAdapter.getView(position, null, childrenListView),
                    position,
                    childrenListAdapter.getItemId(position));
        }
    }

    private static class ViewHolder {
        ListView childrenListView;
        TextView loadingTextView;
        Button buttonAddChildView;
    }

    private static final String TAG = "PARENT_CHILDREN_LIST";
    private ChildAdapter childrenListAdapter;
    private ViewHolder viewHolder;
    private Context context = this;

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

    private void initViews() {
        viewHolder = new ViewHolder();
        viewHolder.childrenListView = (ListView) findViewById(R.id.parent_children_list);
        viewHolder.loadingTextView = (TextView) findViewById(R.id.parent_children_loading_text);
        viewHolder.buttonAddChildView = (Button) findViewById(R.id.button_addchild);
    }

    public void addButtonListeners() {
        //Add child button
        viewHolder.buttonAddChildView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentChildrenList.this, ParentChildrenAdd.class);
                startActivity(intent);
            }
        });
    }

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

    private void populateList() {
        new PopulateChildrenListTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateList();
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
            Intent intent = new Intent(getApplicationContext(), ParentSettings.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(getApplicationContext(), ParentInfoAbout.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PopulateChildrenListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getAndShowChildrenListOrMessage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (int i = 0; i < childrenListAdapter.getCount(); i++) {
                Log.d(TAG, "update: i = " + i);
                ChildData child = childrenListAdapter.getItem(i);
                Log.d(TAG, "update: child = " + child);
                if (child.isActive()) {
                    Log.d(TAG, "update: child is active!");
                    new StartActiveChildTaskDelayed(i, 6000).execute();
                }
            }
        }
    }

    //hämta data från parse
    public void getAndShowChildrenListOrMessage() {
        BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                ArrayList<ChildData> tempList = getChildrenListFromParse();
                showContentOrMessage(tempList);
            }
        });
    }

    private ArrayList<ChildData> getChildrenListFromParse() {
        return BussData.getInstance().getChildren().getAsChildDataList();
    }

    private void showContentOrMessage(ArrayList<ChildData> tempList) {
        if (tempList.isEmpty()) {
            viewHolder.loadingTextView.setText(R.string.parent_children_list_no_children_text);
        } else {
            childrenListAdapter.clear();
            childrenListAdapter.addAll(tempList);
            childrenListAdapter.notifyDataSetChanged();
            showChildrenList();
        }
    }

    private void showChildrenList() {
        viewHolder.loadingTextView.setVisibility(View.GONE);
        viewHolder.childrenListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
        Log.d(TAG, "onItemClick: view is: " + view.toString());
        ChildData child = (ChildData) parent.getAdapter().getItem(position);

        if (child.isActive()) {
            Intent intent = new Intent(context, ParentActiveChild.class);
            intent.putExtra("child_id", child.getId());
            startActivity(intent);
        } else {
            //Log.v("CHILDADAPTER","Child inactive: " +child.getId());
        }
    }
}
