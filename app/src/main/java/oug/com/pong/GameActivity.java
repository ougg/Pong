package oug.com.pong;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener, Runnable{
    GameView gameView;
    GameModel gameModel;
    Thread gameThread = null;
    boolean isRunning=false;
    int previousY=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        gameModel = new GameModel(width,height);
        gameView = new GameView(this,gameModel);
        gameView.setOnTouchListener(this);
        setContentView(gameView);

        gameModel.setSpeed(7);
        gameModel.newGame();

    }

    @Override
    protected void onPause() {
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
        isRunning=true;
        gameThread = new Thread(this);
        gameThread.start();
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
        while(isRunning){
            gameModel.moveBall();
            gameModel.AI();
            gameModel.checkCollisions();
            gameView.draw();
        }
    }
}
