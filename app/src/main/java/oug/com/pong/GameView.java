package oug.com.pong;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView{
    SurfaceHolder holder;
    GameModel gameModel;
    int mode;

    public GameView(Context context, GameModel gameModel, int mode) {
        super(context);
        holder = getHolder();
        this.gameModel=gameModel;
        this.mode=mode;
    }
    public void draw(){
        if(!holder.getSurface().isValid()){
            return;
        }
        Paint white = new Paint();
        white.setStyle(Paint.Style.FILL);
        white.setColor(Color.WHITE);
        white.setTextAlign(Paint.Align.CENTER);

        Canvas c = holder.lockCanvas();
        c.drawARGB(255,0,0,0);
        c.drawRect(0,gameModel.getPaddleAY(), gameModel.getPaddleWidth(),gameModel.getPaddleAY()+gameModel.getPaddleHeight(),white);
        c.drawRect(gameModel.getScreenWidth()-gameModel.getPaddleWidth(),gameModel.getPaddleBY(), gameModel.getScreenWidth(),gameModel.getPaddleBY()+gameModel.getPaddleHeight(),white);
        c.drawCircle(gameModel.getBallX()+gameModel.getDiameter()/2,gameModel.getBallY()+gameModel.getDiameter()/2,gameModel.getDiameter()/2,white);

        if (mode == GameModes.CLASSIC) {
            white.setTextSize(gameModel.getScreenHeight()/10);
            c.drawText(gameModel.getScoreA()+"",0,1,gameModel.getScreenWidth()/4, gameModel.getScreenHeight()/10,white);
            c.drawText(gameModel.getScoreB()+"",0,1,gameModel.getScreenWidth()*3/4, gameModel.getScreenHeight()/10,white);
        }else{
            white.setTextSize(gameModel.getScreenHeight()/15);
            long time = gameModel.getTime();
            String timeString = String.format("%02d:%02d:%03d", time/60000,(time/1000)%60,time%1000 );
            c.drawText(timeString,0,8,gameModel.getScreenWidth()/2, gameModel.getScreenHeight()/15,white);
        }
        holder.unlockCanvasAndPost(c);
    }
}
