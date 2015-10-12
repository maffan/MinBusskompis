package se.grupp4.minbusskompis.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.ui.fragments.StartPopupDialog;


public class MainActivity extends AppCompatActivity {

    Button button_parent;
    Button button_child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StartPopupDialog startPopupDialog = new StartPopupDialog(this);
        startPopupDialog.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        addButtonListeners();
    }

    public void addButtonListeners(){

        final Context context = this;

        button_parent = (Button) findViewById(R.id.parent_selectbutton);

        button_child = (Button) findViewById(R.id.child_selectbutton);

        button_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ändrade startvy till listan på children då man är parent.
                Intent intent = new Intent(MainActivity.this, ParentChildrenList.class);
                startActivity(intent);
            }
        });

        button_child.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChildDestinations.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
