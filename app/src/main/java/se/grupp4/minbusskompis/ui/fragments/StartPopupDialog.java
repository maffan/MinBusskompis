package se.grupp4.minbusskompis.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import se.grupp4.minbusskompis.R;

/**
 * Created by Tobias on 2015-10-12.
 */
public class StartPopupDialog extends Dialog {
    Button dismissButton;

    public StartPopupDialog(Context context) {
        super(context);
        this.setTitle("Min Busskompis");
        this.setContentView(R.layout.fragment_start_info_dialog);

        dismissButton = (Button) this.findViewById(R.id.startInfoDismissButton);
        dismissButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
