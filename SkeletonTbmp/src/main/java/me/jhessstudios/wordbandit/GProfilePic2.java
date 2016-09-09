package me.jhessstudios.wordbandit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by Juhess88 on 2/24/2015.
 */
public class GProfilePic2 extends Fragment {

    CommunicatorGame comm8;

    TextView score;

    TextView txt;

    ImageView img;

    Bitmap mIcon11=null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.g_prof_pic_fragment2,
                container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm8 = (CommunicatorGame)getActivity();

        score = (TextView) getActivity().findViewById(R.id.gScore2);

        img = (ImageView) getActivity().findViewById(R.id.gProfPic2);
        txt = (TextView) getActivity().findViewById(R.id.gName2);
    }

    public void setCurrentPlayerGreen(){

        txt.setTextColor(getResources().getColor(R.color.primaryColor));
        score.setTextColor(getResources().getColor(R.color.primaryColor));
    }

    public void setCurrentPlayerBlack(){

        txt.setTextColor(getResources().getColor(R.color.black));
        score.setTextColor(getResources().getColor(R.color.black));
    }

    public TextView getScore() {
        return score;
    }

    public TextView getTxt() {
        return txt;
    }

    public void getName(String data){
        txt.setText(data);
    }

    public void getFirstName(String data){
        txt.setText(data);
    }

    public void getProfPic(Bitmap data){
        img.setImageBitmap(data);
    }

    public void setNullProfilePic(){
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.blankprofile);
        img.setImageBitmap(comm8.getCircleBitmap((Bitmap.createScaledBitmap(largeIcon, 150, 150, false))));
    }

    public void getStringProf(String data){

        new LoadProfileImage(img).execute(data);
    }

    /**
     * Background Async task to load user profile picture from url
     * */
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
            bmImage.setImageBitmap(comm8.getCircleBitmap(Bitmap.createScaledBitmap(result, 150, 150, false)));
        }
    }


    public void setScore(String data){
        score.setText(data);
    }
}

