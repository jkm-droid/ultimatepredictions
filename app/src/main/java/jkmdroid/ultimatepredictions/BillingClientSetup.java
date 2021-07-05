package jkmdroid.ultimatepredictions;


import android.content.Context;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;

/**
 * Created by jkm-droid on 05/04/2021.
 */

public class BillingClientSetup {
    private static BillingClient instance;

    public static BillingClient getInstance(Context context, PurchasesUpdatedListener listener){
        return instance == null ? setupBillingClient(context, listener):instance;
    }

    private static BillingClient setupBillingClient(Context context, PurchasesUpdatedListener listener) {
        BillingClient billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(listener)
                .build();
        return billingClient;
    }
}
