package jkmdroid.ultimatepredictions;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;

import java.util.List;

/**
 * Created by jkm-droid on 05/04/2021.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    AppCompatActivity appCompatActivity;
    List<SkuDetails> skuDetailsList;
    BillingClient billingClient;

    public ProductAdapter(AppCompatActivity appCompatActivity, List<SkuDetails> skuDetailsList, BillingClient billingClient) {
        this.appCompatActivity = appCompatActivity;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(appCompatActivity.getBaseContext())
                .inflate(R.layout.product_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.product_name.setText(skuDetailsList.get(position).getTitle());
        holder.product_price.setText(skuDetailsList.get(position).getPrice());
        holder.product_description.setText(skuDetailsList.get(position).getDescription());

        holder.setClickListener((view, position1) -> {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetailsList.get(position1))
                    .build();

            int response = billingClient.launchBillingFlow(appCompatActivity, billingFlowParams)
                    .getResponseCode();
            switch (response){
                case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                    Toast.makeText(appCompatActivity, "BILLING_UNAVAILABLE", Toast.LENGTH_LONG).show();
                    break;

                case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                    Toast.makeText(appCompatActivity, "DEVELOPER_ERROR", Toast.LENGTH_LONG).show();
                    break;

                case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                    Toast.makeText(appCompatActivity, "FEATURE_NOT_SUPPORTED", Toast.LENGTH_LONG).show();
                    break;

                case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                    Toast.makeText(appCompatActivity, "ITEM_ALREADY_OWNED", Toast.LENGTH_LONG).show();
                    break;
                case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                    Toast.makeText(appCompatActivity, "SERVICE_DISCONNECTED", Toast.LENGTH_LONG).show();
                    break;

                case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                    Toast.makeText(appCompatActivity, "SERVICE_TIMEOUT", Toast.LENGTH_LONG).show();
                    break;

                case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                    Toast.makeText(appCompatActivity, "ITEM_UNAVAILABLE", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView product_name, product_price, product_description;
        IRecyclerClickListener clickListener;

        public void setClickListener(IRecyclerClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public MyViewHolder(View itemView){
            super(itemView);

            product_name = (TextView)itemView.findViewById(R.id.product_name);
            product_price = (TextView)itemView.findViewById(R.id.product_price);
            product_description = (TextView)itemView.findViewById(R.id.product_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition());
        }
    }
}
