package de.nimax.nimax_cocktails.mixing;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.ViewListener;

public class AmountViewListener implements ViewListener {

    /**
     * Type of drinks
     */
    private int[] amounts;
    /**
     * Active activity
     */
    private Activity activity;

    public AmountViewListener(int[] amounts, Activity activity) {
        this.amounts = amounts;
        this.activity = activity;
    }

    @Override
    public View setViewForPosition(int position) {
        View amount_item = activity.getLayoutInflater().inflate(R.layout.adapter_amount, null);
        // Modify the text
        TextView text = amount_item.findViewById(R.id.text_amount);
        String amount = Integer.toString(amounts[position]);
        text.setText(amount);
        return amount_item;
    }
}
