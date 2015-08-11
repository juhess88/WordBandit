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
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditTextFragment extends Fragment {

    CommunicatorGame comm5;

    final String TAG = "MyTouchListener";
    final boolean D = true;

    private EditText editText;
    private String[] words;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_txt_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm5 = (CommunicatorGame) getActivity();
        editText = (EditText) getActivity().findViewById(R.id.edittextfrag);
        Button tb_enter = (Button) getActivity().findViewById(R.id.toolbar_enter);
        tb_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm5.fling(false);
            }
        });

        //this section reads the dictionary file called words and saves it in displayText
        String displayText = "";
        try {
            InputStream fileStream = getResources().openRawResource(
                    R.raw.enable1);
            int fileLen = fileStream.available();
            // Read the entire resource into a local byte buffer.
            byte[] fileBuffer = new byte[fileLen];
            fileStream.read(fileBuffer);
            fileStream.close();
            displayText = new String(fileBuffer);
        } catch (IOException e) {
            // exception handling
        }

        //dictionary saved in array called words
        words = displayText.split("\\s+");

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        Log.d(TAG, "EditTextFragment - onDown");
                        return true;
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
                                comm5.fling(true);
                            }
                        }
                        return true;
                    }

                });

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

    }

    public void changeEditText(String data) {
        editText.setText(editText.getText().toString() + data);
    }

    public void clearEditText() {
        editText.setText("");
    }

    public String sendEditText() {
        if (Arrays.asList(words).contains(editText.getText().toString()))
            return editText.getText().toString();
        else
            return "";
    }

    public String getTextFromEditText() {
        return editText.getText().toString();
    }

    public int editTextSize() {
        return editText.getText().toString().length();
    }

    public void removeCharFromText(String data) {
        String s = editText.getText().toString();
        String[] ss = s.split("");
        List<String> ssList = new ArrayList<String>();
        ssList.addAll(Arrays.asList(ss));
        ssList.remove(data);
        String newString = "";
        for (String b : ssList) {
            newString += b;
        }

        editText.setText(newString);

    }
}
