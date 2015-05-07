package me.tb.player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Juhess88 on 4/8/2015.
 */
public class CheckLastTurnStatus extends Activity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_last_turn_status);

        btn = (Button) findViewById(R.id.backToHomeScreen);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckLastTurnStatus.this, SkeletonActivity.class);
                startActivity(intent);
            }
        });
    }
}
