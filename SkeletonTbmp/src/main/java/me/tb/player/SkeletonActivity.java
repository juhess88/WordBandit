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

package me.tb.player;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.plus.Plus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * INSTRUCTIONS: To run this sample, please set up
 * a project in the Developer Console. Then, place your app ID on
 * res/values/ids.xml. Also, change the package name to the package name you
 * used to create the client ID in Developer Console. Make sure you sign the
 * APK with the certificate whose fingerprint you entered in Developer Console
 * when creating your Client Id.
 *
 * @author Wolff (wolff@google.com), 2013
 */
public class SkeletonActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnInvitationReceivedListener, OnTurnBasedMatchUpdateReceivedListener,
        CommunicatorGame {

    public static final String TAG = "SkeletonActivity";

    private GestureDetector gestureScanner;

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Current turn-based match
    private TurnBasedMatch mTurnBasedMatch;

    private AlertDialog mAlertDialog;

    // For our intents
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    // How long to show toasts.
    final static int TOAST_DELAY = Toast.LENGTH_SHORT;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    public static boolean isViewingBoardAfterTurn = false;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;

    // This is the current match data after being unpersisted.
    // Do not retain references to match data once you have
    // taken an action on the match, such as takeTurn()
    public SkeletonTurn mTurnData;

    //keeps track if player 1's turn or player 2's turn
    String myParticipantId;

    boolean isRestart = false;

    String[] fullName1, fullName2;
    String playerPhotoUrl1;
    String playerPhotoUrl2;
    String playername1;
    String playername2;
    List<String> lv1View = new ArrayList<String>();
    List<String> lv2View = new ArrayList<String>();

    EditTextFragment et;
    ButtonLayoutFragment bl;
    DynamicButtonsFragment db;
    GProfilePic1 gp1;
    GProfilePic2 gp2;
    ListView1 lv1;
    ListView2 lv2;

    FragmentManager manager;

    RulesForStealingWords rules;

    Boolean myTurn = true;
    Boolean enableFling = true;

    Boolean secondPlayerEndedGame = false;
    Boolean secondPlayerRematch = false;

    Boolean viewEndOfGame = false;
    Boolean viewEndOfGame2 = false;

    int nextTurn = 0;

    static String shareMessageTitle = "";
    static String shareMessageBody = "";
    static String shareMessageCombo = "";

    private Toolbar toolbar;

    //    private RecyclerView recyclerView;
//    private RecyclerShareAdapter recyclerAdapter;
    private Button buttonMessage;

    Button tb_tiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        initFragments();

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        rules = new RulesForStealingWords();

        gestureScanner = new GestureDetector(this, new MyGestureListener());

        tb_tiles = (Button) findViewById(R.id.toolbar_tiles);

        Button tb_pass = (Button) findViewById(R.id.toolbar_pass);
        tb_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageAtPass("Are you sure you want to pass?\n\nThis action will end your turn.");
            }
        });

        Button tb_shuffle = (Button) findViewById(R.id.toolbar_shuffle);
        tb_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageAtShuffle("Are you sure you want to shuffle the tiles?\n\nThis action will end your turn.");
            }
        });
    }

//    public static List<RecyclerShareModel> getData() {
//        List<RecyclerShareModel> data = new ArrayList<>();
//        int icon = R.drawable.shareicon;
//        String title = shareMessageCombo;
//        RecyclerShareModel current = new RecyclerShareModel();
//        current.iconId = icon;
//        current.title = title;
//        data.add(current);
//        return data;
//    }

    public void setTextTiles(String data) {
        tb_tiles.setText(data);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureScanner.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float velocityThresh = 500;
            double distanceThresh = 75;
            float fling = (Math.abs(velocityX) + Math.abs(velocityY)) / 2;
            double distance = Math.sqrt(Math.pow(e2.getX() - e1.getX(), 2) + Math.pow(e2.getY() - e1.getY(), 2));

            if (fling > velocityThresh && distance > distanceThresh && enableFling) {
                fling(true);
            }

            return true;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isRestart = true;
        if (isViewingBoardAfterTurn || !myTurn || myTurn) {
            lv1.adapter1.clear();
            lv2.adapter2.clear();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Retrieve the TurnBasedMatch from the connectionHint
        Bundle bundle = getIntent().getBundleExtra("data");
        if (bundle != null) {
            connectionHint = bundle;
        }
        if (connectionHint != null) {
            mTurnBasedMatch = connectionHint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (mTurnBasedMatch != null) {
                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                    Log.d(TAG, "Warning: accessing TurnBasedMatch when not connected");
                }

                updateMatch(mTurnBasedMatch);
            }

        } else {

            Intent intent = getIntent();
            startTheGame(intent);
        }
    }

    public void initFragments() {
        bl = new ButtonLayoutFragment();
        et = new EditTextFragment();
        db = new DynamicButtonsFragment();
        gp1 = new GProfilePic1();
        gp2 = new GProfilePic2();
        lv1 = new ListView1();
        lv2 = new ListView2();

        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.fragment_button_layout, bl, "BFrag");
        transaction.add(R.id.etToolbar, et, "EFrag");
        transaction.add(R.id.fragment_dynamic_button_layout, db, "DFrag").hide(db);
        transaction.add(R.id.fragment_google1, gp1, "GFrag1");
        transaction.add(R.id.fragment_google2, gp2, "GFrag2");
        transaction.add(R.id.fragment_listview1, lv1, "LFrag1");
        transaction.add(R.id.fragment_listview2, lv2, "LFrag2");

        transaction.commit();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public int getTilesRemaining() {
        return bl.getList_of_letters().size();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
//            case R.id.enter:
//                fling();
//                return true;
            case R.id.clear:
                returnButtonsToUnclickedState();
                clearTextFromEditTextFragment();
                return true;
//            case R.id.shuffle:
//                messageAtShuffle("Are you sure you want to shuffle the tiles?\n\nThis action will end your turn.");
//                return true;
//            case R.id.pass:
//                messageAtPass("Are you sure you want to pass?\n\nThis action will end your turn.");
//                return true;
            case R.id.exit:
                exitGameQuestion("Are you sure you want to exit?");
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

//        MenuItem tile_item = menu.findItem(R.id.tile_count);
//        MenuItem shuffle = menu.findItem(R.id.shuffle);
//        MenuItem pass = menu.findItem(R.id.pass);
        MenuItem exit = menu.findItem(R.id.exit);
//        MenuItem enter = menu.findItem(R.id.enter);
        MenuItem clear = menu.findItem(R.id.clear);
        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);

//        if (bl != null) {
//            tile_item.setTitle("Tiles Remaining: " + bl.getList_of_letters().size());
//        }
        if (!myTurn) {
//            tile_item.setVisible(true);
//            shuffle.setVisible(false);
//            pass.setVisible(false);
            exit.setVisible(false);
//            enter.setVisible(false);
            clear.setVisible(false);
        } else {
//            tile_item.setVisible(true);
//            shuffle.setVisible(true);
//            pass.setVisible(true);
            exit.setVisible(true);
//            enter.setVisible(true);
            clear.setVisible(true);
        }

        return true;
    }


    // Finish the game. Sometimes, this is your only choice.
    public void onFinishClicked() {
        showSpinner();

        ArrayList<Participant> participants = mMatch.getParticipants();

        ParticipantResult opponentResult = null;
        ParticipantResult creatorResult = null;

        if (Integer.parseInt(mTurnData.playerpoints1) > Integer.parseInt(mTurnData.playerpoints2)) {
//            Log.d("GAME OVER", "player 1 wins");
            opponentResult = new ParticipantResult(participants.get(0).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_WIN, 1);
            creatorResult = new ParticipantResult(participants.get(1).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_LOSS, 2);
        } else if (Integer.parseInt(mTurnData.playerpoints1) < Integer.parseInt(mTurnData.playerpoints2)) {
//            Log.d("GAME OVER", "player 2 wins");
            opponentResult = new ParticipantResult(participants.get(0).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_LOSS, 2);
            creatorResult = new ParticipantResult(participants.get(1).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_WIN, 1);
        } else {
//            Log.d("GAME OVER", "tie");
            opponentResult = new ParticipantResult(participants.get(0).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_TIE, 1);
            creatorResult = new ParticipantResult(participants.get(1).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_TIE, 1);
        }

        gp1.setCurrentPlayerBlack();
        gp2.setCurrentPlayerBlack();
        lv1.setMyTurn(false);
        lv2.setMyTurn(false);

        Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId()
                , mMatch.getData(), creatorResult, opponentResult)
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });

        isDoingTurn = false;
        myTurn = false;
        for (int i = 0; i < bl.button_list.size(); i++) {
            bl.button_list.get(i).setClickable(false);
        }
        lv1.setMyTurn(false);
        lv2.setMyTurn(false);
        setViewVisibility();

    }

    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.
    public void completeYouTurn() {
        showSpinner();

        lv1View.clear();
        lv2View.clear();

        String nextParticipantId = getNextParticipantId();

        mTurnData.myParticipantIdST = nextParticipantId;

        //when user clicks onDone we know the first turn has ended
        mTurnData.hadFirstTurn = true;

        //keeps track of the random letters counter
        mTurnData.my_list_counterST = bl.my_list_counter;

        //add stuff here for mTurnData.data1
        int x = lv1.adapter1.getCount();
        mTurnData.data1.clear();
        for (int i = 0; i < x; i++) {
            mTurnData.data1.add(i, lv1.adapter1.getItem(i));
            lv1View.add(0, lv1.adapter1.getItem(i));
        }

        //add stuff here for mTurnData.data2
        int y = lv2.adapter2.getCount();
        mTurnData.data2.clear();
        for (int i = 0; i < y; i++) {
            mTurnData.data2.add(i, lv2.adapter2.getItem(i));
            lv2View.add(0, lv2.adapter2.getItem(i));
        }

        //saves button_letters state
        mTurnData.button_letters.clear();
        for (int i = 0; i < bl.button_list.size(); i++) {
            mTurnData.button_letters.add(i, bl.button_list.get(i).getText().toString());
        }

        //saves list_of_words state
        mTurnData.list_of_lettersST.clear();
        for (int i = 0; i < bl.list_of_letters.size(); i++) {
            mTurnData.list_of_lettersST.add(i, bl.list_of_letters.get(i));
        }

        //saves make words with letter state state
        mTurnData.make_words_with_letterST.clear();
        for (int i = 0; i < bl.make_words_with_letters.size(); i++) {
            mTurnData.make_words_with_letterST.add(i, bl.make_words_with_letters.get(i));
        }

        //add stuff here for mTurnData.playerpoints1
        mTurnData.playerpoints1 = gp1.score.getText().toString();
        //add stuff here for mTurnData.playerpoints2
        mTurnData.playerpoints2 = gp2.score.getText().toString();

        //pass the tiles data for the next player
        mTurnData.tilesCounter = Integer.toString(bl.getList_of_letters().size());

        mTurnData.messageCombo = shareMessageCombo;

        showSpinner();

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, mMatch.getMatchId(),
                mTurnData.persist(), nextParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });

        mTurnData = null;
    }

    // Sign-in, Sign out behavior

    // Update the visibility based on what state we're in.
    public void setViewVisibility() {

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        if (isViewingBoardAfterTurn) {
            Intent intent = new Intent(SkeletonActivity.this, AfterTurnComplete.class);
            intent.putStringArrayListExtra("button_letter", (ArrayList) bl.getButton_letter());
            intent.putStringArrayListExtra("list1", (ArrayList) lv1View);
            intent.putStringArrayListExtra("list2", (ArrayList) lv2View);
            intent.putExtra("score1", gp1.score.getText().toString());
            intent.putExtra("score2", gp2.score.getText().toString());
            intent.putExtra("name1", fullName1[0]);
            intent.putExtra("name2", fullName2[0]);
            intent.putExtra("pic1", playerPhotoUrl1);
            intent.putExtra("pic2", playerPhotoUrl2);
            intent.putExtra("tiles", Integer.toString(bl.getList_of_letters().size()));
            intent.putExtra("share", shareMessageCombo);
            if (secondPlayerRematch) {
                myParticipantId = getNextParticipantId();
            }
            intent.putExtra("nextPlayerTurn", myParticipantId);
            startActivity(intent);
            SkeletonActivity.this.finish();
        }
    }

    @Override
    public Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public void messageAtStartOfTurn(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Test", "testing for rematch");
    }


    // Switch to gameplay view.
    public void setGameplayUI() {

        isDoingTurn = true;
        setViewVisibility();

        EditText y = (EditText) findViewById(R.id.edittextfrag);
        if (y.getHint().equals("Game Over")) {
            shareMessageCombo = "Thanks for playing!";
            mTurnData.messageCombo = "Thanks for playing!";
        }

        if (mTurnData.messageCombo != null && mTurnData.messageCombo.contains("shuffle")) {
            mTurnData.shareNextTurnMessage = mTurnData.messageCombo;
        }

        if (mTurnData.messageCombo != null && mTurnData.messageCombo.contains("pass")) {
            mTurnData.shareNextTurnMessage = mTurnData.messageCombo;
        }

        shareMessageCombo = mTurnData.messageCombo;


        // Create the next turn
        mTurnData.turnCounter += 1;

        nextTurn = mTurnData.turnCounter + 1;

        for (int i = 0; i < mTurnData.data1.size(); i++) {
            lv1.adapter1.add(mTurnData.data1.get(i));
//            Log.d("words", mTurnData.data1.get(i));
        }
        for (int i = 0; i < mTurnData.data2.size(); i++) {
            lv2.adapter2.add(mTurnData.data2.get(i));
        }

        for (int i = 0; i < mTurnData.button_letters.size(); i++) {
            bl.button_list.get(i).setText(mTurnData.button_letters.get(i));
        }

        //if the first player went delete the list of words and letter and load them with our saved data...
        if (mTurnData.hadFirstTurn != null) {
            if (mTurnData.hadFirstTurn) {
                bl.list_of_letters.clear();
            }
        }
        for (int i = 0; i < mTurnData.list_of_lettersST.size(); i++) {
            bl.list_of_letters.add(i, mTurnData.list_of_lettersST.get(i));
        }

        if (mTurnData.list_of_lettersST != null && mTurnData.turnCounter > 1) {
            setTextTiles("Tiles: " + mTurnData.list_of_lettersST.size());
        }

        //if the first player went delete the list of words and letter and load them with our saved data...
        if (mTurnData.hadFirstTurn != null) {
            if (mTurnData.hadFirstTurn) {
                bl.make_words_with_letters.clear();
            }
        }
        for (int i = 0; i < mTurnData.make_words_with_letterST.size(); i++) {
            bl.make_words_with_letters.add(i, mTurnData.make_words_with_letterST.get(i));
        }

        if (mTurnData.playername1 == null) {
            fullName1 = mMatch.getParticipants().get(0).getDisplayName().split("\\s+");
            mTurnData.playername1 = fullName1[0];

            //keeps track of the player
            mTurnData.myParticipantIdST = mMatch.getParticipants().get(0).getParticipantId();
        }
        gp1.getFirstName(mTurnData.playername1);
        fullName1 = mTurnData.playername1.split("\\s+");


        if (mTurnData.playerprofile1 != null) {
            gp1.getStringProf(mTurnData.playerprofile1);
        } else {
            gp1.setNullProfilePic();
        }

        if (mTurnData.playerpoints1 == null) {
            mTurnData.playerpoints1 = "Score: 0";
        }
        gp1.setScore(mTurnData.playerpoints1);

        if (mTurnData.playername2 == null) {
            fullName2 = mMatch.getParticipants().get(1).getDisplayName().split("\\s+");
            mTurnData.playername2 = fullName2[0];
        }
        gp2.getFirstName(mTurnData.playername2);
        fullName2 = mTurnData.playername2.split("\\s+");

        if (mTurnData.playerprofile1 == null) {
            mTurnData.playerprofile1 = mMatch.getParticipants().get(0).getIconImageUrl();
        }
        playerPhotoUrl1 = mTurnData.playerprofile1;

        if (mTurnData.playerprofile2 == null) {
            mTurnData.playerprofile2 = mMatch.getParticipants().get(1).getIconImageUrl();
        }
        playerPhotoUrl2 = mTurnData.playerprofile2;

        if (mTurnData.playerprofile2 != null) {
            gp2.getStringProf(mTurnData.playerprofile2);
        } else {
            gp2.setNullProfilePic();
        }

        if (mTurnData.playerpoints2 == null) {
            mTurnData.playerpoints2 = "Score: 0";
            mTurnData.my_list_counterST = bl.my_list_counter;
            mTurnData.secondPlayerFinalTurn = false;
            mTurnData.secondPlayerRematch = false;
        }
        gp2.setScore(mTurnData.playerpoints2);

        bl.my_list_counter = mTurnData.my_list_counterST;

        if (mTurnData.secondPlayerRematch != null) {
            if (mTurnData.secondPlayerRematch) {
                mTurnData.myParticipantIdST = getNextParticipantId();
                secondPlayerRematch = true;
            }
        }
        //Create the next round when player one has turn
        if (mTurnData.myParticipantIdST != null && mTurnData.myParticipantIdST.equals("p_1")) {
            mTurnData.roundCounter++;
        }

        if (mTurnData.roundCounter == 0) { //then second player initiated rematch
            mTurnData.secondPlayerRematch = true;
            mTurnData.myParticipantIdST = "p_1";
            mTurnData.roundCounter++;
            secondPlayerRematch = true;
        }

        if (mTurnData.myParticipantIdST != null) {
            if (!viewEndOfGame && !viewEndOfGame2 && !secondPlayerEndedGame) {
                if (mTurnData.myParticipantIdST.equals("p_1")) {
                    gp1.setCurrentPlayerGreen();
                } else {
                    gp2.setCurrentPlayerGreen();
                }
            }
        }

        if (!myTurn) {
            for (int i = 0; i < bl.button_list.size(); i++) {
                bl.button_list.get(i).setClickable(false);
            }
//            recyclerView = (RecyclerView) findViewById(R.id.recycleShare);
//            recyclerView.addOnItemTouchListener(
//                    new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            try {
//                                String type = "image/*";
//                                String mediaPath = Environment.getExternalStorageDirectory() + "/game_icon1.png";
//                                AfterTurnComplete.createInstagramIntent(SkeletonActivity.this, type, mediaPath, mTurnData.messageCombo);
//
//                            } catch (Exception e) {
//                                // TODO: handle exception
//                                e.printStackTrace();
//                            }
//                        }
//                    })
//            );
//            recyclerAdapter = new RecyclerShareAdapter(this, getData());
//            recyclerView.setAdapter(recyclerAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
//            recyclerView.setVisibility(View.VISIBLE);
            buttonMessage = (Button) findViewById(R.id.button_message);
            buttonMessage.setText(shareMessageCombo);
            buttonMessage.setVisibility(View.VISIBLE);
            buttonMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String type = "image/*";
                        String mediaPath = Environment.getExternalStorageDirectory() + "/game_icon1.png";
//                        AfterTurnComplete.createInstagramIntent(SkeletonActivity.this, type, mediaPath, SkeletonActivity.shareMessageCombo);
                        onShareClick();
//                        initShareIntent("facebook.katana");
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            });

        }

        if (!secondPlayerEndedGame) {
            if (lastTurnSecondPlayer() && !viewEndOfGame && myTurn) {
//                Log.d("GAME OVER", "the game is over");
                //now when player views end of game he will be redirected to end of game view
                EditText e = (EditText) findViewById(R.id.edittextfrag);
                e.setHint("Game Over");
                mTurnData.messageCombo = "Thank you for playing!";
                messageAtEndOfGame("Game over", "Final Score\n\n" + mTurnData.playername1 + " - " + mTurnData.playerpoints1 +
                        "\n" + mTurnData.playername2 + " - " + mTurnData.playerpoints2);
            } else {
                if (lastTurnSecondPlayer() && viewEndOfGame2) {
//                    Log.d("GAME OVER", "please play again");
                    EditText e = (EditText) findViewById(R.id.edittextfrag);
                    e.setHint("Game Over");
                    mTurnData.messageCombo = "Thank you for playing!";
                    messageAtStartOfTurn("Game over", "Final Score\n\n" + mTurnData.playername1 + " - " + mTurnData.playerpoints1 +
                            "\n" + mTurnData.playername2 + " - " + mTurnData.playerpoints2);
                } else {
                    if ((mTurnData.turnCounter == 1 || mTurnData.turnCounter == 2) && myTurn) {
                        messageAtStartOfTurn("Round " + mTurnData.roundCounter, "Tap on the tiles to make a word.\nTap on a word to try and take it.\nSwipe to enter.\nGood Luck!");
                    } else {
                        if (mTurnData.list_of_lettersST.size() == 0) {
                            messageAtStartOfTurn("Round " + mTurnData.roundCounter, "Warning: Final Turn!\n\n" + mTurnData.shareNextTurnMessage);
                        } else {
                            if (mTurnData.list_of_lettersST.size() < 10 && myTurn) {
                                messageAtStartOfTurn("Round " + mTurnData.roundCounter, mTurnData.shareNextTurnMessage +
                                        "\n\nWarning: Tile counter at " + mTurnData.list_of_lettersST.size() +
                                        "\nGame ends when it reaches 0");
                            } else {
                                if (myTurn) {
                                    messageAtStartOfTurn("Round " + mTurnData.roundCounter, mTurnData.shareNextTurnMessage);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void initShareIntent(String type) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/jpeg");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Log.d("package name", info.activityInfo.packageName.toLowerCase());
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type)) {
                    share.putExtra(Intent.EXTRA_SUBJECT, "subject");
                    share.putExtra(Intent.EXTRA_TEXT, "your text");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/game_icon1.png"))); // Optional, just if you wanna share an image.
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return;

            startActivity(Intent.createChooser(share, "Select"));
        }
    }

    public void onShareClick() {
        Resources resources = getResources();
        String type = "image/*";
        String mediaPath = Environment.getExternalStorageDirectory() + "/game_icon1.png";

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Download in Google Play Store\nhttps://play.google.com/store/apps/details?id=me.tb.player");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Play Word Bandit - Multiplayer");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.setType(type);

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType(type);

        Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_chooser_text));

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("com.google.android.gm")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("twitter") || packageName.contains("facebook.katana") || packageName.contains("com.instagram.android")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType(type);
                // Add the URI and the caption to the Intent.
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (packageName.contains("twitter") || packageName.contains("instagram")) {
                    intent.putExtra(Intent.EXTRA_TEXT, shareMessageCombo + "\nDownload now https://play.google.com/store/apps/details?id=me.tb.player");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    //This controls all the sharing platform intents
    public static void createInstagramIntent(Context context, String type, String mediaPath, String caption) {

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI and the caption to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, caption);
        share.putExtra(Intent.EXTRA_SUBJECT, "Check this out");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Broadcast the Intent.
        context.startActivity(Intent.createChooser(share, "Share to"));
    }

    public void messageAtEndOfGame(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        finishGame();
                    }
                });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();


    }

    public void setCurrentPlayerGreen(TextView name, TextView score) {

        name.setTextColor(getResources().getColor(R.color.green));
        score.setTextColor(getResources().getColor(R.color.green));
    }

    @Override
    public void messageAtShuffle(String message) {
        if (myTurn) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set title
            alertDialogBuilder.setMessage(message);

            // set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            bl.shuffle();
                            isViewingBoardAfterTurn = true;
                            returnButtonsToUnclickedState();
                            clearTextFromEditTextFragment();
                            if (mTurnData.myParticipantIdST != null && mTurnData.myParticipantIdST.equals("p_1")) {
                                shareMessageCombo = mTurnData.playername1 + " decided to shuffle";
                            } else {
                                shareMessageCombo = mTurnData.playername2 + " decided to shuffle";
                            }
                            turnComplete();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {
                    returnButtonsToUnclickedState();
                    clearTextFromEditTextFragment();
                }
            });
            // create alert dialog
            mAlertDialog = alertDialogBuilder.create();

            // show it
            mAlertDialog.show();
        }

    }

    @Override
    public void messageAtPass(String message) {
        if (myTurn) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set title
            alertDialogBuilder.setMessage(message);

            // set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            isViewingBoardAfterTurn = true;
                            returnButtonsToUnclickedState();
                            clearTextFromEditTextFragment();
                            if (mTurnData.myParticipantIdST != null && mTurnData.myParticipantIdST.equals("p_1")) {
                                shareMessageCombo = mTurnData.playername1 + " decided to pass";
                            } else {
                                shareMessageCombo = mTurnData.playername2 + " decided to pass";
                            }
                            turnComplete();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {
                    returnButtonsToUnclickedState();
                    clearTextFromEditTextFragment();
                }
            });
            // create alert dialog
            mAlertDialog = alertDialogBuilder.create();

            // show it
            mAlertDialog.show();

        }
    }

    public void messageAtEndOfTurn(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        isViewingBoardAfterTurn = true;
                        wordAddedToListViewOfCurrentPlayer(wordUserCreatedFromEditTextFragment());
                        removeButtonTextIfClickedAndIsWord();
                        fillEmptyTilesWithNewLetters();
                        updateScoreOfBothPlayers();
                        returnButtonsToUnclickedState();
                        clearTextFromEditTextFragment();
                        turnComplete();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                returnButtonsToUnclickedState();
                clearTextFromEditTextFragment();
            }
        });
        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }


    public void messageAtEndOfStolenTurn(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        isViewingBoardAfterTurn = true;

                        //updates the listview and adds the word to the corresponding player
                        wordAddedToListViewOfCurrentPlayer(wordUserCreatedFromEditTextFragment());
                        //checks which list view clicked
                        if (isListView1Clicked()) {
                            wordIsStolenFromListView1();
                        } else {
                            wordIsStolenFromListView2();
                        }
                        updateScoreOfBothPlayers();
                        removeButtonTextIfClickedAndIsWord();
                        fillEmptyTilesWithNewLetters();
                        showOriginalGameScreen();
                        returnButtonsToUnclickedState();
                        clearTextFromEditTextFragment();
                        turnComplete();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                returnButtonsToUnclickedState();
                clearTextFromEditTextFragment();
                db.clearTheNewClickedButtons();
            }
        });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    @Override
    public boolean lastTurnFirstPlayer() {
        if (mTurnData.list_of_lettersST.size() == 0 && mTurnData.turnCounter != 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean lastTurnSecondPlayer() {
        //check if second player had final turn
        if (mTurnData.secondPlayerFinalTurn) {
            return true;
            //if first player had last turn then next player will have one more turn

        } else {
            if (lastTurnFirstPlayer()) {
                mTurnData.secondPlayerFinalTurn = true;
            }
        }

        return false;
    }

    // Helpful dialogs

    public void showSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
    }

    public void dismissSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.GONE);
    }

    @Override
    public void exitGameQuestion(String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        isViewingBoardAfterTurn = false;
                        enableFling = true;
                        nextTurn = 0;
                        Intent intent = new Intent(SkeletonActivity.this, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        SkeletonActivity.this.finish();
                    }
                })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //do nothing...return to game
                            }
                        });
        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }


    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    // Rematch dialog
    public void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you want a rematch?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sure, rematch!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton("No.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }

    public void startTheGame(Intent data) {

        if (data.getStringExtra("message").equals("saved")) {
            TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                updateMatch(match);
            }
        } else {

            // get the invitee list
            final ArrayList<String> invitees = data
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            Bundle autoMatchCriteria = null;

            int minAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            // Start the match
            Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            processResult(result);
                        }
                    });
            showSpinner();
        }
    }

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {
        mTurnData = new SkeletonTurn();

        findViewById(R.id.gameplay_layout).setVisibility(View.VISIBLE);
        getSupportActionBar().show();
        isViewingBoardAfterTurn = false;

        // Some basic turn data
        //game has not started yet
        mTurnData.hadFirstTurn = false;

        //game has not ended yet
        mTurnData.secondPlayerFinalTurn = false;

        //can't view end of game yet
        mTurnData.viewEndOfGame = false;

        mTurnData.viewEndOfGame2 = false;

        mTurnData.secondPlayerRematch = false;

        mTurnData.my_list_counterST = bl.my_list_counter;

        mTurnData.roundCounter = 0;
        mTurnData.turnCounter = 0;

        mMatch = match;

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        myParticipantId = mMatch.getParticipantId(playerId);

        Log.d("playerId", "Player Id: " + playerId);
        Log.d("myParticipantId", "Participant Id: " + myParticipantId);

        ArrayList<Participant> participants = mMatch.getParticipants();

        //keeps track of the player
        mTurnData.myParticipantIdST = participants.get(0).getParticipantId();

        playerPhotoUrl1 = participants.get(0).getIconImageUrl();

        if (playerPhotoUrl1 != null) {
            gp1.getStringProf(playerPhotoUrl1);
        } else {
            gp1.setNullProfilePic();
        }
        participants.get(0).getParticipantId();

        playername1 = participants.get(0).getDisplayName();
        fullName1 = playername1.split("\\s+");
        gp1.getFirstName(fullName1[0]);
        gp1.setCurrentPlayerGreen();

        if (participants.size() > 1) {
            playerPhotoUrl2 = participants.get(1).getIconImageUrl();

            if (playerPhotoUrl2 != null) {
                gp2.getStringProf(playerPhotoUrl2);
            } else {
                gp2.setNullProfilePic();
            }
            playername2 = participants.get(1).getDisplayName();
            fullName2 = playername2.split("\\s+");
            gp2.getFirstName(fullName2[0]);
            mTurnData.playername2 = fullName2[0];
            mTurnData.playerprofile2 = playerPhotoUrl2;
        }

        if (participants.size() < 2) {
            mTurnData.playername2 = "Matching...";
        }
        mTurnData.playername1 = fullName1[0];
        mTurnData.playerprofile1 = playerPhotoUrl1;


        mTurnData.playerpoints1 = ("Score: 0");
        mTurnData.playerpoints2 = ("Score: 0");

        mTurnData.tilesCounter = Integer.toString(bl.getList_of_letters().size());

        showSpinner();

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, match.getMatchId(),
                mTurnData.persist(), myParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
    }

    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
        Toast.makeText(getApplicationContext(), "New feature coming soon", Toast.LENGTH_SHORT).show();
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        myParticipantId = mMatch.getParticipantId(playerId);

        Log.d("playerId", "Player Id: " + playerId);
        Log.d("myParticipantId", "Participant Id: " + myParticipantId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (!myTurn && findViewById(R.id.gameplay_layout).getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(SkeletonActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            SkeletonActivity.this.finish();
        } else if (myTurn && findViewById(R.id.gameplay_layout).getVisibility() == View.VISIBLE
                && db.isVisible()) {
            bl.restoreCardView();
            showOriginalGameScreen();
            returnButtonsToUnclickedState();
            clearTextFromEditTextFragment();
            EditText e = (EditText) findViewById(R.id.edittextfrag);
            e.setHint("Tap and swipe");
        } else {
            if (myTurn && findViewById(R.id.gameplay_layout).getVisibility() == View.VISIBLE) {
                exitGameQuestion("Are you sure you want to exit?");
            } else {
                Intent intent = new Intent(SkeletonActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
            }
        }

    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {

        findViewById(R.id.gameplay_layout).setVisibility(View.VISIBLE);
        getSupportActionBar().show();
        isViewingBoardAfterTurn = false;

        mMatch = match;

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    myTurn = false;
                    viewEndOfGame = true;
                    viewEndOfGame2 = true;
                    lv1.setMyTurn(false);
                    lv2.setMyTurn(false);
                    gp1.setCurrentPlayerBlack();
                    gp2.setCurrentPlayerBlack();
                    mTurnData = SkeletonTurn.unpersist(mMatch.getData());
                    setGameplayUI();
                    int x = 0;
                    return;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                secondPlayerEndedGame = true;
                mTurnData = SkeletonTurn.unpersist(mMatch.getData());
                EditText e = (EditText) findViewById(R.id.edittextfrag);
                e.setHint("Game Over");
                mTurnData.messageCombo = "Thank you for playing!";
                gp1.setCurrentPlayerBlack();
                gp2.setCurrentPlayerBlack();
                lv1.setMyTurn(false);
                lv2.setMyTurn(false);
                showWarning("Game over", "Final Score\n\n" + mTurnData.playername1 + " - " + mTurnData.playerpoints1 +
                        "\n" + mTurnData.playername2 + " - " + mTurnData.playerpoints2);
                Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId());
                myTurn = false;
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                mTurnData = SkeletonTurn.unpersist(mMatch.getData());
                setGameplayUI();
                int x = 0;

                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:

                EditText e = (EditText) findViewById(R.id.edittextfrag);
                e.setHint("Turn Complete");
                myTurn = false;
                lv1.setMyTurn(false);
                lv2.setMyTurn(false);

                mTurnData = SkeletonTurn.unpersist(mMatch.getData());
                setGameplayUI();
                int y = 0;
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("Good initiative!",
                        "Still waiting for invitations.\n\nBe patient!");
        }

        mTurnData = null;

        setViewVisibility();
    }

    private void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
        dismissSpinner();

        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
            return;
        }

        isDoingTurn = false;

        showWarning("Match",
                "This match is canceled.  Click OK to redirect to home screen.");

    }

    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        dismissSpinner();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }


//    private void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
//        TurnBasedMatch match = result.getMatch();
//        dismissSpinner();
//        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
//            return;
//        }
//        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
//        showWarning("Left", "You've left this match.");
//    }


    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        dismissSpinner();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        //***********ADD THIS WHEN READY**************
        if (match.canRematch()) {
//            askForRematch();
//            return;
        }

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
            return;
        }

        setViewVisibility();
    }

    // Handle notification events.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        Toast.makeText(
                this,
                "An invitation has arrived from "
                        + invitation.getInviter().getDisplayName(), TOAST_DELAY)
                .show();
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        Toast.makeText(this, "An invitation was removed.", TOAST_DELAY).show();
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch match) {
        Toast.makeText(this, "A match was updated.", TOAST_DELAY).show();
    }

    @Override
    public void onTurnBasedMatchRemoved(String matchId) {
        Toast.makeText(this, "A match was removed.", TOAST_DELAY).show();

    }

    public void showErrorMessage(TurnBasedMatch match, int statusCode,
                                 int stringId) {

        showWarning("Warning", getResources().getString(stringId));
    }

    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        this,
                        "Stored action for later.  (Please remove this toast before release.)",
                        TOAST_DELAY).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, statusCode,
                        R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, statusCode,
                        R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, statusCode,
                        R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, statusCode, R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, statusCode,
                        R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(match, statusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }

    //wordAddedToListViewOfCurrentPlayer will check who's turn it is
    //if player 1 then all new words go to player 1's listview
    //if player 2 then all new words go to player 2's listview
    @Override
    public void wordAddedToListViewOfCurrentPlayer(String data) {
        if (mTurnData.myParticipantIdST != null && mTurnData.myParticipantIdST.equals("p_1"))
            lv1.addWordToListView1(data);
        else
            lv2.addWordToListView2(data);
    }

    @Override
    public void addLetterOfClickedButtonToEditTextFragment(String data) {
        et.changeEditText(data);
    }

    @Override
    public String wordUserCreatedFromEditTextFragment() {
        return et.sendEditText();
    }

    @Override
    public void removeLetterOfClickedButtonFromEditTextFragment(String data) {
        et.removeCharFromText(data);

    }

    @Override
    public void clearTextFromEditTextFragment() {
        et.clearEditText();

    }

    @Override
    public void makeGoldTilesForWordUserTryingToSteal(String data) {
        db.getTheWordUserTryingToSteal(data);

    }

    @Override
    public void showOriginalGameScreen() {
        manager.beginTransaction().show(lv1).commit();
        manager.beginTransaction().show(lv2).commit();
        manager.beginTransaction().show(gp1).commit();
        manager.beginTransaction().show(gp2).commit();
        manager.beginTransaction().hide(db).commit();
    }

    @Override
    public Boolean checkIfAllButtonsClickedInWordUserTryingToSteal() {
        return db.checkIfAllButtonsInNewWordClicked();
    }

    @Override
    public Boolean isTryingToStealWord() {
        return !lv1.isVisible();

    }

    @Override
    public void wordIsStolenFromListView1() {
        lv1.removeStolenWord();
    }

    @Override
    public void wordIsStolenFromListView2() {
        lv2.removeStolenWord();
    }

    @Override
    public Boolean isListView1Clicked() {
        return lv1.getFlag();
    }

    @Override
    public void setListView1ToFalse() {
        lv1.setFlag(false);
    }

    @Override
    public int lengthOfWordInEditTextFragment() {
        return et.editTextSize();
    }

    @Override
    public int lengthOfWordUserTryingToSteal() {
        return db.checkHowManyVisibleButtons();
    }

    @Override
    public void returnButtonsToUnclickedState() {
        bl.clearTheClickedButtons();
    }

    @Override
    public void fillEmptyTilesWithNewLetters() {
        bl.fillEmptyTilesWithNewLettersIfNoText();
    }

    @Override
    public void updateScoreOfBothPlayers() {
        gp1.setScore(lv1.updateScoreListView());
        gp2.setScore(lv2.updateScoreListView());
    }


    @Override
    public String getMessageCombo() {
        return shareMessageCombo;
    }

    @Override
    public void removeButtonTextIfClickedAndIsWord() {
        bl.removeTextFromButtonsIfClickedAndIsWord();
    }

    @Override
    public void turnComplete() {
        completeYouTurn();
    }

    public Boolean stolenWordIsRootOfOriginalWord() {
        String wordUserTryingToSteal = wordUserIsTryingToSteal();
        if (rules.wordUserTryingToStealEndWith_E(wordUserTryingToSteal)) {
            if (rules.wordUserIsTryingToSteal_R(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                    || rules.wordUserIsTryingToSteal_RE(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                    || rules.wordUserIsTryingToSteal_D(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                    || rules.wordUserIsTryingToSteal_S(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                    || rules.wordUserIsTryingToSteal_A(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                    || rules.wordUserIsTryingToSteal_Y(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())) {
                return true;
            }
        } else if (rules.wordUserIsTryingToSteal_S(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                || rules.wordUserIsTryingToSteal_RE(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                || rules.wordUserIsTryingToSteal_ING(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                || rules.wordUserIsTryingToSteal_ER(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                || rules.wordUserIsTryingToSteal_ED(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                || rules.wordUserIsTryingToSteal_A(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                || rules.wordUserIsTryingToSteal_Y(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())) {
            return true;
        }
        return false;
    }

    @Override
    public void fling(boolean isSwipe) {
        String etWord = wordUserCreatedFromEditTextFragment();
        if (myTurn && enableFling) {
            //first checks if trying to steal word
            if (isTryingToStealWord()) {
                if (checkIfAllButtonsClickedInWordUserTryingToSteal()) {
                    if (lengthOfWordInEditTextFragment() > lengthOfWordUserTryingToSteal()) {
                        if (!wordUserCreatedFromEditTextFragment().equals("")) {
                            if (!stolenWordIsRootOfOriginalWord()) {

                                if (mTurnData.myParticipantIdST != null && mTurnData.myParticipantIdST.equals("p_1")) {
                                    if (!isListView1Clicked()) {
                                        shareMessageTitle = "You took " + mTurnData.playername2 + "'s word!";
                                        if (isSwipe) {
                                            shareMessageBody = "Take with '" + etWord + "'?";
                                        } else {
                                            shareMessageBody = "Take with '" + etWord + "'?" +
                                                    "\n\nTip: Swipe across the screen to enter your word.";
                                        }
                                        shareMessageCombo = " I took '" + wordUserIsTryingToSteal() +
                                                "' with '" + etWord + "'";
                                        mTurnData.shareNextTurnMessage = mTurnData.playername1 + " took '" + wordUserIsTryingToSteal() + "' with '" + etWord + "'";

                                        messageAtEndOfStolenTurn(shareMessageBody);
                                    } else {
                                        shareMessageTitle = "You improved your word!";
                                        if (isSwipe) {
                                            shareMessageBody = "Improve with '" + etWord + "'?";
                                        } else {
                                            shareMessageBody = "Improve with '" + etWord + "'?" +
                                                    "\n\nTip: Swipe across the screen to enter your word.";
                                        }
                                        shareMessageCombo = " I improved '" + wordUserIsTryingToSteal() +
                                                "' with '" + etWord + "'";
                                        mTurnData.shareNextTurnMessage = mTurnData.playername1 + " improved '" + wordUserIsTryingToSteal() + "' with '" + etWord + "'";
                                        messageAtEndOfStolenTurn(shareMessageBody);
                                    }
                                } else {
                                    if (isListView1Clicked()) {
                                        shareMessageTitle = "You took " + mTurnData.playername1 + "'s word!";
                                        if (isSwipe) {
                                            shareMessageBody = "Take with '" + etWord + "'?";
                                        } else {
                                            shareMessageBody = "Take with '" + etWord + "'?" +
                                                    "\n\nTip: Swipe across the screen to enter your word.";
                                        }
                                        shareMessageCombo = " I took '" + wordUserIsTryingToSteal() +
                                                "' with '" + etWord + "'";
                                        mTurnData.shareNextTurnMessage = mTurnData.playername2 + " took '" + wordUserIsTryingToSteal() + "' with '" + etWord + "'";
                                        messageAtEndOfStolenTurn(shareMessageBody);
                                    } else {
                                        shareMessageTitle = "You improved your word!";
                                        if (isSwipe) {
                                            shareMessageBody = "Improve with '" + etWord + "'?";
                                        } else {
                                            shareMessageBody = "Improve with '" + etWord + "'?" +
                                                    "\n\nTip: Swipe across the screen to enter your word.";
                                        }
                                        shareMessageCombo = " I improved '" + wordUserIsTryingToSteal() +
                                                "' with '" + etWord + "'";
                                        mTurnData.shareNextTurnMessage = mTurnData.playername2 + " improved '" + wordUserIsTryingToSteal() + "' with '" + etWord + "'";
                                        messageAtEndOfStolenTurn(shareMessageBody);
                                    }
                                }


                            } else {
                                Toast.makeText(this, "You cannot take " + wordUserIsTryingToSteal() + " with " + et.getTextFromEditText() +
                                        "\nSame root", Toast.LENGTH_SHORT).show();
                                returnButtonsToUnclickedState();
                                clearTextFromEditTextFragment();
                                db.clearTheNewClickedButtons();
                            }
                        } else {
                            Toast.makeText(this, et.getTextFromEditText() + " is not a word", Toast.LENGTH_SHORT).show();
                            returnButtonsToUnclickedState();
                            clearTextFromEditTextFragment();
                            db.clearTheNewClickedButtons();
                        }
                    } else {
                        Toast.makeText(this, "You can only take " + wordUserIsTryingToSteal() + " with a longer word", Toast.LENGTH_SHORT).show();
                        returnButtonsToUnclickedState();
                        clearTextFromEditTextFragment();
                        db.clearTheNewClickedButtons();
                    }
                } else {
                    if (!et.getTextFromEditText().equals(""))
                        Toast.makeText(this, "All blue letters must be selected", Toast.LENGTH_SHORT).show();
                    returnButtonsToUnclickedState();
                    clearTextFromEditTextFragment();
                    db.clearTheNewClickedButtons();
                }
            } else {
                //checks if making normal word
                if (!wordUserCreatedFromEditTextFragment().equals("")) {

                    shareMessageTitle = "You made a word!";
                    if (isSwipe) {
                        shareMessageBody = "Make the word '" + etWord + "'?";
                    } else {
                        shareMessageBody = "Make the word '" + etWord + "'?" +
                                "\n\nTip: Swipe across the screen to enter your word.";
                    }
                    shareMessageCombo = " My word is '" + etWord + "'";
                    if (mTurnData.myParticipantIdST != null && mTurnData.myParticipantIdST.equals("p_1")) {
                        mTurnData.shareNextTurnMessage = mTurnData.playername1 + " made the word '" + etWord + "'";
                    } else {
                        mTurnData.shareNextTurnMessage = mTurnData.playername2 + " made the word '" + etWord + "'";
                    }
                    messageAtEndOfTurn(shareMessageBody);

                } else {
                    if (!et.getTextFromEditText().equals(""))
                        Toast.makeText(this, et.getTextFromEditText() + " is not a word", Toast.LENGTH_SHORT).show();
                    returnButtonsToUnclickedState();
                    clearTextFromEditTextFragment();
                }
            }
        }
    }

    @Override
    public String wordUserIsTryingToSteal() {
        int x = 0;
        if (isListView1Clicked()) { //if true, get word from first listview
            int a = 0; //just for debugging purposes...delete
            return lv1.wordUserTryingToStealFromListView1();
        } else { //if false, then get word from second listview
            int yx = 0; //just for debugging purposes...delete
            return lv2.wordUserTryingToStealFromListView2();
        }
    }

    @Override
    public int getTilesRemainingInt() {
        Log.d("letters", "There are" + bl.list_of_letters.size());
        return bl.list_of_letters.size();
    }

    @Override
    public void removeCV() {
        bl.removeCardView();
    }

    @Override
    public void addCV() {
        bl.restoreCardView();
    }

    @Override
    public void finishGame() {
        onFinishClicked();
    }
}