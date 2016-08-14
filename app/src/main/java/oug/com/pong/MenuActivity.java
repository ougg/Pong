package oug.com.pong;

import android.content.Intent;
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
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.selectMode)));
    }
    public void showDifficultyLevels(View view){
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.selectDifficulty)));
    }
    public void newSurvivalGame(View view){
        startNewGame(GameModes.SURVIVAL,-1);
    }

    public void newClassicGame(View view){
        switch(view.getId()){
            case R.id.difficultyEasyButton:
                startNewGame(GameModes.CLASSIC,GameModes.EASY);
                break;
            case R.id.difficultyMediumButton:
                startNewGame(GameModes.CLASSIC,GameModes.MEDIUM);
                break;
            case R.id.difficultyHardButton:
                startNewGame(GameModes.CLASSIC,GameModes.HARD);
                break;
            default:
                break;
        }
    }

    public void startNewGame(int mode, int difficulty){
        Intent intent = new Intent(this,GameActivity.class);
        intent.putExtra("GAME_MODE",mode);
        intent.putExtra("GAME_DIFFICULTY",difficulty);
        startActivity(intent);
    }

}
