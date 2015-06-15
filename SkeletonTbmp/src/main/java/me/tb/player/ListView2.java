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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juhess88 on 2/22/2015.
 */
public class ListView2 extends Fragment {

    final String TAG = "ListView1";
    final boolean D = true;

    CommunicatorGame comm3;

    int point = 0;

    String item;

    Boolean flag = false;

    ListView list;

    ArrayAdapter<String> adapter2;
    List<String> array_list;

    Boolean myTurn = true;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listview2, container, false);

    }

    public void setMyTurn(Boolean turn) {
        myTurn = turn;
    }

    public boolean getMyTurn() {
        return myTurn;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm3 = (CommunicatorGame) getActivity();
        array_list = new ArrayList<String>();

        //Build Adapter
        adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.items, array_list);

        //Configure the list view
        list = (ListView) getActivity().findViewById(R.id.listview2);
        list.setAdapter(adapter2);

        registerClickCallback();

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        Log.d(TAG, "ListView2 - onDown");
                        return false;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        float velocityThresh = 1000;
                        double distanceThresh = 75;
                        float fling = (Math.abs(velocityX) + Math.abs(velocityY)) / 2;
                        double distance = Math.sqrt(Math.pow(e2.getX() - e1.getX(), 2) + Math.pow(e2.getY() - e1.getY(), 2));

                        if (D) {
                            Log.d(TAG, "fling: " + fling + ", e1X: " + e1.getX() + ", e1Y: " + e1.getY()
                                    + ", e2X: " + e2.getX() + ", e2Y: " + e2.getY() + ", distance: " + distance
                                    + ", vX: " + velocityX + " vY:" + velocityY);

                            if (fling > velocityThresh && distance > distanceThresh) {
                                comm3.fling();
                            }
                        }
                        return true;
                    }

                });

        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

    }

    //updates the score on every click
    public String updateScoreListView() {
        point = 0;
        for (int i = 0; i < adapter2.getCount(); i++) {
            point += (adapter2.getItem(i).length()) * 100;
        }
        String score = "" + point;
        return score;
    }

    public int getPoint() {
        return point;
    }

    //if player 2's turn, words added to player 2's listview
    public void addWordToListView2(String data) {
        adapter2.insert(data, 0);
        adapter2.notifyDataSetChanged();
    }

    public void removeStolenWord() {
        adapter2.remove(item);
        adapter2.notifyDataSetChanged();

    }

    public String wordUserTryingToStealFromListView2() {
        return item;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean data) {
        flag = data;
    }

    private void registerClickCallback() {
        list = (ListView) getActivity().findViewById(R.id.listview2);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (myTurn) {

                    flag = true;

                    comm3.setListView1ToFalse();

                    item = (String) parent.getItemAtPosition(position);

                    comm3.makeGoldTilesForWordUserTryingToSteal(item);

                    comm3.removeCV();

                    EditText e = (EditText) getActivity().findViewById(R.id.edittextfrag);
                    e.setHint("Steal " + item);

                    Fragment listviewfrag1 = getActivity().getSupportFragmentManager().findFragmentByTag("LFrag1");
                    getActivity().getSupportFragmentManager().beginTransaction().hide(listviewfrag1).commit();

                    Fragment listviewfrag2 = getActivity().getSupportFragmentManager().findFragmentByTag("LFrag2");
                    getActivity().getSupportFragmentManager().beginTransaction().hide(listviewfrag2).commit();

                    Fragment gprofpic1 = getActivity().getSupportFragmentManager().findFragmentByTag("GFrag1");
                    getActivity().getSupportFragmentManager().beginTransaction().hide(gprofpic1).commit();

                    Fragment gprofpic2 = getActivity().getSupportFragmentManager().findFragmentByTag("GFrag2");
                    getActivity().getSupportFragmentManager().beginTransaction().hide(gprofpic2).commit();


                    Fragment dynbtn = getActivity().getSupportFragmentManager().findFragmentByTag("DFrag");
                    getActivity().getSupportFragmentManager().beginTransaction().show(dynbtn).commit();

                }
            }
        });
    }
}
