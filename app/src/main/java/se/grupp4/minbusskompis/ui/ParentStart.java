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

public class ParentStart extends AppCompatActivity {

    Button buttonChildren;
    Button buttonDestinations;
    Button buttonSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_start);
        addListenerButton();
    }

    public void addListenerButton(){

        final Context context = this;

        buttonChildren = (Button) findViewById(R.id.parent_childrenbutton);
        buttonDestinations = (Button) findViewById(R.id.parent_destinationsbutton);
        buttonSettings = (Button) findViewById(R.id.parent_settingsbutton);



        buttonDestinations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentStart.this, ParentDestinations.class);
                startActivity(intent);
            }
        });

        buttonChildren.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(ParentStart.this, ParentChildrenList.class);
                startActivity(intent);
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentStart.this, ParentSettings.class);
                startActivity(intent);

            }
        });
    }


}
