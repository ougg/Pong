package oug.com.pong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ViewFlipper;

public class MenuActivity extends AppCompatActivity {
    ViewFlipper viewFlipper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        viewFlipper=(ViewFlipper) findViewById(R.id.viewFlipper);

    }

    public void playClicked(View view){
        Log.i("testing","play clicked");
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.selectMode)));
    }
    public void showDifficultyLevels(View view){
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.selectDifficulty)));
    }
    public void newSurvivalGame(View view){

    }

    public void newClassicGame(View view){
        switch(view.getId()){
            case R.id.difficultyEasyButton:
                Log.i("testing","easy");
                break;
            case R.id.difficultyMediumButton:
                Log.i("testing","medium");
                break;
            case R.id.difficultyHardButton:
                Log.i("testing","hard");
                break;
            default:
                break;
        }
    }

}
