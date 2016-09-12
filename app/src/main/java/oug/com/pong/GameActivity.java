package oug.com.pong;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener, Runnable{
    GameView gameView;
    GameModel gameModel;
    Thread gameThread = null;
    boolean isRunning=false;
    int previousY=0;
    int mode, difficulty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        gameModel = new GameModel(width, height);

        Intent intent = getIntent();
        mode = intent.getIntExtra("GAME_MODE", -1);
        difficulty = intent.getIntExtra("GAME_DIFFICULTY", -1);

        //check if extras are ok
        if (mode == -1 || (difficulty == -1 && mode == GameModes.CLASSIC)) {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            finish();
        }

        gameView = new GameView(this, gameModel, mode);
        gameView.setOnTouchListener(this);
        setContentView(gameView);

        //check if its the first instance of this activity - if so, start new game
        if(savedInstanceState==null) {
            if (mode == GameModes.CLASSIC)
                gameModel.setDifficulty(difficulty);
            else
                gameModel.setSpeed(gameModel.getScreenWidth() / 75);

            gameModel.newGame();
        }
        else{
            gameModel.setSpeed(savedInstanceState.getInt("SPEED"));
            gameModel.setScores(savedInstanceState.getInt("SCOREA"),savedInstanceState.getInt("SCOREB"));
            gameModel.setStartTime(savedInstanceState.getInt("STARTTIME"));
            gameModel.setCoordinates(savedInstanceState.getIntArray("COORDS"));
            gameModel.setFramesCount(savedInstanceState.getInt("FRAMESCOUNT"));
            gameModel.setFlags(savedInstanceState.getBooleanArray("FLAGS"));
            gameModel.setAcceleration(savedInstanceState.getDoubleArray("ACCELERATION"));
        }

    }

    @Override
    protected void onPause() {
        Log.i("testing","onpause");
        super.onPause();
        isRunning=false;
        while(true){
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        gameThread=null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("testing","onresume");
        isRunning=true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("SPEED",gameModel.getSpeed());
        savedInstanceState.putInt("SCOREA",gameModel.getScoreA());
        savedInstanceState.putInt("SCOREB",gameModel.getScoreB());
        savedInstanceState.putLong("STARTTIME",gameModel.getStartTime());
        savedInstanceState.putIntArray("COORDS",gameModel.getCoordinates());
        savedInstanceState.putInt("FRAMESCOUNT",gameModel.getFramesCount());
        savedInstanceState.putBooleanArray("FLAGS",gameModel.getFlags());
        savedInstanceState.putDoubleArray("ACCELERATION",gameModel.getAcceleration());
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                //set the starting position
                previousY=(int)motionEvent.getY();
            break;
            case  MotionEvent.ACTION_MOVE:
                //calculate the distance moved (and multiply by 1.5) and move a paddle
                gameModel.setPaddleBY(gameModel.getPaddleBY()+(int) 1.5*((int) motionEvent.getY()-previousY));
                //set current position as a starting position for next move event
                previousY=(int)motionEvent.getY();
            break;
            default:
                break;

        }
        return true;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while(isRunning){
            if(mode == GameModes.CLASSIC && (gameModel.getScoreA()>9||gameModel.getScoreB()>9)){
                isRunning=false;
                endGame(gameModel.getScoreB()>9,-1);
            }

            if(mode==GameModes.SURVIVAL){
                //speed up the game every 10 seconds
                if(System.currentTimeMillis()-startTime>=10000){
                    gameModel.setSpeed(gameModel.getSpeed()+2);
                    startTime = System.currentTimeMillis();
                }

                //if player lost a point, end game
                if(gameModel.getScoreA()>0){
                    isRunning=false;
                    endGame(false, gameModel.getTime());
                }
            }
            gameModel.moveBall();
            gameModel.AI();
            gameModel.checkCollisions();
            gameView.draw();
        }
    }

    public void endGame(boolean gameWon,long time){
        Intent intent = new Intent(this,GameOverActivity.class);
        intent.putExtra("GAME_WON",gameWon);
        intent.putExtra("TIME",time);
        startActivity(intent);
        finish();
    }
}
