package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.ui.fragments.StartPopupDialog;

/*
    StartSelectMode
    Activity that will only be shown during the first startup of the application.
    After either selecting child or parent, the application will henceforth start directly to that
    activity.

    On first startup an informationdialog is shown.
 */
public class StartSelectMode extends Activity {

    private SharedPreferences sharedPreferences;
    private ViewHolder viewHolder;
    private Context context = this;

    private class ViewHolder{
        Button button_parent;
        Button button_child;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_select_mode);
        viewHolder = new ViewHolder();

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_APPEND);
        if(!(sharedPreferences.contains("startpopupdialog"))) {
            sharedPreferences.edit().putBoolean("startpopupdialog", true).apply();
        }
        if(sharedPreferences.getBoolean("startpopupdialog", true)) {
            StartPopupDialog startPopupDialog = new StartPopupDialog(this);
            startPopupDialog.show();
            sharedPreferences.edit().putBoolean("startpopupdialog", false).apply();
        }
        initViews();
        addButtonListeners();
    }

    private void initViews() {
        viewHolder.button_parent = (Button) findViewById(R.id.startup_parent_button);
        viewHolder.button_child = (Button) findViewById(R.id.startup_child_button);
    }

    /**
     * Save your if the device should be either a parent or a child
     */
    public void addButtonListeners(){
        viewHolder.button_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ändrade startvy till listan på children då man är parent.
                sharedPreferences.edit().putBoolean("setasparent", true).apply();
                sharedPreferences.edit().putBoolean("setaschild", false).apply();
                Intent intent = new Intent(getApplicationContext(), ParentChildrenList.class);
                startActivity(intent);
            }
        });

        viewHolder.button_child.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sharedPreferences.edit().putBoolean("setaschild", true).apply();
                sharedPreferences.edit().putBoolean("setasparent", false).apply();
                Intent intent = new Intent(getApplicationContext(), ChildDestinations.class);
                startActivity(intent);
            }
        });
    }


}
