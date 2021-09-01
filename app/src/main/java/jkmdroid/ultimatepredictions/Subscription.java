package jkmdroid.ultimatepredictions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Subscription extends AppCompatActivity implements PurchasesUpdatedListener {
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private BillingClient billingClient;
    TextView productTitle, productPrice, productDescription, loadingView;
    CardView subscription;
    String email="ultimatepredictions@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        productTitle = findViewById(R.id.product_title);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        subscription = findViewById(R.id.cardview_subscription);
        loadingView = findViewById(R.id.loading);

        set_up_billing_client();
    }

    private void set_up_billing_client() {
        billingClient = BillingClient.newBuilder(Subscription.this)
                .enablePendingPurchases()
                .setListener(this)
                .build();
        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    System.out.println("------Connected to billing system---------------");
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
                    List<Purchase> purchaseList = queryPurchase.getPurchasesList();
                    if (purchaseList != null && purchaseList.size() > 0){
                        handle_already_subscribed_items(purchaseList);
                    }else{
                        //load all subscriptions
                        load_all_subscriptions();
                    }

                }else{
                    System.out.println("Error code: "+billingResult.getResponseCode());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                System.out.println("--------Disconnected from billing system------");
            }

        });
    }

    private void load_all_subscriptions() {
        if (billingClient.isReady()){
//            loadingView.setVisibility(View.VISIBLE);
            List<String> skuList = new ArrayList<>();
            skuList.add("ultimate_vip_package");
            SkuDetailsParams.Builder detailsParams = SkuDetailsParams.newBuilder();
            detailsParams.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

            billingClient.querySkuDetailsAsync(detailsParams.build(), new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull  BillingResult billingResult, @Nullable List<SkuDetails> skuDetails) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (skuDetails != null && skuDetails.size() > 0) {
                            System.out.println("------------getting sku details-------------");
                            String title = skuDetails.get(0).getTitle();
                            String price = skuDetails.get(0).getPrice();
                            String description = skuDetails.get(0).getDescription();
                            subscription.setVisibility(View.VISIBLE);
                            productTitle.setText(title);
                            productPrice.setText(price);
                            productDescription.setText(description);
//                            System.out.println("sku details------------------"+title+price+description);

                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetails.get(0))
                                    .build();
                            billingClient.launchBillingFlow(Subscription.this, billingFlowParams);
                        }else{
                            System.out.println("Sku details empty----------------------");
                        }
                    }
                }
            });
        }else{
            set_up_billing_client();
            System.out.println("Billing client not ready");
        }
    }

    private void handle_already_subscribed_items(List<Purchase> purchases) {
        for (Purchase purchase : purchases){
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
                    System.out.println("------YOU ARE ALREADY SUBSCRIBED---------------");
                }
            }
        }
    }

    private String convert_date(String purchaseTime) {
        Long timestamp = Long.parseLong(purchaseTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return dateFormat.format(calendar.getTime());
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

        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String responses = "you are already subscribed";//subscription saved successfully//subscription not saved//an error occurred
                if (msg.arg1 == 200){
                    if (((String)msg.obj).equalsIgnoreCase("subscription saved successfully")){
                        Toast.makeText(Subscription.this, "SUBSCRIBED SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                    } else if(((String)msg.obj).equalsIgnoreCase("subscription not saved/")) {
                        Toast.makeText(Subscription.this, "SUBSCRIPTION FAILED", Toast.LENGTH_SHORT).show();

                    } else if(((String)msg.obj).equalsIgnoreCase("you are already subscribed")) {
                        Toast.makeText(Subscription.this, "YOU ARE ALREADY SUBSCRIBED", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Subscription.this, "UNKNOWN ERROR OCCURRED...RETRY", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchaseList) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseList != null){
            handle_already_subscribed_items(purchaseList);

        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            Purchase.PurchasesResult alreadyOwned = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
            List<Purchase> purchases = alreadyOwned.getPurchasesList();
            if (purchases != null){
                handle_already_subscribed_items(purchases);
            }
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            Toast.makeText(Subscription.this, "Subscription cancelled", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Subscription.this, "Error"+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
        }
    }
}