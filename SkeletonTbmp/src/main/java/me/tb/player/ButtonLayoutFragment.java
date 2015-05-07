package me.tb.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ButtonLayoutFragment extends Fragment implements View.OnClickListener {

    CommunicatorGame comm;

    final String TAG = "MyTouchListener";
    final boolean D = true;

    //the tiles remaining
    TextView tiles;

    String[] letters;

    List<String> list_of_letters = new ArrayList<String>();

    //list of letters chosen to make a word
    List<String> make_words_with_letters = new ArrayList<String>();

    //list of buttons
    List<Button> button_list = new ArrayList<Button>();

    Button b0, b1, b2, b3, b4, b5, b6, b7, b8;

    //random letter up to 144
    int my_list_counter = 144;

    //randomly selects letters to go in the squares
    Random rand = new Random();
    int random_counter;

    int shuffleBoardOnce = 0;

    ArrayList<String> button_letter = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.buttons_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm = (CommunicatorGame) getActivity();

        letters = new String[]{"e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e",
                "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
                "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i",
                "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o",
                "t", "t", "t", "t", "t", "t", "t", "t", "t",
                "r", "r", "r", "r", "r", "r", "r", "r", "r",
                "n", "n", "n", "n", "n", "n", "n", "n",
                "d", "d", "d", "d", "d", "d",
                "s", "s", "s", "s", "s", "s",
                "u", "u", "u", "u", "u", "u",
                "l", "l", "l", "l", "l",
                "g", "g", "g", "g",
                "b", "b", "b",
                "c", "c", "c",
                "f", "f", "f",
                "h", "h", "h",
                "m", "m", "m",
                "p", "p", "p",
                "v", "v", "v",
                "w", "w", "w",
                "y", "y", "y",
                "j", "j",
                "k", "k",
                "q", "q",
                "x", "x",
                "z", "z"};

        list_of_letters.addAll(Arrays.asList(letters));

        Button[] bttn_arr = new Button[]{b0, b1, b2, b3, b4, b5, b6, b7, b8};

        button_list.addAll(Arrays.asList(bttn_arr));

        button_list.set(0, (Button) getActivity().findViewById(R.id.button0));
        button_list.set(1, (Button) getActivity().findViewById(R.id.button1));
        button_list.set(2, (Button) getActivity().findViewById(R.id.button2));
        button_list.set(3, (Button) getActivity().findViewById(R.id.button3));
        button_list.set(4, (Button) getActivity().findViewById(R.id.button4));
        button_list.set(5, (Button) getActivity().findViewById(R.id.button5));
        button_list.set(6, (Button) getActivity().findViewById(R.id.button6));
        button_list.set(7, (Button) getActivity().findViewById(R.id.button7));
        button_list.set(8, (Button) getActivity().findViewById(R.id.button8));

        fillEmptyTilesWithNewLettersIfNoText();

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        Log.d(TAG, "ButtonLayoutFragment - onDown");
                        return false;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        float velocityThresh = 500;
                        double distanceThresh = 75;
                        float fling = (Math.abs(velocityX) + Math.abs(velocityY)) / 2;
                        double distance = Math.sqrt(Math.pow(e2.getX() - e1.getX(), 2) + Math.pow(e2.getY() - e1.getY(), 2));

                        if (D) {
                            Log.d(TAG, "fling: " + fling + ", e1X: " + e1.getX() + ", e1Y: " + e1.getY()
                                    + ", e2X: " + e2.getX() + ", e2Y: " + e2.getY() + ", distance: " + distance
                                    + ", vX: " + velocityX + " vY:" + velocityY);

                            if (fling > velocityThresh && distance > distanceThresh) {
                                comm.fling();
                            }
                        }
                        return true;
                    }

                });

        for (Button b : button_list) {
            b.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gesture.onTouchEvent(event);
                }
            });
        }

        for (Button b : button_list) {
            b.setOnClickListener(this);
        }
    }

    public List<String> getButton_letter() {
        button_letter.clear();
        for (Button b : button_list) {
            button_letter.add(0, b.getText().toString());

        }
        return button_letter;
    }

    public List<String> getList_of_letters() {
        return list_of_letters;
    }

    public void setList_of_letters(List<String> list_of_letters) {
        this.list_of_letters = list_of_letters;
    }

    public void fillEmptyTilesWithNewLettersIfNoText() {
        while (buttonsHaveTextCheck(button_list)) {
            if (my_list_counter <= 0)
                return;     // length of the array/list.
            random_counter = rand.nextInt(my_list_counter);
            make_words_with_letters.add(list_of_letters.get(random_counter));
            for (Button b : button_list) //contains the buttons
            {
                if (b.getText().equals("")) { //if button is empty, add text to it
                    b.setText(list_of_letters.get(random_counter)); //sets text to random letter from List
                    list_of_letters.remove(list_of_letters.get(random_counter)); //then removes that letter from the List
                    my_list_counter--;
                    break;
                }

            }
        }
    }

    public void shuffle() {
        if (shuffleBoardOnce == 0) {
            for (int i = 0; i < button_list.size(); i++) {
                list_of_letters.add(0, button_list.get(i).getText().toString());
                button_list.get(i).setText("");
                my_list_counter++;
                fillEmptyTilesWithNewLettersIfNoText();
                clearTheClickedButtons();
                comm.clearTextFromEditTextFragment();
                comm.returnButtonsToUnclickedState();
                shuffleBoardOnce++;
            }
        } else {
            Toast.makeText(getActivity(), "Can only shuffle once per turn", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeTextFromButtonsIfClickedAndIsWord() {
        for (Button b : button_list)
            if (b.getTag() == "1")
                b.setText("");
    }

    //if all tiles have letters return false
    //otherwise return true
    public boolean buttonsHaveTextCheck(List<Button> BList) {
        for (Button b : BList) {
            if (b.getText().equals(""))
                return true;
        }
        return false;
    }

    //string - updates the score on every click
    public void updateTilesRemainingString(TextView t) {
        t.setText("Tiles: " + list_of_letters.size());
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        if (!btn.getText().toString().equals("")) {
            if (btn.getTag().equals("0")) {
                comm.addLetterOfClickedButtonToEditTextFragment(btn.getText().toString());
                btn.setBackgroundResource(R.drawable.button_reverse);
                btn.setTag("1");
            } else {
                comm.removeLetterOfClickedButtonFromEditTextFragment(btn.getText().toString());
                btn.setBackgroundResource(R.drawable.button);
                btn.setTag("0");
            }
        }
    }

    public void clearTheClickedButtons() {
        for (int i = 0; i < button_list.size(); i++) {
            button_list.get(i).setBackgroundResource(R.drawable.button);
            button_list.get(i).setTag("0");
        }
    }
}
