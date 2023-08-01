package ca.on.conestogac.utility;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.on.conestogac.R;

public class Utility {

    /*
    Method to set Activity title at run time.
     */
    public static void setActivityTitle(AppCompatActivity activity, String title)
    {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(activity);
        View v = inflator.inflate(R.layout.custom_actionbar, null);
        TextView actionBarTextView = v.findViewById(R.id.actionbarTextView);
        actionBarTextView.setText(title);
        activity.getSupportActionBar().setCustomView(v);
        activity.findViewById(R.id.actionbarTextView).setSelected(true);
    }
}
