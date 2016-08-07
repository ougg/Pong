package oug.com.pong;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable{
    SurfaceHolder holder;
    Thread gameThread = null;
    boolean isRunning=false;
    GameModel gameModel;

    public GameView(Context context, GameModel gameModel) {
        super(context);
        holder = getHolder();
        this.gameModel=gameModel;

    }

    @Override
    public void run() {
        while(isRunning){
            if(!holder.getSurface().isValid()){
                continue;
            }
            Paint white = new Paint();
            white.setStyle(Paint.Style.FILL);
            white.setColor(Color.WHITE);
            Canvas c = holder.lockCanvas();
            c.drawARGB(255,0,0,0);
            c.drawRect(0,gameModel.getPaddleAY(), gameModel.getPaddleWidth(),gameModel.getPaddleAY()+gameModel.getPaddleHeight(),white);
            c.drawRect(gameModel.getScreenWidth()-gameModel.getPaddleWidth(),gameModel.getPaddleBY(), gameModel.getScreenWidth(),gameModel.getPaddleBY()+gameModel.getPaddleHeight(),white);
            c.drawCircle(gameModel.getBallX()+gameModel.getDiameter()/2,gameModel.getBallY()+gameModel.getDiameter()/2,gameModel.getDiameter()/2,white);
            holder.unlockCanvasAndPost(c);
        }
    }

    public void pause(){
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

    public void resume(){
        isRunning=true;
        gameThread = new Thread(this);
        gameThread.start();
    }


}
