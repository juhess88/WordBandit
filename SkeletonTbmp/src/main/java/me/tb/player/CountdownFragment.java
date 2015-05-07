package me.tb.player;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Juhess88 on 2/26/2015.
 */
public class CountdownFragment extends Fragment {

    CountDownTimer cd=null;
    TextView timer;
    CommunicatorGame comm4;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.countdown_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        timer = (TextView) getActivity().findViewById(R.id.timer);

        comm4 = (CommunicatorGame) getActivity();

    }

    public void startCountdown(){
        cd = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timer.setText("done!");

                //ends turn or game
                //********I think I can delete this line below
//                if(comm4.getTilesRemainingInt()<130 && comm4.lastTurnFirstPlayer())
                //********I think I can delete this above line

//                if(comm4.lastTurnFirstPlayer())
//                    comm4.finishGame();
//                else
//                    comm4.turnComplete();
            }
        }.start();
    }
}
