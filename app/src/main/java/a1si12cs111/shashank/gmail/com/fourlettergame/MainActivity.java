package a1si12cs111.shashank.gmail.com.fourlettergame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //TODO : send the last word not answered to the score activity
    //TODO : add more words
    //TODO : correctly shuffle the words in the question
    //TODO : handle counter while resume
    //TODO : animations
    //TODO : redesign the initial page.

    TextView topLeft, topRight, bottomLeft, bottomRight, firstButton, secondButton, thirdButton, fourthButton;
    int timerTick;
    ImageButton resetButton;
    TextView textView, timerText;
    String formedWord = "";
    String jsonString = "";
    int positionCounter = 0, jsonWordCounter = -1;
    String word = "";
    JSONArray jsonArray = null;
    LinearLayout mainGrid, resultGrid;
    Handler handler;
    Runnable r1,r2,r3;
    Integer score = 0;
    ArrayList<Integer> occuredRandomNumbers = new ArrayList<Integer>();
    CountDownTimer countDownTimer, tickTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        timerTick = 31000;
        SharedPreferences.Editor editor = getSharedPreferences("TICK", MODE_PRIVATE).edit();
        editor.putString("tick", "false");
        editor.apply();
        r1 = new Runnable() {

            @Override
            public void run() {
                mainGrid.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                resetButton.setVisibility(View.VISIBLE);
                try {
                    clearResultImages();
                    loadNewWord(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        r2 = new Runnable() {

            @Override
            public void run() {
                mainGrid.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                resetButton.setVisibility(View.VISIBLE);
                try {
                    clearResultImages();
                    loadNewWord(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        r3 = new Runnable() {
            @Override
            public void run() {
                gunShot();
            }
        };

        textView = (TextView) findViewById(R.id.test_text);
        timerText = (TextView) findViewById(R.id.timer_text);
        topLeft = (TextView) findViewById(R.id.top_left);
        resetButton = (ImageButton) findViewById(R.id.reset_button);
        topRight = (TextView) findViewById(R.id.top_right);
        bottomLeft = (TextView) findViewById(R.id.bottom_left);
        bottomRight = (TextView) findViewById(R.id.bottom_right);
        firstButton = (TextView) findViewById(R.id.first_image);
        secondButton = (TextView) findViewById(R.id.second_image);
        thirdButton = (TextView) findViewById(R.id.third_image);
        fourthButton = (TextView) findViewById(R.id.fourth_image);
        mainGrid = (LinearLayout) findViewById(R.id.main_grid);
        resultGrid = (LinearLayout) findViewById(R.id.result_grid);



        startPlaying();

        topLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateResultImages(0, topLeft);
            }
        });
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateResultImages(1, topRight);
            }
        });
        bottomLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateResultImages(2, bottomLeft);
            }
        });
        bottomRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateResultImages(3, bottomRight);
            }
        });

        //reset wrongly keyed in letters
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionCounter = 0;
                formedWord = "";
                try {
                    clearResultImages();
                    loadNewWord(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void startPlaying() {
        readySteadyGo();
        handler.postDelayed(r3, 4000);
    }

    private void gunShot() {
        mainGrid.setVisibility(View.VISIBLE);
        resultGrid.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.VISIBLE);

        //TODO : change to 30 seconds later : done
        countDownTimer = new CountDownTimer(timerTick, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTick = (int) millisUntilFinished;
                timerText.setText("" + millisUntilFinished / 1000);
                if (millisUntilFinished / 1000 < 7) {
                    timerText.setTextColor(Color.RED);
                }
            }

            public void onFinish() {
                Integer updatedScore = 0;
                timerText.setText("0");
                // logic for highest score and score via shared preferences and intents
                SharedPreferences prefs = getSharedPreferences("HIGHESTSCORE", MODE_PRIVATE);
                String restoredScore = prefs.getString("score", null);
                if (restoredScore != null) {
                    Integer restoredIntegerScore = Integer.valueOf(restoredScore);
                    if (restoredIntegerScore > score) {
                        updatedScore = restoredIntegerScore;
                    } else {
                        updatedScore = score;
                    }
                } else {
                    updatedScore = score;
                }
                SharedPreferences.Editor editor = getSharedPreferences("HIGHESTSCORE", MODE_PRIVATE).edit();
                editor.putString("score", updatedScore.toString());
                editor.apply();
                finish();
                Intent intent = new Intent(getApplicationContext(), ScoresActivity.class);
                intent.putExtra("score", score.toString());
                startActivity(intent);
            }
        }.start();

        vitalLogicInitFunction();
    }

    private void readySteadyGo() {

        tickTimer = new CountDownTimer(4500, 1000) {

            public void onTick(long millisUntilFinished) {
                textView.setVisibility(View.VISIBLE);
                switch ((int) millisUntilFinished / 1000) {
                    case 3:  textView.setText("Ready");
                        textView.setTextColor(Color.RED);
                        break;
                    case 2: textView.setText("Steady");
                        textView.setTextColor(Color.YELLOW);
                        break;
                    case 1: textView.setText("Go");
                        textView.setTextColor(Color.GREEN);
                        break;
                }

            }

            public void onFinish() {
                textView.setVisibility(View.INVISIBLE);
                //return;
            }
        }.start();
    }

    private void vitalLogicInitFunction() {
        try {
            jsonString = new DataUtil().getJsonFromFile(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO : make this logic time based : eventually
        try {
            jsonArray = new JSONArray(jsonString);
            loadNewWord(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadNewWord(Boolean incrementOrNot) throws JSONException {
        int count = getJsonWordCount(incrementOrNot);
        if (count > -1) {
            word = getWord(count,jsonArray);
        } else {
            word = "";
        }
        setAppropriateImage(word);
    }
    private int getJsonWordCount(Boolean incrementOrNot) {
        //improve the random number generator logic which is already known to me.
        int randomNumber;
        if (occuredRandomNumbers.size() < jsonArray.length()) {
            if (incrementOrNot == true) {
                while (true) {
                    randomNumber = new Random().nextInt(jsonArray.length());
                    if (!occuredRandomNumbers.contains(randomNumber)) {
                        occuredRandomNumbers.add(randomNumber);
                        break;
                    }
                }
                jsonWordCounter = randomNumber;
            }
            return jsonWordCounter;
        } else {
            return -1;
        }
    }

    private void populateResultImages(int index, TextView imageToShow) {
        Character characterAtIndex = getcharacterAtIndex(index);
        formedWord+=characterAtIndex.toString();
        imageToShow.setText("");
        imageToShow.setEnabled(false);
        setCompleteImage(getPositionCount(true), characterAtIndex);
        if (getPositionCount(false) == 4) {
            try {
                checkForCorrectness(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getWord(int index, JSONArray jsonArray) throws JSONException {

        JSONObject jsonObject = jsonArray.getJSONObject(index);
        return jsonObject.getString("word");
    }

    private void setAppropriateImage(String word) {

        if (word.length() > 0) {
            for (int i=0; i<word.length(); i++) {
                setImage(i+1, word.charAt(i)); //getMipMapImage(word.charAt(i))
            }
        }
    }

    private void setImage(int index, char character) {


        switch (index) {
            case 1 : topLeft.setText(String.valueOf(character));
                break;
            case 2 : topRight.setText(String.valueOf(character));
                break;
            case 3 : bottomLeft.setText(String.valueOf(character));
                break;
            case 4 : bottomRight.setText(String.valueOf(character));
                break;
        }
    }

    private void setCompleteImage(int index, char character) {


        switch (index) {
            case 1 : firstButton.setText(String.valueOf(character));
                break;
            case 2 : secondButton.setText(String.valueOf(character));
                break;
            case 3 : thirdButton.setText(String.valueOf(character));
                break;
            case 4 : fourthButton.setText(String.valueOf(character));
                break;
        }
    }

    private int getPositionCount(Boolean incrementOrNot) {
        if (incrementOrNot == true) {
            positionCounter ++;
        }
        /*if (positionCounter == 5) {
            positionCounter = 0;
        }*/
        return positionCounter;
    }

    private Character getcharacterAtIndex(int characterIndex) {
        return word.charAt(characterIndex);
    }

    private void clearResultImages() {
        firstButton.setText("");
        topLeft.setEnabled(true);
        secondButton.setText("");
        topRight.setEnabled(true);
        thirdButton.setText("");
        bottomLeft.setEnabled(true);
        fourthButton.setText("");
        bottomRight.setEnabled(true);

    }
    private void checkForCorrectness(JSONArray jsonArray) throws JSONException {

        JSONObject jsonObject = jsonArray.getJSONObject(getJsonWordCount(false));
        String correctWord = jsonObject.getString("correctWord");

        if (formedWord.equals(correctWord)) {
            textView.setText("CORRECT");
            textView.setTextColor(Color.GREEN);
            positionCounter = 0;
            formedWord = "";
            //clearResultImages();
            mainGrid.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            score ++;
            handler.postDelayed(r1, 500);
            //loadNewWord(true);
        } else {
            textView.setText("WRONG");
            textView.setTextColor(Color.RED);
            positionCounter = 0;
            formedWord = "";
            //clearResultImages();
            mainGrid.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            handler.postDelayed(r2, 500);
            //loadNewWord(false);
        }
    }

    @Override
    public void onBackPressed() {
        // preventing from going into the entryActivity as the timer is ticking
        if (handler != null) {
            handler.removeCallbacks(r1);
            handler.removeCallbacks(r2);
            handler.removeCallbacks(r3);
        }
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if(tickTimer != null) {
            tickTimer.cancel();
            tickTimer = null;
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(r1);
            handler.removeCallbacks(r2);
            handler.removeCallbacks(r3);
        }
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if(tickTimer != null) {
            tickTimer.cancel();
            tickTimer = null;
        }
        SharedPreferences.Editor editor = getSharedPreferences("TICK", MODE_PRIVATE).edit();
        editor.putString("tick", "true");
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("TICK", MODE_PRIVATE);
        String restoredScore = prefs.getString("tick", null);
        if (restoredScore != null && restoredScore.equals("true")) {
            gunShot();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("TICK", MODE_PRIVATE).edit();
        editor.putString("tick", "false");
        editor.apply();
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences("TICK", MODE_PRIVATE).edit();
        editor.putString("tick", "false");
        editor.apply();
    }*/
}
