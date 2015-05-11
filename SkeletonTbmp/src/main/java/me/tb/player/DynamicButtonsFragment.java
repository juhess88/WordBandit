package me.tb.player;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Juhess88 on 2/24/2015.
 */
public class DynamicButtonsFragment extends Fragment implements View.OnClickListener {
    CommunicatorGame comm4;

    protected static final String TAG = "MyTouchListener";
    protected static final boolean D = true;
    private GestureDetector gestureScanner;

    //list of buttons
    List<Button> newButtonList = new ArrayList<Button>();

    Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10,
            b11, b12, b13, b14;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dynamic_buttons_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm4 = (CommunicatorGame) getActivity();

        Button[] bttn_arr = new Button[]{b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10,
                b11, b12, b13, b14};

        newButtonList.addAll(Arrays.asList(bttn_arr));

        newButtonList.set(0, (Button) getActivity().findViewById(R.id.newbutton0));
        newButtonList.set(1, (Button) getActivity().findViewById(R.id.newbutton1));
        newButtonList.set(2, (Button) getActivity().findViewById(R.id.newbutton2));
        newButtonList.set(3, (Button) getActivity().findViewById(R.id.newbutton3));
        newButtonList.set(4, (Button) getActivity().findViewById(R.id.newbutton4));
        newButtonList.set(5, (Button) getActivity().findViewById(R.id.newbutton5));
        newButtonList.set(6, (Button) getActivity().findViewById(R.id.newbutton6));
        newButtonList.set(7, (Button) getActivity().findViewById(R.id.newbutton7));
        newButtonList.set(8, (Button) getActivity().findViewById(R.id.newbutton8));
        newButtonList.set(9, (Button) getActivity().findViewById(R.id.newbutton9));
        newButtonList.set(10, (Button) getActivity().findViewById(R.id.newbutton10));
        newButtonList.set(11, (Button) getActivity().findViewById(R.id.newbutton11));
        newButtonList.set(12, (Button) getActivity().findViewById(R.id.newbutton12));
        newButtonList.set(13, (Button) getActivity().findViewById(R.id.newbutton13));
        newButtonList.set(14, (Button) getActivity().findViewById(R.id.newbutton14));

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        Log.d(TAG, "DynamicButtonsFragment - onDown");
                        return false;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        float fling = (Math.abs(velocityX) + Math.abs(velocityY)) / 2;
                        if (D) {
                            Log.d(TAG, "DynamicButtonsFragment - onFling: " + fling + ", " + velocityX + " " + velocityY);
                            comm4.fling();
                        }
                        return true;
                    }

                });

        for (Button b : newButtonList) {
            b.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gesture.onTouchEvent(event);
                }
            });
        }

        for (Button b : newButtonList) {
            b.setOnClickListener(this);
        }

    }

    public boolean checkIfAllButtonsInNewWordClicked() {
        for (Button b : newButtonList)
            if (b.getVisibility() == View.VISIBLE)
                if (b.getTag().equals("0"))
                    return false;
        return true;
    }

    public int checkHowManyVisibleButtons() {
        int count = 0;
        for (Button b : newButtonList)
            if (b.getVisibility() == View.VISIBLE)
                count++;
        return count;
    }

    @Override
    public void onClick(View v) {

        Button btn = (Button) v;
        if (!btn.getText().toString().equals("")) {
            if (btn.getTag().equals("0")) {
                comm4.addLetterOfClickedButtonToEditTextFragment(btn.getText().toString());
                btn.setBackgroundResource(R.drawable.button_reverse);
                btn.setTag("1");
            } else {
                comm4.removeLetterOfClickedButtonFromEditTextFragment(btn.getText().toString());
                btn.setBackgroundResource(R.drawable.button);
                btn.setTag("0");
            }
        }

    }

    public void getTheWordUserTryingToSteal(String data) {

        clearTheNewClickedButtons();

        //make all tiles visible with no letters
        for (Button b : newButtonList) {
            b.setVisibility(View.VISIBLE);
            b.setText("");
        }

        String[] splitWord = data.split("(?!^)");

        //one letter per tile until word is complete
        for (int i = 0; i < splitWord.length; i++) {
            newButtonList.get(i).setText(splitWord[i]);

        }

        //hide the tiles that have no letters
        for (Button b : newButtonList) {
            if (b.getText().toString().equals(""))
                b.setVisibility(View.GONE);
        }
    }

    public void clearTheNewClickedButtons() {
        for (int i = 0; i < newButtonList.size(); i++) {
            newButtonList.get(i).setBackgroundResource(R.drawable.button);
            newButtonList.get(i).setTag("0");
        }
    }
}
