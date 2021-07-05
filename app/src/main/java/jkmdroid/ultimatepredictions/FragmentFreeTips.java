package jkmdroid.ultimatepredictions;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkmdroid on 7/04/21.
 */
public class FragmentFreeTips extends Fragment {
    private ListView listView;
    private OnFragmentRestart onFragmentRestart;
    private ArrayList<Tip> tips;
    TextView errorView, vipTextView, title;
    ImageView imageError, imageVip1, imageVip2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_free, container,false);

        listView = view.findViewById(R.id.listview);
        title =  view.findViewById(R.id.title);

//        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        AdView mAdView = view.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        errorView = view.findViewById(R.id.error);
        vipTextView = view.findViewById(R.id.vip_textview);

        imageError = view.findViewById(R.id.image_error);
        imageVip1 = view.findViewById(R.id.vip_image1);
        imageVip2 = view.findViewById(R.id.vip_image2);

        if (MyHelper.isOnline(getActivity())) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText(R.string.error);
        }else {
            imageError.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
            errorView.setText(R.string.error_connection);
            errorView.setTextColor(this.getResources().getColor(R.color.errorColor));
        }

        FrameLayout layout = new FrameLayout(getActivity());
        layout.addView(view);

        if (onFragmentRestart != null)
            onFragmentRestart.onTipsReceived();
        return layout;
    }
    public void setOnFragmentRestart(OnFragmentRestart onFragmentRestart){
        this.onFragmentRestart = onFragmentRestart;
    }
    public void setTips(ArrayList<Tip> tips){
        this.tips = tips;

        if (getContext() == null)
            return;

        if (tips != null && tips.size() > 0){
            errorView.setVisibility(View.GONE);
            listView.setAdapter(new Adapter(getContext(), tips));
        }else{
            errorView.setVisibility(View.VISIBLE);
            errorView.setText(R.string.error_string);
            errorView.setTextColor(this.getResources().getColor(R.color.errorColor));
        }
    }

    interface  OnFragmentRestart{
        void onTipsReceived();
    }
    public ArrayList<Tip> getTips() {
        return tips;
    }

    class Adapter extends ArrayAdapter {
        public Adapter(@NonNull Context context, @NonNull List objects){
            super(context, R.layout.past_tips, objects);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View v;
            if (convertView == null)
                v = LayoutInflater.from(getContext()).inflate(R.layout.past_tips, null);
            else v = convertView;

            ((TextView)v.findViewById(R.id.time)).setText(MyHelper.toPostDate(tips.get(position).getMatchTime()));
            ((TextView)v.findViewById(R.id.team1)).setText(tips.get(position).getTeamA());
            ((TextView)v.findViewById(R.id.team2)).setText(tips.get(position).getTeamB());
            ((TextView)v.findViewById(R.id.drawodds)).setText(String.format("%s", tips.get(position).getDraw()));
            ((TextView)v.findViewById(R.id.homeodds)).setText(String.format("%s", tips.get(position).getHome()));
            ((TextView)v.findViewById(R.id.awayodds)).setText(String.format("%s", tips.get(position).getAway()));

            ImageView imgWinlose = v.findViewById(R.id.winlose);

            if (tips.get(position).getVipStatus() == 10){
                ((TextView) v.findViewById(R.id.vip_status)).setText(R.string.vip_string);
            }

            String winlose = tips.get(position).getWinLose();
            String s, correct = tips.get(position).getCorrect();
            s = "Picked: " + correct + " -> ";


            if (winlose.equalsIgnoreCase("won")) {

                imgWinlose.setImageResource(R.drawable.won_status);

                if (correct.equalsIgnoreCase("home")){
                    ((TextView)v.findViewById(R.id.team1)).setTextColor(Color.parseColor("#0B880F"));
                    ((TextView)v.findViewById(R.id.homeodds)).setTextColor(Color.parseColor("#0B880F"));
                    ((TextView)v.findViewById(R.id.home)).setTextColor(Color.parseColor("#0B880F"));
                    s += tips.get(position).getHome();

                }else if(correct.equalsIgnoreCase("draw")){
                    ((TextView)v.findViewById(R.id.vs)).setTextColor(Color.parseColor("#0B880F"));
                    ((TextView)v.findViewById(R.id.drawodds)).setTextColor(Color.parseColor("#0B880F"));
                    ((TextView)v.findViewById(R.id.draw)).setTextColor(Color.parseColor("#0B880F"));
                    s += tips.get(position).getDraw();

                }else if (correct.equalsIgnoreCase("away")){
                    ((TextView)v.findViewById(R.id.team2)).setTextColor(Color.parseColor("#0B880F"));
                    ((TextView)v.findViewById(R.id.awayodds)).setTextColor(Color.parseColor("#0B880F"));
                    ((TextView)v.findViewById(R.id.away)).setTextColor(Color.parseColor("#0B880F"));
                    s += tips.get(position).getAway();

                }else{
                    s = "Picked: "+tips.get(position).getCorrect()+" -> "+tips.get(position).getOther();
                }

                if (tips.get(position).getScore().equals(""))
                    ((TextView)v.findViewById(R.id.score)).setText(String.format("%s", tips.get(position).getScore()));
                else
                    ((TextView)v.findViewById(R.id.score)).setText(String.format("Score:%s", tips.get(position).getScore()));

                ((TextView) v.findViewById(R.id.score)).setTextColor(Color.argb(250,0,165,0));
                ((TextView) v.findViewById(R.id.correct)).setTextColor(Color.argb(250,0,165,0));
                ((TextView) v.findViewById(R.id.correct)).setText(s);

            }

            return v;
        }
    }
}
