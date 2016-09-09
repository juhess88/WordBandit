/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.jhessstudios.wordbandit;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Basic turn data. It's just a blank data string and a turn number counter.
 * 
 * @author wolff
 * 
 */
public class SkeletonTurn {

    public static final String TAG = "EBTurn";

    public ArrayList<String> data1 = new ArrayList<String>();
    public ArrayList<String> data2= new ArrayList<String>();
    public ArrayList<String> button_letters = new ArrayList<String>();
    public ArrayList<String> list_of_lettersST = new ArrayList<String>();
    public ArrayList<String> make_words_with_letterST = new ArrayList<String>();

    //keeps track which player
    public String myParticipantIdST;
    public String playername1;
    public String playerprofile1;
    public String playerpoints1;

    public String playername2;
    public String playerprofile2;
    public String playerpoints2;

    public Boolean hadFirstTurn;

    //keeps track if second player does rematch
    public Boolean secondPlayerRematch;

    //updates the counter for random letters being put in the buttons
    public int my_list_counterST;

    //keeps track of the round
    public int turnCounter;

    //keeps track of round
    public int roundCounter;

    //keeps track of remaining tiles in game
    public String tilesCounter;

    public String shareNextTurnMessage;

    public Boolean secondPlayerFinalTurn;
    public Boolean viewEndOfGame;

    public Boolean viewEndOfGame2;

    public String messageCombo;

    public SkeletonTurn() {
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();
        JSONArray retValArr1 = new JSONArray();
        JSONArray retValArr2 = new JSONArray();
        JSONArray retValArr3 = new JSONArray();
        JSONArray retValArr4 = new JSONArray();
        JSONArray retValArr5 = new JSONArray();

        try {
            for (int i = 0; i < data1.size(); i++)
                retValArr1.put(data1.get(i));
            for (int i = 0; i < data2.size(); i++)
                retValArr2.put(data2.get(i));
            for (int i=0; i<button_letters.size(); i++)
                retValArr3.put(button_letters.get(i));
            for(int i=0; i<list_of_lettersST.size(); i++)
                retValArr4.put(list_of_lettersST.get(i));
            for(int i=0; i<make_words_with_letterST.size(); i++)
                retValArr5.put(make_words_with_letterST.get(i));

            retVal.put("data1", retValArr1);
            retVal.put("data2", retValArr2);
            retVal.put("button_letters", retValArr3);
            retVal.put("list_of_letterST", retValArr4);
            retVal.put("make_words_with_letterST", retValArr5);
            retVal.put("hadFirstTurn", hadFirstTurn);
            retVal.put("my_list_counterST", my_list_counterST);
            retVal.put("turnCounter", turnCounter);
            retVal.put("roundCounter", roundCounter);
            retVal.put("playername1", playername1);
            retVal.put("playerprofile1", playerprofile1);
            retVal.put("playerpoints1", playerpoints1);
            retVal.put("playername2", playername2);
            retVal.put("playerprofile2", playerprofile2);
            retVal.put("playerpoints2", playerpoints2);
            retVal.put("myParticipantIdST", myParticipantIdST);
            retVal.put("tilesCounter", tilesCounter);
            retVal.put("shareNextTurnMessage", shareNextTurnMessage);
            retVal.put("secondPlayerFinalTurn", secondPlayerFinalTurn);
            retVal.put("viewEndOfGame", viewEndOfGame);
            retVal.put("viewEndOfGame2", viewEndOfGame2);
            retVal.put("messageCombo", messageCombo);
            retVal.put("secondPlayerRematch", secondPlayerRematch);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String st = retVal.toString();


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(st);
        String prettyJsonString = gson.toJson(je);

//        Log.d(TAG, "==== PERSISTING\n" + prettyJsonString);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of SkeletonTurn.
    static public SkeletonTurn unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new SkeletonTurn();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(st);
        String prettyJsonString = gson.toJson(je);


//        Log.d(TAG, "====UNPERSIST \n" + prettyJsonString);

        SkeletonTurn retVal = new SkeletonTurn();

        try {
            JSONObject obj = new JSONObject(st);
            JSONArray cast1 = obj.getJSONArray("data1");
            JSONArray cast2 = obj.getJSONArray("data2");
            JSONArray cast3 = obj.getJSONArray("button_letters");
            JSONArray cast4 = obj.getJSONArray("list_of_letterST");
            JSONArray cast5 = obj.getJSONArray("make_words_with_letterST");

            for (int i=0; i<cast1.length(); i++) {
                String val = cast1.getString(i);
                retVal.data1.add(i, val);
            }
            for (int i=0; i<cast2.length(); i++) {
                String val = cast2.getString(i);
                retVal.data2.add(i, val);
            }

            for (int i=0; i<cast3.length(); i++) {
                String val = cast3.getString(i);
                retVal.button_letters.add(i, val);
            }

            for (int i=0; i<cast4.length(); i++) {
                String val = cast4.getString(i);
                retVal.list_of_lettersST.add(i, val);
            }

            for (int i=0; i<cast5.length(); i++) {
                String val = cast5.getString(i);
                retVal.make_words_with_letterST.add(i, val);
            }

            if (obj.has("hadFirstTurn")) {
                retVal.hadFirstTurn = obj.getBoolean("hadFirstTurn");
            }

            if (obj.has("my_list_counterST")) {
                retVal.my_list_counterST = obj.getInt("my_list_counterST");
            }

            if (obj.has("turnCounter")) {
                retVal.turnCounter = obj.getInt("turnCounter");
            }

            if (obj.has("roundCounter")) {
                retVal.roundCounter = obj.getInt("roundCounter");
            }

            if (obj.has("playername1")) {
                retVal.playername1 = obj.getString("playername1");
            }

            if (obj.has("playerprofile1")) {
                retVal.playerprofile1 = obj.getString("playerprofile1");
            }

            if (obj.has("playerpoints1")) {
                retVal.playerpoints1 = obj.getString("playerpoints1");
            }

            if (obj.has("playername2")) {
                retVal.playername2 = obj.getString("playername2");
            }

            if (obj.has("playerprofile2")) {
                retVal.playerprofile2 = obj.getString("playerprofile2");
            }

            if (obj.has("playerpoints2")) {
                retVal.playerpoints2 = obj.getString("playerpoints2");
            }

            if (obj.has("myParticipantIdST")) {
                retVal.myParticipantIdST = obj.getString("myParticipantIdST");
            }

            if (obj.has("tilesCounter")) {
                retVal.tilesCounter = obj.getString("tilesCounter");
            }

            if (obj.has("shareNextTurnMessage")) {
                retVal.shareNextTurnMessage = obj.getString("shareNextTurnMessage");
            }

            if (obj.has("secondPlayerFinalTurn")) {
                retVal.secondPlayerFinalTurn = obj.getBoolean("secondPlayerFinalTurn");
            }

            if (obj.has("secondPlayerRematch")) {
                retVal.secondPlayerRematch = obj.getBoolean("secondPlayerRematch");
            }

            if (obj.has("viewEndOfGame")) {
                retVal.viewEndOfGame = obj.getBoolean("viewEndOfGame");
            }

            if (obj.has("viewEndOfGame2")) {
                retVal.viewEndOfGame2 = obj.getBoolean("viewEndOfGame2");
            }

            if (obj.has("messageCombo")) {
                retVal.messageCombo = obj.getString("messageCombo");
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retVal;
    }
}
