package me.tb.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Juhess88 on 4/26/2015.
 */
public class HomeFragment extends Fragment {

    CommunicatorGame comm;
    ImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm = (CommunicatorGame) getActivity();
        img = (ImageView)getActivity().findViewById(R.id.backHome);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comm.exitGameQuestion("Are you sure you want to exit?");
            }
        });
    }
}
