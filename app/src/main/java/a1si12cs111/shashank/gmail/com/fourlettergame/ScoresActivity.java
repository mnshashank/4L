package a1si12cs111.shashank.gmail.com.fourlettergame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoresActivity extends AppCompatActivity {

    TextView scoreActual, highestActual;
    Button playAgainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_scores);

        scoreActual = (TextView) findViewById(R.id.score_actual);
        highestActual = (TextView) findViewById(R.id.highest_actual);
        playAgainButton = (Button) findViewById(R.id.play_again);

        Intent intent = getIntent();
        String score = intent.getStringExtra("score");

        scoreActual.setText(score);

        SharedPreferences prefs = getSharedPreferences("HIGHESTSCORE", MODE_PRIVATE);
        String restoredScore = prefs.getString("score", null);
        if (restoredScore != null) {
            highestActual.setText(restoredScore);
        } else {
            highestActual.setText("0");
        }

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
        startActivity(intent);
    }
}
