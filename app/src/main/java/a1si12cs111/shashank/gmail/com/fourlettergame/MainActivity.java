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

    ImageButton topLeft, topRight, bottomLeft, bottomRight, firstButton, secondButton, thirdButton, fourthButton, resetButton;
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
        topLeft = (ImageButton) findViewById(R.id.top_left);
        resetButton = (ImageButton) findViewById(R.id.reset_button);
        topRight = (ImageButton) findViewById(R.id.top_right);
        bottomLeft = (ImageButton) findViewById(R.id.bottom_left);
        bottomRight = (ImageButton) findViewById(R.id.bottom_right);
        firstButton = (ImageButton) findViewById(R.id.first_image);
        secondButton = (ImageButton) findViewById(R.id.second_image);
        thirdButton = (ImageButton) findViewById(R.id.third_image);
        fourthButton = (ImageButton) findViewById(R.id.fourth_image);
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
        countDownTimer = new CountDownTimer(31000, 1000) {

            public void onTick(long millisUntilFinished) {
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

    private void populateResultImages(int index, ImageView imageToShow) {
        Character characterAtIndex = getcharacterAtIndex(index);
        formedWord+=characterAtIndex.toString();
        imageToShow.setImageResource(R.mipmap.ic_empty);
        imageToShow.setEnabled(false);
        setCompleteImage(getPositionCount(true), getMipMapImage(characterAtIndex));
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
                setImage(i+1, getMipMapImage(word.charAt(i)));
            }
        }
    }

    private void setImage(int index, int mipmapImage) {


        switch (index) {
            case 1 : topLeft.setImageResource(mipmapImage);
                break;
            case 2 : topRight.setImageResource(mipmapImage);
                break;
            case 3 : bottomLeft.setImageResource(mipmapImage);
                break;
            case 4 : bottomRight.setImageResource(mipmapImage);
                break;
        }
    }

    private void setCompleteImage(int index, int mipmapImage) {


        switch (index) {
            case 1 : firstButton.setImageResource(mipmapImage);
                break;
            case 2 : secondButton.setImageResource(mipmapImage);
                break;
            case 3 : thirdButton.setImageResource(mipmapImage);
                break;
            case 4 : fourthButton.setImageResource(mipmapImage);
                break;
        }
    }

    private int getMipMapImage(Character letter) {
        int mipMapImage = 0;
        switch (letter) {
            case 'A' :  mipMapImage = R.mipmap.ic_a;
                break;
            case 'B' : mipMapImage = R.mipmap.ic_b;
                break;
            case 'C' : mipMapImage = R.mipmap.ic_c;
                break;
            case 'D' : mipMapImage = R.mipmap.ic_d;
                break;
            case 'E' : mipMapImage = R.mipmap.ic_e;
                break;
            case 'F' : mipMapImage = R.mipmap.ic_f;
                break;
            case 'G' : mipMapImage = R.mipmap.ic_g;
                break;
            case 'H' : mipMapImage = R.mipmap.ic_h;
                break;
            case 'I' : mipMapImage = R.mipmap.ic_i;
                break;
            case 'J' : mipMapImage = R.mipmap.ic_j;
                break;
            case 'K' : mipMapImage = R.mipmap.ic_k;
                break;
            case 'L' : mipMapImage = R.mipmap.ic_l;
                break;
            case 'M' : mipMapImage = R.mipmap.ic_m;
                break;
            case 'N' : mipMapImage = R.mipmap.ic_n;
                break;
            case 'O' : mipMapImage = R.mipmap.ic_o;
                break;
            case 'P' : mipMapImage = R.mipmap.ic_p;
                break;
            case 'Q' : mipMapImage = R.mipmap.ic_q;
                break;
            case 'R' : mipMapImage = R.mipmap.ic_r;
                break;
            case 'S' : mipMapImage = R.mipmap.ic_s;
                break;
            case 'T' : mipMapImage = R.mipmap.ic_t;
                break;
            case 'U' : mipMapImage = R.mipmap.ic_u;
                break;
            case 'V' : mipMapImage = R.mipmap.ic_v;
                break;
            case 'W' : mipMapImage = R.mipmap.ic_w;
                break;
            case 'X' : mipMapImage = R.mipmap.ic_x;
                break;
            case 'Y' : mipMapImage = R.mipmap.ic_y;
                break;
            case 'Z' : mipMapImage = R.mipmap.ic_z;
                break;

        }
         return mipMapImage;
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
        firstButton.setImageResource(R.mipmap.ic_empty);
        topLeft.setEnabled(true);
        secondButton.setImageResource(R.mipmap.ic_empty);
        topRight.setEnabled(true);
        thirdButton.setImageResource(R.mipmap.ic_empty);
        bottomLeft.setEnabled(true);
        fourthButton.setImageResource(R.mipmap.ic_empty);
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
    }
}
