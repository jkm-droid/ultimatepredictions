package jkmdroid.ultimatepredictions;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
public class FragmentPast extends Fragment{
    private ListView listView;
    private OnFragmentRestart onFragmentRestart;
    private ArrayList<Tip> tips;
    TextView errorView;
    ImageView imageError;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_past, container, false);
        listView = view.findViewById(R.id.listview);

        errorView = view.findViewById(R.id.error);
        imageError = view.findViewById(R.id.image_error);

        if (MyHelper.isOnline(getActivity())) {
            errorView.setText("Getting tips...Please wait");
        }else {
            imageError.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("There is no internet connection!!");
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
        }
        if (tips == null){
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
            ImageView imgWinlose = v.findViewById(R.id.winlose);

            if (tips.get(position).getVipStatus() == 10){
                ((TextView) v.findViewById(R.id.vip_status)).setText("VIP");
            }

            String winlose = tips.get(position).getWinLose();
            String s, correct = tips.get(position).getCorrect();
            s = "Pick => " + correct;

            if (winlose.equalsIgnoreCase("won")) {
                long diff = System.currentTimeMillis() - tips.get(position).getMatchTime();
                if(diff > 0 && diff <= (115*1000*60))
                    imgWinlose.setImageResource(R.drawable.pending);
                else
                    imgWinlose.setImageResource(R.drawable.won_status);

                ((TextView) v.findViewById(R.id.correct)).setText(s);

            }else if(winlose.equalsIgnoreCase("lost")){
                imgWinlose.setImageResource(R.drawable.lost_status);

                ((TextView) v.findViewById(R.id.correct)).setText(s);

            }

            return v;
        }
    }
}