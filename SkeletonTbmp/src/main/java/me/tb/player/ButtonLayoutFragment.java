package me.tb.player;

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
import android.widget.TextView;

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

    //random letter up to 109
    int my_list_counter = 109;

    //randomly selects letters to go in the squares
    Random rand = new Random();
    int random_counter;

    ArrayList<String> button_letter = new ArrayList<String>();

    CardView cv;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            return inflater.inflate(R.layout.buttons_layout21, container, false);
        }
        return inflater.inflate(R.layout.buttons_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm = (CommunicatorGame) getActivity();

//        letters = new String[]{"a", "b", "i", "d", "e", "s", "r", "a", "b", "i", "d", "e", "s", "r", "t"};

        /*
        * CHANGE MY_LIST COUNTER  TO 144 --- CURRENTLY CHANGED TO 15 FOR TESTING
        *
        *
        *       ORIGINAL LIST OF 144 TILES
        *
        *       "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", //18
                "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", //13
                "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", //12
                "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", //11
                "t", "t", "t", "t", "t", "t", "t", "t", "t", //9
                "r", "r", "r", "r", "r", "r", "r", "r", "r", //9
                "n", "n", "n", "n", "n", "n", "n", "n", //8
                "d", "d", "d", "d", "d", "d", //6
                "s", "s", "s", "s", "s", "s", //6
                "u", "u", "u", "u", "u", "u", //6
                "l", "l", "l", "l", "l", //5
                "g", "g", "g", "g", //4
                "b", "b", "b", //3
                "c", "c", "c", //3
                "f", "f", "f", //3
                "h", "h", "h", //3
                "m", "m", "m", //3
                "p", "p", "p", //3
                "v", "v", "v", //3
                "w", "w", "w", //3
                "y", "y", "y", //3
                "j", "j", //2
                "k", "k", //2
                "q", "q", //2
                "x", "x", //2
                "z", "z"}; //2

        * */
        letters = new String[]{
                //109 total
                "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", //12
                "a", "a", "a", "a", "a", "a", "a", "a", "a", // 9
                "i", "i", "i", "i", "i", "i", "i", "i", //8
                "o", "o", "o", "o", "o", "o", //6
                "t", "t", "t", "t", "t", "t", //6
                "r", "r", "r", "r", "r", "r", //6
                "n", "n", "n", "n", //4
                "d", "d", "d", "d", "d", //5
                "s", "s", "s", "s", "s", //5
                "u", "u", "u", "u", "u", //5
                "l", "l", "l", "l", //4
                "g", "g", "g", "g", //4
                "b", "b", "b", //3
                "c", "c", "c", //3
                "f", "f", "f", //3
                "h", "h", //2
                "m", "m", "m", //3
                "p", "p", "p", //3
                "v", "v", //2
                "w", "w", "w", //3
                "y", "y", "y", //3
                "j", "j", //2
                "k", "k", //2
                "q", "q", //2
                "x", "x", //2
                "z", "z"}; //2

        Log.d("Total Tiles: ", "" + letters.length);
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
//                        Log.d(TAG, "ButtonLayoutFragment - onDown");
                        return false;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        float velocityThresh = 500;
                        double distanceThresh = 75;
                        float fling = (Math.abs(velocityX) + Math.abs(velocityY)) / 2;
                        double distance = Math.sqrt(Math.pow(e2.getX() - e1.getX(), 2) + Math.pow(e2.getY() - e1.getY(), 2));

                        if (D) {
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

    public void removeCardView() {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            cv = (CardView) getActivity().findViewById(R.id.cardviewUnder21);
            cv.setContentPadding(-10, -10, -10, -10);
            cv.setCardElevation(0);
        }
    }

    public void restoreCardView() {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            cv = (CardView) getActivity().findViewById(R.id.cardviewUnder21);
            cv.setContentPadding(0, 0, 0, 0);
            cv.setCardElevation(10);
        }
    }

    public void shuffle() {
        for (int i = 0; i < button_list.size(); i++) {
            list_of_letters.add(0, button_list.get(i).getText().toString());
            button_list.get(i).setText("");
            my_list_counter++;
            fillEmptyTilesWithNewLettersIfNoText();
            clearTheClickedButtons();
            comm.clearTextFromEditTextFragment();
            comm.returnButtonsToUnclickedState();
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
