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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
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
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * TBMPSkeleton: A minimalistic "game" that shows turn-based
 * multiplayer features for Play Games Services.  In this game, you
 * can invite a variable number of players and take turns editing a
 * shared state, which consists of single string.  You can also select
 * automatch players; all known players play before automatch slots
 * are filled.
 * <p/>
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
        View.OnClickListener, CommunicatorGame {

    public static final String TAG = "SkeletonActivity";
    final boolean D = true;

    private GestureDetector gestureScanner;

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // Current turn-based match
    private TurnBasedMatch mTurnBasedMatch;

    // Local convenience pointers
    public TextView mTurnTextView;

    private AlertDialog mAlertDialog;

    // For our intents
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    // How long to show toasts.
    final static int TOAST_DELAY = Toast.LENGTH_SHORT;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    public static boolean isViewingBoardAfterTurn = false;
    static boolean resetOnce = false;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;

    // This is the current match data after being unpersisted.
    // Do not retain references to match data once you have
    // taken an action on the match, such as takeTurn()
    public SkeletonTurn mTurnData;

    //keeps track if player 1's turn or player 2's turn
    String myParticipantId;

    //keeps track of saveInstanceState
    //true-new game do not save state
    //false-saved game save state
    Boolean newMatch = false;

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    String stringURL = "";

    Bitmap mIcon11 = null;

    ImageView img;

    Button doneButton;

    String[] fullName1, fullName2;
    String playerPhotoUrl1;
    String playerPhotoUrl2;
    String playername1;
    String playername2;

    EditTextFragment et;
    ButtonLayoutFragment bl;
    DynamicButtonsFragment db;
    GProfilePic1 gp1;
    GProfilePic2 gp2;
    ListView1 lv1;
    ListView2 lv2;
    CountdownFragment cf;
    StealTextFragment st;

    FragmentManager manager;

    RulesForStealingWords rules;

    Boolean myTurn = true;
    Boolean enableFling = true;

    Boolean secondPlayerEndedGame = false;

    Boolean viewEndOfGame = false;
    Boolean viewEndOfGame2 = false;

    int nextTurn = 0;

    static List<String> lv1View = new ArrayList<String>();
    static List<String> lv2View = new ArrayList<String>();
    static List<String> blView = new ArrayList<String>();
    static String pointview1;
    static String pointview2;
    static String tilesRemainingView;

    static List<String> listLetters = new ArrayList<String>();
    static String nameView1;
    static String nameView2;
    static String profView1;
    static String profview2;

    static String shareMessageTitle = "";
    static String shareMessageBody = "";
    static String shareMessageCombo = "";
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private RecyclerShareAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(): onCreate called");
        setContentView(R.layout.activity_main);


        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();


        // Setup signin and signout buttons
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        copyIcon();

        rules = new RulesForStealingWords();

        img = (ImageView) findViewById(R.id.gImage);

//        doneButton = (Button) findViewById(R.id.doneButton);

        gestureScanner = new GestureDetector(this, new MyGestureListener());


        et = new EditTextFragment();
        bl = new ButtonLayoutFragment();
        db = new DynamicButtonsFragment();
        gp1 = new GProfilePic1();
        gp2 = new GProfilePic2();
        lv1 = new ListView1();
        lv2 = new ListView2();
        cf = new CountdownFragment();
        st = new StealTextFragment();

        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.etToolbar, et, "EFrag");
        transaction.add(R.id.fragment_button_layout, bl, "BFrag");
        transaction.add(R.id.fragment_dynamic_button_layout, db, "DFrag").hide(db);
        transaction.add(R.id.fragment_google1, gp1, "GFrag1");
        transaction.add(R.id.fragment_google2, gp2, "GFrag2");
        transaction.add(R.id.fragment_listview1, lv1, "LFrag1");
        transaction.add(R.id.fragment_listview2, lv2, "LFrag2");
        transaction.add(R.id.fragment_countdown, cf, "CFrag");
        transaction.add(R.id.fragment_steal_me, st, "SFrag").hide(st);

        transaction.commit();

    }

    public static List<RecyclerShareModel> getData() {
        List<RecyclerShareModel> data = new ArrayList<>();
        int icon = R.drawable.shareicon;
        String title = shareMessageCombo;

        RecyclerShareModel current = new RecyclerShareModel();
        current.iconId = icon;
        current.title = title;
        data.add(current);

        return data;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureScanner.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "SkeletonActivity - onDown");
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float velocityThresh = 500;
            double distanceThresh = 75;
            float fling = (Math.abs(velocityX) + Math.abs(velocityY)) / 2;
            double distance = Math.sqrt(Math.pow(e2.getX() - e1.getX(), 2) + Math.pow(e2.getY() - e1.getY(), 2));

            if (D) {
//                Log.d(TAG, "fling: " + fling + ", e1X: " + e1.getX() + ", e1Y: " + e1.getY()
//                        + ", e2X: " + e2.getX() + ", e2Y: " + e2.getY() + ", distance: " + distance
//                        + ", vX: " + velocityX + " vY:" + velocityY);

                if (fling > velocityThresh && distance > distanceThresh && enableFling) {
                    Log.d("onFling", "fling");
                    fling();
                }
            }
            return true;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart(): onRestart called");

        if (isViewingBoardAfterTurn) {
            lv1.adapter1.clear();
            lv2.adapter2.clear();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart(): Connecting to Google APIs");
        mGoogleApiClient.connect();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState(): onRestoreInstanceState called");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume(): onResume called");

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause(): onPause called");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if(newMatch)
//            outState=null;
        Log.d(TAG, "onSaveInstanceState(): onSaveInstanceState called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): Disconnecting from Google APIs");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy(): onDestroy called");
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected(): Connection successful");

        // Retrieve the TurnBasedMatch from the connectionHint
        if (connectionHint != null) {
            mTurnBasedMatch = connectionHint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (mTurnBasedMatch != null) {
                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                    Log.d(TAG, "Warning: accessing TurnBasedMatch when not connected");
                }

                updateMatch(mTurnBasedMatch);
                return;
            }
        }
        setViewVisibility();

        // As a demonstration, we are registering this activity as a handler for
        // invitation and match events.

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
//        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
//        Games.TurnBasedMultiplayer.registerMatchUpdateListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended():  Trying to reconnect.");
        mGoogleApiClient.connect();
        setViewVisibility();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            // Already resolving
            Log.d(TAG, "onConnectionFailed(): ignoring connection failure, already resolving.");
            return;
        }

        // Launch the sign-in flow if the button was clicked or if auto sign-in is enabled
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;

            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult, RC_SIGN_IN,
                    getString(R.string.signin_other_error));
        }

        setViewVisibility();
    }

    //resets the game state so no duplicate info appears
    //when a new game is started without exiting the app
    public void reset() {
        //this section deals with if the user starts a new game without exiting the app
        //all the data needs to be removed or it will be loaded twice...
        Intent intent = new Intent(this, SkeletonActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.shuffle:
                if (findViewById(R.id.gameplay_layout).getVisibility() == View.VISIBLE)
                    if (!isViewingBoardAfterTurn)
                        bl.shuffle();
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

        MenuItem tile_item = menu.findItem(R.id.tile_count);
        tile_item.setTitle("Tiles Remaining: " + bl.getList_of_letters().size());

        if (findViewById(R.id.gameplay_layout).getVisibility() == View.GONE) {
            tile_item.setVisible(false);
            MenuItem shuffle = menu.findItem(R.id.shuffle);
            shuffle.setVisible(false);
        } else {
            tile_item.setVisible(true);
            MenuItem shuffle = menu.findItem(R.id.shuffle);
            shuffle.setVisible(true);
        }

        return true;
    }

    // Displays your inbox. You will get back onActivityResult where
    // you will need to figure out what you clicked on.
    public void onCheckGamesClicked(View view) {
        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }

    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.
    public void onStartMatchClicked(View view) {
        //we want it to set a brand new game so do not load previous state
        newMatch = true;
        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient,
                1, 1, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    // Create a one-on-one automatch game.
    public void onQuickMatchClicked(View view) {

        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                1, 1, 0);

        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                .setAutoMatchCriteria(autoMatchCriteria).build();

        showSpinner();

        // Start the match
        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> cb = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
            @Override
            public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                processResult(result);
            }
        };
        Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(cb);
    }

    // In-game controls

    // Cancel the game. Should possibly wait until the game is canceled before
    // giving up on the view.
    public void onCancelClicked(View view) {
        showSpinner();
        Games.TurnBasedMultiplayer.cancelMatch(mGoogleApiClient, mMatch.getMatchId())
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.CancelMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.CancelMatchResult result) {
                        processResult(result);
                    }
                });
        isDoingTurn = false;
        setViewVisibility();
    }

    // Leave the game during your turn. Note that there is a separate
    // Games.TurnBasedMultiplayer.leaveMatch() if you want to leave NOT on your turn.
//    public void onLeaveClicked(View view) {
//        showSpinner();
//        String nextParticipantId = getNextParticipantId();
//
//        Games.TurnBasedMultiplayer.leaveMatchDuringTurn(mGoogleApiClient, mMatch.getMatchId(),
//                nextParticipantId).setResultCallback(
//                    new ResultCallback<TurnBasedMultiplayer.LeaveMatchResult>() {
//            @Override
//            public void onResult(TurnBasedMultiplayer.LeaveMatchResult result) {
//                processResult(result);
//            }
//        });
//        setViewVisibility();
//    }

    // Finish the game. Sometimes, this is your only choice.
    public void onFinishClicked() {
        showSpinner();

        ArrayList<Participant> participants = mMatch.getParticipants();

        ParticipantResult opponentResult = null;
        ParticipantResult creatorResult = null;

        if (lv1.getPoint() > lv2.getPoint()) {
            opponentResult = new ParticipantResult(participants.get(0).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_WIN, 1);
            creatorResult = new ParticipantResult(participants.get(1).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_LOSS, 2);
        } else if (lv1.getPoint() < lv2.getPoint()) {
            opponentResult = new ParticipantResult(participants.get(0).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_LOSS, 1);
            creatorResult = new ParticipantResult(participants.get(1).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_WIN, 2);
        } else {
            opponentResult = new ParticipantResult(participants.get(0).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_TIE, 1);
            creatorResult = new ParticipantResult(participants.get(1).getParticipantId(),
                    ParticipantResult.MATCH_RESULT_TIE, 2);
        }

// mTurnData.persist() istead of mMatch.getData()
        Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId()
                , mMatch.getData(), creatorResult, opponentResult)
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });

        isDoingTurn = false;
        setViewVisibility();

        reset();
    }


    public void doShuffle(View view) {
        if (myTurn) {
            bl.shuffle();
        }
    }

    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.
    public void completeYouTurn() {
        showSpinner();

        lv1View.clear();
        lv2View.clear();
        listLetters.clear();

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
            blView.add(i, bl.button_list.get(i).getText().toString());
        }

        //saves list_of_words state
        mTurnData.list_of_lettersST.clear();
        for (int i = 0; i < bl.list_of_letters.size(); i++) {
            mTurnData.list_of_lettersST.add(i, bl.list_of_letters.get(i));
            listLetters.add(i, bl.list_of_letters.get(i));
        }

        //saves make words with letter state state
        mTurnData.make_words_with_letterST.clear();
        for (int i = 0; i < bl.make_words_with_letters.size(); i++) {
            mTurnData.make_words_with_letterST.add(i, bl.make_words_with_letters.get(i));
        }

        //add stuff here for mTurnData.playerpoints1
        mTurnData.playerpoints1 = gp1.score.getText().toString();
        pointview1 = gp1.score.getText().toString();
        pointview2 = gp2.score.getText().toString();
        //add stuff here for mTurnData.playerpoints2
        mTurnData.playerpoints2 = gp2.score.getText().toString();

        //pass the tiles data for the next player
        mTurnData.tilesCounter = Integer.toString(bl.getList_of_letters().size());

        mTurnData.messageCombo = shareMessageCombo;

        tilesRemainingView = Integer.toString(bl.getList_of_letters().size());

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
//        cf.cd.cancel();
//        Log.d("CountdownTimer", "Countdown timer cancelled");

        reset();
    }

    // Sign-in, Sign out behavior

    // Update the visibility based on what state we're in.
    public void setViewVisibility() {
        boolean isSignedIn = (mGoogleApiClient != null) && (mGoogleApiClient.isConnected());


        findViewById(R.id.login_layout).setVisibility(View.GONE);
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);

        if (!isSignedIn) {
            findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.matchup_layout).setVisibility(View.GONE);
            findViewById(R.id.gameplay_layout).setVisibility(View.GONE);

            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            return;        }


        ((TextView) findViewById(R.id.name_field)).setText(Games.Players.getCurrentPlayer(
                mGoogleApiClient).getDisplayName());

        Player player = Games.Players.getCurrentPlayer(mGoogleApiClient);

        String playerPhotoUrl = player.getIconImageUrl();

//        getStringProf(playerPhotoUrl);
        if (playerPhotoUrl != null)
            new LoadProfileImage(img).execute(playerPhotoUrl);
        else {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.blankprofile);
            img.setImageBitmap(getCircleBitmap((Bitmap.createScaledBitmap(largeIcon, 200, 200, false))));
        }

        if (isDoingTurn) {
            findViewById(R.id.matchup_layout).setVisibility(View.GONE);
            findViewById(R.id.gameplay_layout).setVisibility(View.VISIBLE);
            getSupportActionBar().show();
            isViewingBoardAfterTurn = false;
        } else {
            if (isViewingBoardAfterTurn) {
                Intent intent = new Intent(SkeletonActivity.this, AfterTurnComplete.class);
                intent.putStringArrayListExtra("button_letter", (ArrayList) bl.getButton_letter());
                intent.putStringArrayListExtra("list1", (ArrayList) lv1View);
                intent.putStringArrayListExtra("list2", (ArrayList) lv2View);
                intent.putExtra("score1", pointview1);
                intent.putExtra("score2", pointview2);
                intent.putExtra("name1", nameView1);
                intent.putExtra("name2", nameView2);
                intent.putExtra("pic1", profView1);
                intent.putExtra("pic2", profview2);
                intent.putExtra("tiles", tilesRemainingView);
                intent.putExtra("share", shareMessageCombo);
                startActivity(intent);
                SkeletonActivity.this.finish();

            } else {
                findViewById(R.id.matchup_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.gameplay_layout).setVisibility(View.GONE);
                getSupportActionBar().hide();
                isViewingBoardAfterTurn = false;
            }

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

//    public void getStringProf(String data){
//        stringURL=data;
//
//        // by default the profile url gives 50x50 px image only
//        // we can replace the value with whatever dimension we want by
//        // replacing sz=X
//        stringURL = stringURL.substring(0,
//                stringURL.length() - 2)
//                + 1000;
//
//        new LoadProfileImage(img).execute(stringURL);
//    }

    /**
     * Background Async task to load user profile picture from url
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(result, 200, 200, false)));
        }
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


    // Switch to gameplay view.
    public void setGameplayUI() {
        isDoingTurn = true;
        setViewVisibility();

        shareMessageCombo = mTurnData.messageCombo;

        //Create the next round when player one has turn
        if (mTurnData.myParticipantIdST.equals("p_1")) {
            mTurnData.roundCounter++;
        }
        // Create the next turn
        mTurnData.turnCounter += 1;

        nextTurn = mTurnData.turnCounter + 1;

        for (int i = 0; i < mTurnData.data1.size(); i++) {
            lv1.adapter1.add(mTurnData.data1.get(i));
        }
        for (int i = 0; i < mTurnData.data2.size(); i++) {
            lv2.adapter2.add(mTurnData.data2.get(i));
        }

        for (int i = 0; i < mTurnData.button_letters.size(); i++) {
            bl.button_list.get(i).setText(mTurnData.button_letters.get(i));
        }

        //if the first player went delete the list of words and letter and load them with our saved data...
        if (mTurnData.hadFirstTurn)
            bl.list_of_letters.clear();
        for (int i = 0; i < mTurnData.list_of_lettersST.size(); i++) {
            bl.list_of_letters.add(i, mTurnData.list_of_lettersST.get(i));
        }

        //if the first player went delete the list of words and letter and load them with our saved data...
        if (mTurnData.hadFirstTurn)
            bl.make_words_with_letters.clear();
        for (int i = 0; i < mTurnData.make_words_with_letterST.size(); i++) {
            bl.make_words_with_letters.add(i, mTurnData.make_words_with_letterST.get(i));
        }

        gp1.getFirstName(mTurnData.playername1);
        nameView1 = mTurnData.playername1;

        if (mTurnData.playerprofile1 != null) {
            gp1.getStringProf(mTurnData.playerprofile1);
        } else {
            gp1.setNullProfilePic();
        }
        gp1.setScore(mTurnData.playerpoints1);

        gp2.getFirstName(mTurnData.playername2);
        nameView2 = mTurnData.playername2;

        if (mTurnData.playerprofile2 != null) {
            gp2.getStringProf(mTurnData.playerprofile2);
        } else {
            gp2.setNullProfilePic();
        }
        gp2.setScore(mTurnData.playerpoints2);

        profView1 = mTurnData.playerprofile1;
        profview2 = mTurnData.playerprofile2;
        bl.my_list_counter = mTurnData.my_list_counterST;

        if (!myTurn) {
            for (int i = 0; i < bl.button_list.size(); i++) {
                bl.button_list.get(i).setClickable(false);
                recyclerView = (RecyclerView) findViewById(R.id.recycleShare);
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                try {
                                    String type = "image/*";
                                    String mediaPath = Environment.getExternalStorageDirectory() + "/game_icon1.png";
                                    AfterTurnComplete.createInstagramIntent(SkeletonActivity.this, type, mediaPath, mTurnData.messageCombo);

                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                }
                            }
                        })
                );
                recyclerAdapter = new RecyclerShareAdapter(this, getData());
                recyclerView.setAdapter(recyclerAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        if (!secondPlayerEndedGame) {
            if (lastTurnSecondPlayer() && !viewEndOfGame) {
                //now when player views end of game he will be redirected to end of game view
                messageAtEndOfGame("Game over sir", "Better luck next time");
            } else {
                if (lastTurnSecondPlayer() && viewEndOfGame2) {
                    messageAtStartOfTurn("Game over master", "Better luck next game!");
                } else {
                    if (mTurnData.turnCounter == 1) {
                        messageAtStartOfTurn("Round " + mTurnData.roundCounter, "Good Luck!");
                    } else {
                        if (mTurnData.list_of_lettersST.size() == 0) {
                            messageAtStartOfTurn("Round " + mTurnData.roundCounter, "Warning: Final Turn!\n\n" + mTurnData.shareNextTurnMessage);
                        } else {
                            if (mTurnData.list_of_lettersST.size() < 10) {
                                messageAtStartOfTurn("Round " + mTurnData.roundCounter, mTurnData.shareNextTurnMessage +
                                        "\n\nWarning: Tile counter at: " + mTurnData.list_of_lettersST.size()+
                                        "\nGame end when tile counter at 0");
                            } else {
                                messageAtStartOfTurn("Round " + mTurnData.roundCounter, mTurnData.shareNextTurnMessage);
                            }
                        }
                    }
                }
            }
        }
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

    public void messageAtEndOfTurn(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
//                        if (lastTurnSecondPlayer()) {
//                            finishGame();
//                        } else {
                        isViewingBoardAfterTurn = true;
                        wordAddedToListViewOfCurrentPlayer(wordUserCreatedFromEditTextFragment());
                        removeButtonTextIfClickedAndIsWord();
                        fillEmptyTilesWithNewLetters();
                        updateScoreOfBothPlayers();
                        returnButtonsToUnclickedState();
                        clearTextFromEditTextFragment();
                        turnComplete();
//                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                returnButtonsToUnclickedState();
                clearTextFromEditTextFragment();
            }
        });

//


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
                        // if this button is clicked, close
                        // current activity
//                        if (lastTurnSecondPlayer()) {
//                            finishGame();
//                        } else {
                        isViewingBoardAfterTurn = true;

                        //updates the listview and adds the word to the corresponding player
                        wordAddedToListViewOfCurrentPlayer(wordUserCreatedFromEditTextFragment());
                        //checks which list view clicked
                        if (isListView1Clicked())
                            wordIsStolenFromListView1();
                        else
                            wordIsStolenFromListView2();
                        //changes score but checks if facebook game or google game
                        //                  if(getGoogleCounter()==1)
                        updateScoreOfBothPlayers();
                        //                  else
                        //                      fChangeScore();
                        removeButtonTextIfClickedAndIsWord();
                        fillEmptyTilesWithNewLetters();
                        showOriginalGameScreen();
                        returnButtonsToUnclickedState();
                        clearTextFromEditTextFragment();
                        turnComplete();
//                        }
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
        Log.d("Tiles", "" + mTurnData.list_of_lettersST.size());
        if (mTurnData.list_of_lettersST.size() == 0 && mTurnData.turnCounter != 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean lastTurnSecondPlayer() {
        //check if second player had final turn
        if (mTurnData.secondPlayerFinalTurn == true) {
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

    void copyIcon() {
        File f = new File(Environment.getExternalStorageDirectory() + "/game_icon1.png");
        Log.e(null, f.getPath());
        if (!f.exists()) {
            FileOutputStream out = null;
            try {
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.game_icon);
                out = new FileOutputStream(f);
                icon.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
                //fuck it
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        //do nothing
                    }
                }
            }

        }

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
                        Intent intent = new Intent(SkeletonActivity.this, SkeletonActivity.class);
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
                        // if this button is clicked, close
                        // current activity
                        reset();
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
                                reset();
                            }
                        });

        alertDialogBuilder.show();
    }

    // This function is what gets called when you return from either the Play
    // Games built-in inbox, or else the create game built-in interface.
    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (request == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
//            if (response == Activity.RESULT_OK) {
//                mGoogleApiClient.connect();
//            } else {
//                BaseGameUtils.showActivityResultError(this, request, response, R.string.signin_other_error);
//            }
        } else if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                updateMatch(match);
            }

            Log.d(TAG, "Match = " + match);
        } else if (request == RC_SELECT_PLAYERS) {
            // Returned from 'Select players to Invite' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

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

        // Some basic turn data
        //game has not started yet
        mTurnData.hadFirstTurn = false;

        //game has not ended yet
        mTurnData.secondPlayerFinalTurn = false;

        //can't view end of game yet
        mTurnData.viewEndOfGame = false;

        mTurnData.viewEndOfGame2 = false;

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

        playerPhotoUrl2 = participants.get(1).getIconImageUrl();

        if (playerPhotoUrl2 != null) {
            gp2.getStringProf(playerPhotoUrl2);
        } else {
            gp2.setNullProfilePic();
        }
        playername2 = participants.get(1).getDisplayName();
        fullName2 = playername2.split("\\s+");
        gp2.getFirstName(fullName2[0]);

        mTurnData.playername1 = fullName1[0];
        mTurnData.playerprofile1 = playerPhotoUrl1;
        mTurnData.playername2 = fullName2[0];
        mTurnData.playerprofile2 = playerPhotoUrl2;

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
        showSpinner();
        Games.TurnBasedMultiplayer.rematch(mGoogleApiClient, mMatch.getMatchId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                        processResult(result);
                    }
                });
        mMatch = null;
        isDoingTurn = false;
        reset();
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
            Intent intent = new Intent(SkeletonActivity.this, SkeletonActivity.class);
            startActivity(intent);
            SkeletonActivity.this.finish();
        } else if (myTurn && findViewById(R.id.gameplay_layout).getVisibility() == View.VISIBLE
                && db.isVisible()) {
            showOriginalGameScreen();
            returnButtonsToUnclickedState();
            clearTextFromEditTextFragment();
            EditText e = (EditText) findViewById(R.id.edittextfrag);
            e.setHint("Tap and swipe");
        } else {
            if (myTurn && findViewById(R.id.gameplay_layout).getVisibility() == View.VISIBLE) {
                exitGameQuestion("Are you sure you want to exit?");
            } else {
                this.finish();
            }
        }

    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
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
//                    showWarning(
//                            "Game Over!",
//                            "Check the final results");
                    myTurn = false;
                    viewEndOfGame = true;
                    viewEndOfGame2 = true;
                    lv1.setMyTurn(false);
                    lv2.setMyTurn(false);
//                    manager.beginTransaction().show(sf).commit();
                    mTurnData = SkeletonTurn.unpersist(mMatch.getData());
                    setGameplayUI();
                    int x = 0;
                    return;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                secondPlayerEndedGame = true;
                showWarning("Game Over!", "Check the final results");
                Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId());

                //here


        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                mTurnData = SkeletonTurn.unpersist(mMatch.getData());
                setGameplayUI();
                int x = 0;
//                if (lastTurnSecondPlayer() && mTurnData.viewEndOfGame) {
//                    mTurnData.viewEndOfGame=false;
//                    //now when player views end of game he will be redirected to end of game view
//                    finishGame();
//                }
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
//                showWarning("Alas...", "It's not your turn.");

                EditText e = (EditText) findViewById(R.id.edittextfrag);
                e.setHint("Turn Complete");
//                manager.beginTransaction().show(sf).commit();
                myTurn = false;
                lv1.setMyTurn(false);
                lv2.setMyTurn(false);

                mTurnData = SkeletonTurn.unpersist(mMatch.getData());
                setGameplayUI();
                int y = 0;
                return;
//                break;
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

//        if (match.getStatus()==2) {
//            Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId())
//                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
//                        @Override
//                        public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
//                            processResult(result);
//                        }
//                    });
//        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                // Check to see the developer who's running this sample code read the instructions :-)
                // NOTE: this check is here only because this is a sample! Don't include this
                // check in your actual production app.
//                if (!BaseGameUtils.verifySampleSetup(this, R.string.app_id)) {
//                    Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
//                }

                mSignInClicked = true;
                mTurnBasedMatch = null;
                findViewById(R.id.sign_in_button).setVisibility(View.GONE);
                mGoogleApiClient.connect();
                break;
            case R.id.sign_out_button:
                mSignInClicked = false;
                Games.signOut(mGoogleApiClient);
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
                setViewVisibility();
                break;
        }
    }

    //wordAddedToListViewOfCurrentPlayer will check who's turn it is
    //if player 1 then all new words go to player 1's listview
    //if player 2 then all new words go to player 2's listview
    @Override
    public void wordAddedToListViewOfCurrentPlayer(String data) {
        if (mTurnData.myParticipantIdST.equals("p_1"))
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
        manager.beginTransaction().hide(st).commit();

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
                    || rules.wordUserIsTryingToSteal_D(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                    || rules.wordUserIsTryingToSteal_S(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                    || rules.wordUserIsTryingToSteal_A(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
                    || rules.wordUserIsTryingToSteal_Y(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())) {
                return true;
            }
        } else if (rules.wordUserIsTryingToSteal_S(wordUserTryingToSteal, wordUserCreatedFromEditTextFragment())
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
    public void fling() {
        String etWord = wordUserCreatedFromEditTextFragment();
        if (myTurn && enableFling) {
            //first checks if trying to steal word
            if (isTryingToStealWord()) {
                if (checkIfAllButtonsClickedInWordUserTryingToSteal()) {
                    if (lengthOfWordInEditTextFragment() > lengthOfWordUserTryingToSteal()) {
                        if (!wordUserCreatedFromEditTextFragment().equals("")) {
                            if (!stolenWordIsRootOfOriginalWord()) {

                                if (mTurnData.myParticipantIdST.equals("p_1")) {
                                    if (!isListView1Clicked()) {
                                        shareMessageTitle = "You stole " + mTurnData.playername2 + "'s word!";
                                        shareMessageBody = "Steal with '" + etWord + "'?";

                                        shareMessageCombo = shareMessageTitle + " You stole '" + wordUserIsTryingToSteal() +
                                                "' with '" + etWord + "'";
                                        mTurnData.shareNextTurnMessage = mTurnData.playername1 + " stole '" + wordUserIsTryingToSteal() + "' with '" + etWord + "'";
                                        ;
                                        messageAtEndOfStolenTurn(shareMessageBody);
                                    } else {
                                        shareMessageTitle = "You improved your word!";
                                        shareMessageBody = "Improve  with '" + etWord + "'?";
                                        shareMessageCombo = shareMessageTitle + " You improved '" + wordUserIsTryingToSteal() +
                                                "' with '" + etWord + "'";
                                        mTurnData.shareNextTurnMessage = mTurnData.playername1 + " improved '" + wordUserIsTryingToSteal() + "' with '" + etWord + "'";
                                        messageAtEndOfStolenTurn(shareMessageBody);
                                    }
                                } else {
                                    if (isListView1Clicked()) {
                                        shareMessageTitle = "You stole " + mTurnData.playername1 + "'s word!";
                                        shareMessageBody = "Steal with '" + etWord + "'?";
                                        shareMessageCombo = shareMessageTitle + " You stole '" + wordUserIsTryingToSteal() +
                                                "' with '" + etWord + "'";
                                        mTurnData.shareNextTurnMessage = mTurnData.playername2 + " stole '" + wordUserIsTryingToSteal() + "' with '" + etWord + "'";
                                        messageAtEndOfStolenTurn(shareMessageBody);
                                    } else {
                                        shareMessageTitle = "You improved your word!";
                                        shareMessageBody = "Improve with '" + etWord + "'?";
                                        shareMessageCombo = shareMessageTitle + " You improved '" + wordUserIsTryingToSteal() +
                                                "' with '" + etWord + "'";
                                        mTurnData.shareNextTurnMessage = mTurnData.playername2 + " improved '" + wordUserIsTryingToSteal() + "' with '" + etWord + "'";
                                        messageAtEndOfStolenTurn(shareMessageBody);
                                    }
                                }


                            } else {
                                Toast.makeText(this, "You cannot steal " + wordUserIsTryingToSteal() + " with " + et.getTextFromEditText() +
                                        "\nSame root", Toast.LENGTH_SHORT).show();
                                returnButtonsToUnclickedState();
//                                showOriginalGameScreen();
                                clearTextFromEditTextFragment();
                                db.clearTheNewClickedButtons();
                            }
                        } else {
                            Toast.makeText(this, et.getTextFromEditText() + " is not a word", Toast.LENGTH_SHORT).show();
                            returnButtonsToUnclickedState();
//                            showOriginalGameScreen();
                            clearTextFromEditTextFragment();
                            db.clearTheNewClickedButtons();
                        }
                    } else {
                        Toast.makeText(this, "You can only steal " + wordUserIsTryingToSteal() + " with a longer word", Toast.LENGTH_SHORT).show();
                        returnButtonsToUnclickedState();
//                        showOriginalGameScreen();
                        clearTextFromEditTextFragment();
                        db.clearTheNewClickedButtons();
                    }
                } else {
                    if (!et.getTextFromEditText().equals(""))
                        Toast.makeText(this, "Click on all gold tiles to steal " + wordUserIsTryingToSteal(), Toast.LENGTH_SHORT).show();
                    returnButtonsToUnclickedState();
//                    showOriginalGameScreen();
                    clearTextFromEditTextFragment();
                    db.clearTheNewClickedButtons();
                }
            } else {
                //checks if making normal word
                if (!wordUserCreatedFromEditTextFragment().equals("")) {

                    shareMessageTitle = "You made a word!";
                    shareMessageBody = "Make the word '" + etWord + "'?";
                    shareMessageCombo = shareMessageTitle + " Your word is '" + etWord + "'";
                    if (mTurnData.myParticipantIdST.equals("p_1")) {
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
//                    showOriginalGameScreen();
                }
            }
        }
//        else {
//            Toast.makeText(this, "Not your turn", Toast.LENGTH_SHORT).show();
//        }
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
    public void sendToStealText() {
        st.setText("" + wordUserIsTryingToSteal());
    }

    @Override
    public int getTilesRemainingInt() {
        Log.d("letters", "There are" + bl.list_of_letters.size());
        return bl.list_of_letters.size();
    }


    @Override
    public void finishGame() {
        onFinishClicked();
    }
}