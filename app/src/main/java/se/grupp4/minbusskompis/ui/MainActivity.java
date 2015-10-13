package se.grupp4.minbusskompis.ui;

import android.app.Activity;
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


public class MainActivity extends Activity {

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
}
