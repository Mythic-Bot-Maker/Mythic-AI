package com.example.mythic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WellnessPanel {
    private final LinearLayout view;
    private final TextView moodText;
    private WellnessSignal latestSignal = new WellnessSignal("Calm baseline", "Prototype");

    public WellnessPanel(Context context) {
        view = new LinearLayout(context);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setPadding(18, 16, 18, 16);
        view.setBackgroundColor(Color.rgb(26, 32, 47));

        TextView label = new TextView(context);
        label.setText("Watch wellness signal");
        label.setTextColor(Color.rgb(186, 196, 216));
        label.setTextSize(13);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        view.addView(label);

        moodText = new TextView(context);
        moodText.setText("Calm baseline - prototype");
        moodText.setTextColor(Color.WHITE);
        moodText.setTextSize(18);
        moodText.setPadding(0, 6, 0, 0);
        view.addView(moodText);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 18);
        view.setLayoutParams(params);
    }

    public LinearLayout getView() {
        return view;
    }

    public WellnessSignal getLatestSignal() {
        return latestSignal;
    }

    public void showMood(String label, String confidence) {
        latestSignal = new WellnessSignal(label, confidence);
        moodText.setText(label + " - " + confidence);
    }
}
