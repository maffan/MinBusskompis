package se.grupp4.minbusskompis.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.ui.adapters.ChildAdapter;
import se.grupp4.minbusskompis.ui.adapters.ChildData;

public class ParentChildrenList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    protected Button buttonAddChild;
    private ChildAdapter childrenListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_children);
        addButtonListeners();

        childrenListAdapter =
                new ChildAdapter(
                        this,
                        R.layout.fragment_parent_child_list_item,
                        getChildrenList()
                );

        ListView childrenListView = (ListView) findViewById(R.id.parent_children_list);
        childrenListView.setAdapter(childrenListAdapter);
        childrenListView.setOnItemClickListener(this);
    }

    public void addButtonListeners(){
        //Add child button
        buttonAddChild = (Button)findViewById(R.id.button_addchild);
        buttonAddChild.setOnClickListener(new View.OnClickListener() {
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
    public ArrayList<ChildData> getChildrenList() {
        ArrayList<ChildData> data = new ArrayList<ChildData>();
        data.add(new ChildData("Karl",false,"1-1",0));
        data.add(new ChildData("Bert",false,"1-2",1));
        data.add(new ChildData("Konny",true,"1-3",2));
        data.add(new ChildData("Rikard",false,"1-4",3));
        return data;
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
