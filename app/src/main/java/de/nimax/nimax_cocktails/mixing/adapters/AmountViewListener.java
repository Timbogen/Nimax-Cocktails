package de.nimax.nimax_cocktails.mixing.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.ViewListener;

public class AmountViewListener implements ViewListener {

    /**
     * The possible amounts
     */
    private final int[] amounts;
    /**
     * The current activity
     */
    private final Activity activity;

    /**
     * Constructor
     *
     * @param amounts The possible amounts
     * @param activity The current activity
     */
    public AmountViewListener(int[] amounts, Activity activity) {
        this.amounts = amounts;
        this.activity = activity;
    }

    /**
     * Update the view for a certain position
     *
     * @param position The current position
     * @return The updated view
     */
    @Override
    public View setViewForPosition(int position) {
        View amount_item = View.inflate(activity, R.layout.adapter_amount, null);
        // Modify the text
        TextView text = amount_item.findViewById(R.id.text_amount);
        String amount = Integer.toString(amounts[position]);
        text.setText(amount);
        return amount_item;
    }
}
