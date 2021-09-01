package jkmdroid.ultimatepredictions;

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
public class FragmentFreeTips extends Fragment {
    private ListView listView;
    private OnFragmentRestart onFragmentRestart;
    private ArrayList<Tip> tips;
    TextView errorView, vipTextView, title;
    ImageView imageError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_free, container,false);

        listView = view.findViewById(R.id.listview);
        title =  view.findViewById(R.id.title);
        imageError = view.findViewById(R.id.image_error);


        errorView = view.findViewById(R.id.error);
        vipTextView = view.findViewById(R.id.vip_textview);

        if (MyHelper.isOnline(getContext())) {
            errorView.setText("Getting tips...Please wait");
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
            super(context, R.layout.free_tips, objects);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View v;
            if (convertView == null)
                v = LayoutInflater.from(getContext()).inflate(R.layout.free_tips, null);
            else v = convertView;

            ((TextView)v.findViewById(R.id.time)).setText(MyHelper.toPostDate(tips.get(position).getMatchTime()));
            ((TextView)v.findViewById(R.id.team1)).setText(tips.get(position).getTeamA());
            ((TextView)v.findViewById(R.id.team2)).setText(tips.get(position).getTeamB());

            if (tips.get(position).getVipStatus() == 10){
                ((TextView) v.findViewById(R.id.vip_status)).setText(R.string.vip_string);
            }

            String s = "", correct = tips.get(position).getCorrect();
//            s = "Pick => " + correct;
            if (correct.equalsIgnoreCase("home")){
                s = tips.get(position).getTeamA()+ " Wins";
            }else if (correct.equalsIgnoreCase("away")){
                s = tips.get(position).getTeamB()+ " Wins";
            }else{
                s = "Pick => " + correct;
            }

            ((TextView) v.findViewById(R.id.correct)).setText(s);

            return v;
        }
    }
}
