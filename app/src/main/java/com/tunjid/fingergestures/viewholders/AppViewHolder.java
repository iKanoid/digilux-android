package com.tunjid.fingergestures.viewholders;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import com.tunjid.androidbootstrap.view.recyclerview.BaseViewHolder;
import com.tunjid.fingergestures.R;
import com.tunjid.fingergestures.adapters.AppAdapter;
import com.tunjid.fingergestures.billing.PurchasesManager;

public class AppViewHolder extends BaseViewHolder<AppAdapter.AppAdapterListener> {

    public AppViewHolder(View itemView) {
        super(itemView);
    }

    AppViewHolder(View itemView, AppAdapter.AppAdapterListener listener) {
        super(itemView, listener);
    }

    public void bind() {}

    void goPremium(@StringRes int description) {
        Context context = itemView.getContext();
        new AlertDialog.Builder(context)
                .setTitle(R.string.go_premium_title)
                .setMessage(context.getString(R.string.go_premium_body, context.getString(description)))
                .setPositiveButton(R.string.continue_text, (dialog, which) -> adapterListener.purchase(PurchasesManager.PREMIUM_SKU))
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
