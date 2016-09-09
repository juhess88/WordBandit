package me.jhessstudios.wordbandit;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Juhess88 on 4/26/2015.
 */
public class StealTextFragment extends Fragment {

    CommunicatorGame comm;
    TextView txt, txt2;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.steal_text_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm = (CommunicatorGame) getActivity();

        txt = (TextView)getActivity().findViewById(R.id.stealText);
        txt2 = (TextView)getActivity().findViewById(R.id.stealPlain);
        txt2.setPaintFlags(txt2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void setText(String data){
        txt.setText(data);
    }
}
