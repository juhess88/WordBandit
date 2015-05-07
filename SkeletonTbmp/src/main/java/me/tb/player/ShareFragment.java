package me.tb.player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Juhess88 on 5/6/2015.
 */
public class ShareFragment extends Fragment {

    TextView txt;
    String message;
    String test;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.share_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txt=(TextView)getActivity().findViewById(R.id.shareText);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String type = "image/*";
                    String mediaPath = Environment.getExternalStorageDirectory() + "/game_icon1.png";
                    createInstagramIntent(type, mediaPath, message);

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });

    }

    //This controls all the sharing platform intents
    private void createInstagramIntent(String type, String mediaPath, String caption) {

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
        startActivity(Intent.createChooser(share, "Share to"));
    }

    public void getMessageCombo(String data){
        message=data;
        txt.setText(message);
    }
}
