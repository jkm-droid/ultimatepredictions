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
        View view = inflater.inflate(R.layout.fragment_premium, container, false);

        vipBenefits = view.findViewById(R.id.vip_benefits);
        toptierBenefits = view.findViewById(R.id.toptier_benefits);

//        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        AdView mAdView = view.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        set_animation(vipBenefits);

        joinVIP = view.findViewById(R.id.join_vip);
        joinVIP.setOnClickListener(v ->{
            intent = new Intent(getActivity(), SubscribeActivity.class);
            startActivity(intent);
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void set_animation(TextView textView) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1800); //manage the blinking time
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //change color at start of animation
                textView.setTextColor(getActivity().getResources().getColor(R.color.colorVIP1));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //change color at end of animation
                textView.setTextColor(getActivity().getResources().getColor(R.color.green));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                textView.setTextColor(getActivity().getResources().getColor(R.color.colorVIP2));
            }
        });
        textView.startAnimation(anim);
    }

}