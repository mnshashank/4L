package a1si12cs111.shashank.gmail.com.fourlettergame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EntryActivity extends AppCompatActivity {

    Button playStart;
    TextView highestScoreTag, highestScoreValue;
    LinearLayout highestLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        playStart = (Button) findViewById(R.id.play_start);
        highestScoreTag = (TextView) findViewById(R.id.highest_tag_main);
        highestScoreValue = (TextView) findViewById(R.id.highest_actual_main);
        highestLayout = (LinearLayout) findViewById(R.id.highest_score_layout_main);

        SharedPreferences prefs = getSharedPreferences("HIGHESTSCORE", MODE_PRIVATE);
        String restoredScore = prefs.getString("score", null);
        if (restoredScore != null) {
            highestScoreValue.setText(restoredScore);
        } else {
            highestLayout.setVisibility(View.INVISIBLE);
        }

        playStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
