package me.tb.player;

import android.app.Activity;
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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Juhess88 on 5/24/2015.
 */
public class SignInActivity extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnInvitationReceivedListener, OnTurnBasedMatchUpdateReceivedListener {

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // For our intents
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_LOOK_AT_MATCHES = 10001;
    final static int RC_SELECT_PLAYERS = 10000;

    ImageView img;
    Boolean isFromNotification = false;
    Bitmap mIcon11 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_sign_in);

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
        findViewById(R.id.startMatchButton).setOnClickListener(this);
        findViewById(R.id.checkGamesButton).setOnClickListener(this);
        findViewById(R.id.rulesButton).setOnClickListener(this);
        findViewById(R.id.findButton).setOnClickListener(this);

        img = (ImageView) findViewById(R.id.gImage);

        copyIcon();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                mSignInClicked = true;
//                mTurnBasedMatch = null;
                findViewById(R.id.login_layout).setVisibility(View.GONE);
                findViewById(R.id.secret_layout).setVisibility(View.VISIBLE);
                mGoogleApiClient.connect();
                break;
            case R.id.sign_out_button:
                mSignInClicked = false;
                findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.secret_layout).setVisibility(View.GONE);
                Games.signOut(mGoogleApiClient);
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
                break;
            case R.id.startMatchButton:
                //we either send a message to skeleton activity
                //that we are starting new game or continuing saved game
                findViewById(R.id.secret_layout).setVisibility(View.GONE);
                Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient,
                        1, 1, false);
                startActivityForResult(intent, RC_SELECT_PLAYERS);

                break;
            case R.id.checkGamesButton:
                //we either send a message to skeleton activity
                //that we are starting new game or continuing saved game
                findViewById(R.id.secret_layout).setVisibility(View.GONE);
                Intent intent2 = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
                startActivityForResult(intent2, RC_LOOK_AT_MATCHES);
                break;

            case R.id.rulesButton:
                Intent intent3 = new Intent(SignInActivity.this, HowToPlayActivity.class);
                startActivity(intent3);
                break;
            case R.id.findButton:
                Intent intent4 = new Intent(SignInActivity.this, HowToFindPlayersActivity.class);
                startActivity(intent4);
        }
    }

    // This function is what gets called when you return from either the Play
    // Games built-in inbox, or else the create game built-in interface.
    @Override
    public void onActivityResult(int request, int response, final Intent data) {
        super.onActivityResult(request, response, data);
        if (request == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (response == Activity.RESULT_OK) {
                mGoogleApiClient.connect();

            } else {
                BaseGameUtils.showActivityResultError(this, request, response, R.string.signin_other_error);
                findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.secret_layout).setVisibility(View.GONE);
            }
        } else if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                findViewById(R.id.secret_layout).setVisibility(View.VISIBLE);
                // user canceled
                return;
            }

            findViewById(R.id.progressLayout2).setVisibility(View.VISIBLE);
            Intent intent = new Intent(SignInActivity.this, SkeletonActivity.class);
            intent.putExtra("message", "saved");
            intent.putExtras(data);
            startActivity(intent);

        } else if (request == RC_SELECT_PLAYERS) {
            // Returned from 'Select players to Invite' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                findViewById(R.id.secret_layout).setVisibility(View.VISIBLE);
                return;
            }

            findViewById(R.id.progressLayout2).setVisibility(View.VISIBLE);
            Intent intent = new Intent(SignInActivity.this, SkeletonActivity.class);
            intent.putExtra("message", "new");
            intent.putExtras(data);
            startActivity(intent);
        }
    }

    public void setViewVisibility() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (!isFromNotification) {
                findViewById(R.id.login_layout).setVisibility(View.GONE);
                findViewById(R.id.secret_layout).setVisibility(View.VISIBLE);

                String fullname = Games.Players.getCurrentPlayer(
                        mGoogleApiClient).getDisplayName();
                String firstname[] = fullname.split("\\s+");

                ((TextView) findViewById(R.id.name_field)).setText(firstname[0]);

                Player player = Games.Players.getCurrentPlayer(mGoogleApiClient);

                String playerPhotoUrl = player.getIconImageUrl();

                if (playerPhotoUrl != null)
                    new LoadProfileImage(img).execute(playerPhotoUrl);
                else {
                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.blankprofile);
                    img.setImageBitmap(getCircleBitmap((Bitmap.createScaledBitmap(largeIcon, 200, 200, false))));
                }

            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Retrieve the TurnBasedMatch from the connectionHint
        if (connectionHint != null) {
            isFromNotification = true;
            findViewById(R.id.secret_layout).setVisibility(View.GONE);
            Intent intent = new Intent(SignInActivity.this, SkeletonActivity.class);
            intent.putExtra("data", connectionHint);
            startActivity(intent);
            finish();
        }
        setViewVisibility();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // Already resolving
            Log.d("SignInActivity", "onConnectionFailed(): ignoring connection failure, already resolving.");
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

    @Override
    public void onInvitationReceived(Invitation invitation) {
        Toast.makeText(
                this,
                "An invitation has arrived from "
                        + invitation.getInviter().getDisplayName(), Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onInvitationRemoved(String s) {

    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        Toast.makeText(this, "A match was updated.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {

    }


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
            try{
                bmImage.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(result, 200, 200, false)));
            } catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                mSignInClicked = false;
                findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.secret_layout).setVisibility(View.GONE);
            }
        }
    }

    private void copyIcon() {
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

}
