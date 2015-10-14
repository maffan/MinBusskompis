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


//Note that dummybuttons are temporary for debugging
public class ChildDestinations extends AppCompatActivity {

    protected Button childCodeButton;
    protected Button walkmodeButton;
    protected Button busStation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_destinations);
        addButtonListener();
    }

    public void addButtonListener() {
        final Context context = this;

        childCodeButton = (Button) findViewById(R.id.child_destination_childcode_button);
        walkmodeButton = (Button) findViewById(R.id.child_destination_walkmode_button);
        busStation = (Button) findViewById(R.id.child_destination_busstation_button);

        childCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChildDestinations.this, ChildChildCode.class);
                startActivity(intent);
            }
        });

        walkmodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChildDestinations.this, ChildWalkMode.class);
                startActivity(intent);
            }
        });

        busStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChildDestinations.this, ChildBusStation.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_destinations, menu);
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

    private void settingsItem(){

        Intent intent = new Intent(this, ChildChildCode.class);
        startActivity(intent);
    }
}
