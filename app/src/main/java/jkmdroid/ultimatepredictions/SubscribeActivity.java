package jkmdroid.ultimatepredictions;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jkm-droid on 05/04/2021.
 */

public class SubscribeActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    RecyclerView recyclerView;
    TextView txtPremium, no_subscription;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        setupBillingClient();
        init();
    }

    private void init() {
        txtPremium = findViewById(R.id.txt_premium);
        no_subscription = findViewById(R.id.txt_no_subscriptions);
        recyclerView = findViewById(R.id.product_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

    }

    private void setupBillingClient() {
        acknowledgePurchaseResponseListener = billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                txtPremium.setVisibility(View.VISIBLE);
        };

        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Toast.makeText(getApplicationContext(), "Connected to billing system", Toast.LENGTH_LONG).show();

                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                            .getPurchasesList();
                    if (purchases.size() > 0) {
                        recyclerView.setVisibility(View.GONE);

                        for (Purchase purchase : purchases)
                            handle_item_already_subscribed(purchase);
                    }else{
                        txtPremium.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        loadAllSubscriptions();
                    }

                }
//                else{
//                    Toast.makeText(getApplicationContext(), "Error code: "+billingResult.getResponseCode(), Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getApplicationContext(), "Disconnected to billing system", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadAllSubscriptions() {
        if (billingClient.isReady()){
            SkuDetailsParams detailsParams = SkuDetailsParams.newBuilder()
                    .setSkusList(Arrays.asList("vip_tips"))
                    .setType(BillingClient.SkuType.SUBS)
                    .build();

            billingClient.querySkuDetailsAsync(detailsParams, (billingResult, list) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    ProductAdapter productAdapter = new ProductAdapter(SubscribeActivity.this, list, billingClient);
                    recyclerView.setAdapter(productAdapter);
                }else{
                    no_subscription.setVisibility(View.VISIBLE);
                    no_subscription.setText("No Subscriptions found");
                    Toast.makeText(SubscribeActivity.this, "Error: "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            Toast.makeText(this, "Billing Client not ready", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null)
            for (Purchase purchase : list){
                handle_item_already_subscribed(purchase);
            }
        else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED)
            Toast.makeText(this, "USER_CANCELED", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Error: "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

    private void handle_item_already_subscribed(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
            //if subscription not acknowledged
            if (!purchase.isAcknowledged()){
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);

                //give the user the subscription
                String purchaseToken = purchase.getPurchaseToken(), purchaseId = purchase.getOrderId();
                String purchaseTime = String.valueOf(purchase.getPurchaseTime());
                purchaseTime = convert_date(purchaseTime);

                send_purchase_details_to_server(purchaseToken, purchaseId, purchaseTime);

            }else {
                //if it is acknowledged
                txtPremium.setVisibility(View.VISIBLE);
                txtPremium.setText("You are already subscribed");
            }
        }
    }

    private void send_purchase_details_to_server(String token, String productId, String time) {

        String d = "";
        try {
            d += URLEncoder.encode("subscription_details", "UTF-8") + "=" + URLEncoder.encode("subs_details", "UTF-8") + "&";
            d += URLEncoder.encode("purchase_token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8") + "&";
            d += URLEncoder.encode("product_id", "UTF-8") + "=" + URLEncoder.encode(productId, "UTF-8") + "&";
            d += URLEncoder.encode("purchase_time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&";
            d += URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        final String data = d;
        final String link = "https://toptier.mblog.co.ke/subscriptions/subscriptions.php";

        @SuppressLint("HandlerLeak")Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String responses = "you are already subscribed";
                if (msg.arg1 == 200){
                    if (((String)msg.obj).equalsIgnoreCase("subscription saved successfully")){
                        Toast.makeText(SubscribeActivity.this, "SUBSCRIBED SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                    } else if(((String)msg.obj).equalsIgnoreCase("subscription not saved/")) {
                        Toast.makeText(SubscribeActivity.this, "SUBSCRIPTION FAILED", Toast.LENGTH_SHORT).show();

                    } else if(((String)msg.obj).equalsIgnoreCase("you are already subscribed")) {
                        txtPremium.setVisibility(View.VISIBLE);
                        txtPremium.setText("You are already subscribed");
                        Toast.makeText(SubscribeActivity.this, "YOU ARE ALREADY SUBSCRIBED", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SubscribeActivity.this, "UNKNOWN ERROR OCCURRED...RETRY", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String response = MyHelper.connectOnline(link, data);
                    Message message = new Message();
                    message.arg1 = 200;
                    message.obj = response;
                    handler.sendMessage(message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private String convert_date(String date) {
        Long timestamp = Long.parseLong(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return dateFormat.format(calendar.getTime());
    }
}