package se.grupp4.minbusskompis.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.ui.ParentChildSettings;

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

    public ChildAdapter (Context context, int layout, ArrayList<ChildData> children){
        super(context, layout, children);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChildData child = getItem(position);
        View row;

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.fragment_parent_child_list_item, parent, false);

            viewHolder.activityIconView = (ImageView) row.findViewById(R.id.parent_children_list_item_child_active_icon);
            viewHolder.childNameView = (TextView) row.findViewById(R.id.parent_children_list_item_name);
            viewHolder.settingsButtonView = (ImageView) row.findViewById(R.id.parent_children_list_item_settings_icon);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            row = convertView;
        }


        //Set icon by mode
        switch (child.getMode()){
            case 0:
                viewHolder.activityIconView.setImageResource(R.drawable.inactive);
                break;
            case 1:
                viewHolder.activityIconView.setImageResource(R.drawable.walking);
                break;
            case 2:
                viewHolder.activityIconView.setImageResource(R.drawable.busstop);
                break;
            case 3:
                viewHolder.activityIconView.setImageResource(R.drawable.bus);
                break;
            case 4:
                viewHolder.activityIconView.setImageResource(R.drawable.busstop);
                break;
            case 5:
                viewHolder.activityIconView.setImageResource(R.drawable.walking);
                break;
            default:
                viewHolder.activityIconView.setImageResource(R.drawable.inactive);
                break;
        }

        //Set name
        viewHolder.childNameView.setText(child.getName());

        //Check if parent
        if(true){
            //Add settings img
            viewHolder.settingsButtonView.setImageResource(R.drawable.settings);

            //Add settings button listener, pass in id to settings to populate
            viewHolder.settingsButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ParentChildSettings.class);
                    intent.putExtra("child_id",child.getId());
                    context.startActivity(intent);
                }
            });
        }

        return row;
    }
}
