package se.grupp4.minbusskompis.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;
import se.grupp4.minbusskompis.parsebuss.BussDestination;

/*
    DestinationsAdapter
    Used to populate destinations for both parent and child
 */
public class DestinationsAdapter extends ArrayAdapter<BussDestination> {
    private Context context;
    private String currentInstallId;
    private DestinationsAdapter currentAdapter = this;

    private static class ViewHolder {
        ImageView destinationIconView;
        TextView destinationNameView;
        ImageView deleteDestinationIconView;
    }

    public DestinationsAdapter(Context context, int layout, ArrayList<BussDestination> destinations, String currentInstallId){
        super(context, layout, destinations);
        this.context = context;
        this.currentInstallId = currentInstallId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BussDestination currentDestination = getItem(position);
        View row;

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.fragment_destinations_list_item, parent, false);

            initViews(row, viewHolder);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            row = convertView;
        }

        //Add destination icon
        viewHolder.destinationIconView.setImageResource(R.drawable.destinations);

        //Set destination name
        viewHolder.destinationNameView.setText(currentDestination.getName());

        //If parent, add remove button
        if (!context.getSharedPreferences("MyPreferences", Context.MODE_APPEND).getBoolean("setaschild", false)) {
            //Add settings img
            viewHolder.deleteDestinationIconView.setImageResource(R.drawable.delete);

            //Add settings button listener, pass in id to settings to populate
            viewHolder.deleteDestinationIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseCloudManager.getInstance().removeDestinationFromChild(currentDestination.getName(),currentInstallId);
                    currentAdapter.remove(currentDestination);
                    currentAdapter.notifyDataSetChanged();
                }
            });
        }

        return row;
    }

    private void initViews(View row, ViewHolder viewHolder) {
        viewHolder.destinationIconView = (ImageView) row.findViewById(R.id.destination_list_item_icon);
        viewHolder.destinationNameView = (TextView) row.findViewById(R.id.destination_list_item_destination_name);
        viewHolder.deleteDestinationIconView = (ImageView) row.findViewById(R.id.destination_list_item_delete_icon);
    }
}
