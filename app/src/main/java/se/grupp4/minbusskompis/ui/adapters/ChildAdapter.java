package se.grupp4.minbusskompis.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.ui.ChildSettings;
import se.grupp4.minbusskompis.ui.ParentChildrenAdd;
import se.grupp4.minbusskompis.ui.ParentChildrenList;

/**
 * Created by Tobias on 2015-10-12.
 */
public class ChildAdapter extends ArrayAdapter<ChildData> {
    private Context context;

    private static class ViewHolder {
        ImageView activityIconView;
        TextView childNameView;
        ImageView settingsButtonView;
    }

    public ChildAdapter (Context context, ArrayList<ChildData> children){
        super(context, 0, children);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get current data
        ChildData child = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_parent_child_list_item, parent, false);
            viewHolder.activityIconView = (ImageView) convertView.findViewById(R.id.parent_children_list_item_child_active_icon);
            viewHolder.childNameView = (TextView) convertView.findViewById(R.id.parent_children_list_item_name);
            viewHolder.settingsButtonView = (ImageView) convertView.findViewById(R.id.parent_children_list_item_settings_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //if active set active icon otherwise inactive icon
        if(child.isActive()){
            viewHolder.activityIconView.setImageResource(R.drawable.active_child);
            //make clickable status
        }else{
            viewHolder.activityIconView.setImageResource(R.drawable.inactive_child);
        }

        //Set name
        viewHolder.childNameView.setText(child.getName());

        //Add settings button listener
        viewHolder.settingsButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChildSettings.class);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
