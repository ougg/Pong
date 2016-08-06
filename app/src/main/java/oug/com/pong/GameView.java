package oug.com.pong;


import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable{
    SurfaceHolder holder;
    Thread gameThread = null;
    boolean isRunning=false;
    public GameView(Context context) {
        super(context);
        holder = getHolder();
    }

    @Override
    public void run() {
        while(isRunning){
            if(!holder.getSurface().isValid()){
                continue;
            }

            Canvas c = holder.lockCanvas();
            //draw things
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
