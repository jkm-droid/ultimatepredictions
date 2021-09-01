package jkmdroid.ultimatepredictions;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * Created by jkmdroid on 7/04/21.
 */

public class FragmentPremium extends Fragment {
    TextView vipBenefits, toptierBenefits;
    Button joinVIP;
    Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_premium, container, false);;

        joinVIP = view.findViewById(R.id.join_vip);
        joinVIP.setOnClickListener(v ->{
            intent = new Intent(getActivity(), Subscription.class);
            startActivity(intent);
        });
        // Inflate the layout for this fragment
        return view;
    }

}