package oug.com.pong;

public class GameModel {
    private int ballXSpeed,ballYSpeed,paddleSpeed,maxYSpeed=2;
    private int scoreA=0,scoreB=0;
    private int ballX,ballY, ballDiameter;
    private double ballXAcceleration,ballYAcceleration;
    private int paddleAY,paddleBY,paddleWidth,paddleHeight;
    private int screenWidth, screenHeight;
    private int framesCount=0;
    private boolean playerStarts = true, paddleCollided=false, serviceDelay=false, service=false;
    private long startTime;

    public GameModel(int screenWidth,int screenHeight){
        this.screenWidth =screenWidth;
        this.screenHeight =screenHeight;
        ballDiameter=screenHeight/20;
        paddleHeight=screenHeight/5;
        paddleWidth=paddleHeight/6;
    }

    public void moveBall(){
        ballX+=Math.round(ballXAcceleration*ballXSpeed);

        ballY+=Math.round(ballYAcceleration*ballYSpeed);
    }

    public void movePaddleA(int x){
        paddleAY+=x*paddleSpeed;
        if(paddleAY<0)
            paddleAY=0;
        if(paddleAY+paddleHeight> screenHeight)
            paddleAY= screenHeight -paddleHeight;
    }

    public void checkCollisions(){

        if((ballY<=0 || ballY>=(screenHeight - ballDiameter))&&!serviceDelay){
            ballYAcceleration*=-1;
            if(ballY<=0)
                ballY=0;
            else
                ballY= screenHeight - ballDiameter;
        }
        //CHECK IF A PADDLE WAS COLLIDED IN LAST 30 FRAMES - HELPS TO AVOID MULTIPLE COLLISION DETECTIONS
        if(paddleCollided){
            if(framesCount<30){
                framesCount++;
                return;
            }
            else{
                paddleCollided=false;
                framesCount=0;
            }
        }

        //PADDLE A COLLISION
        if(ballX<=paddleWidth && ballY+ ballDiameter >=paddleAY && ballY <= paddleAY+paddleHeight){

            if(service){
                ballXAcceleration*=2;
                service=false;
            }

            if(ballY+(ballDiameter /2) >= paddleAY && ballY+(ballDiameter /2) <= paddleAY + paddleHeight){
                ballXAcceleration*=-1;
                ballYAcceleration=((float)(ballY+(ballDiameter /2)-paddleAY-paddleHeight/2)/(paddleHeight/2))*maxYSpeed;

                if(ballYAcceleration*ballYSpeed<1&&ballYAcceleration>=0)
                    ballYAcceleration=1/(double)ballYSpeed;
                if(ballYAcceleration*ballYSpeed>-1&&ballYAcceleration<0)
                    ballYAcceleration=-1/(double)ballYSpeed;

                paddleCollided=true;
                return;
            }

            if(ballY<paddleAY){
                if(Math.pow(paddleWidth-ballX- ballDiameter /2, 2)+Math.pow(paddleAY-ballY- ballDiameter /2, 2)<=Math.pow(ballDiameter /2, 2)){
                    ballXAcceleration*=-1;
                    ballYAcceleration=-1*maxYSpeed;
                    paddleCollided=true;
                    return;
                }
            }
            else{
                if(Math.pow(paddleWidth-ballX- ballDiameter /2, 2)+Math.pow(paddleAY+paddleHeight-ballY- ballDiameter /2, 2)<=Math.pow(ballDiameter /2, 2)){
                    ballXAcceleration*=-1;
                    ballYAcceleration=maxYSpeed;
                    paddleCollided=true;
                    return;
                }
            }
        }

        //PADDLE B COLLISION
        if(ballX>= screenWidth -paddleWidth- ballDiameter && ballY+ ballDiameter >=paddleBY && ballY <= paddleBY+paddleHeight){

            if(service){
                ballXAcceleration*=2;
                service=false;
            }

            if(ballY+(ballDiameter /2) >= paddleBY && ballY+(ballDiameter /2) <= paddleBY + paddleHeight){
                ballXAcceleration*=-1;
                ballYAcceleration=((float)(ballY+(ballDiameter /2)-paddleBY-paddleHeight/2)/(paddleHeight/2))*maxYSpeed;

                if(ballYAcceleration*ballYSpeed<1&&ballYAcceleration>=0)
                    ballYAcceleration=1/(double)ballYSpeed;
                if(ballYAcceleration*ballYSpeed>-1&&ballYAcceleration<0)
                    ballYAcceleration=-1/(double)ballYSpeed;

                paddleCollided=true;
                return;
            }

            if(ballY<paddleBY){
                if(Math.pow(screenWidth -paddleWidth-ballX- ballDiameter /2, 2)+Math.pow(paddleBY-ballY- ballDiameter /2, 2)<=Math.pow(ballDiameter /2, 2)){
                    ballXAcceleration*=-1;
                    ballYAcceleration=-1*maxYSpeed;
                    paddleCollided=true;
                    return;
                }
            }
            else{
                if(Math.pow(screenWidth -paddleWidth-ballX- ballDiameter /2, 2)+Math.pow(paddleBY+paddleHeight-ballY- ballDiameter /2, 2)<=Math.pow(ballDiameter /2, 2)){
                    ballXAcceleration*=-1;
                    ballYAcceleration=maxYSpeed;
                    paddleCollided=true;
                    return;
                }
            }
        }

        if((ballX<=0 || ballX>= screenWidth - ballDiameter)&& !serviceDelay){
            if(ballX<=0)
                scoreB++;
            else
                scoreA++;

            newService();
        }
    }

    public void newGame(){
        paddleBY=(screenHeight -paddleHeight)/2;
        newService();
        startTime=System.currentTimeMillis();
    }

    public void newService(){
        ballXAcceleration=0;
        ballYAcceleration=0;
        ballY=-2* ballDiameter;
        ballX=(screenWidth - ballDiameter)/2;
        paddleAY = (screenHeight -paddleHeight)/2;

        Thread thread = new Thread(){

            public void run(){
                serviceDelay=true;
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                }

                ballX=(screenWidth - ballDiameter)/2;
                ballY=(screenHeight - ballDiameter)/2;
                if(playerStarts)
                    ballXAcceleration=0.5;
                else
                    ballXAcceleration=-0.5;
                service=true;

                playerStarts=!playerStarts;
                if(Math.random()<0.5)
                    ballYAcceleration=(float)Math.random();
                else
                    ballYAcceleration=-1*(float)Math.random();
                serviceDelay=false;
            }
        };
        thread.start();
    }

    public void AI(){

        if(!serviceDelay && ballX < 3*screenWidth/4){
            if(paddleAY+(paddleHeight/4)>ballY+ ballDiameter /2){
                movePaddleA(-1);
            }
            if(paddleAY+(3*paddleHeight/4)<ballY+ ballDiameter /2){
                movePaddleA(1);
            }
        }
    }

    public void setSpeed(int speed){
        ballXSpeed=speed;
        ballYSpeed=ballXSpeed/3;
        paddleSpeed=ballXSpeed*4/9;
    }

    public void setDifficulty(int diff){
        switch(diff){
            case GameModes.EASY:
                setSpeed(10);
                break;
            case GameModes.MEDIUM:
                setSpeed(13);
                break;
            case GameModes.HARD:
                setSpeed(17);
                break;
            default:
                break;
        }
    }

    public void setPaddleBY(int value){
        paddleBY=value;
        if(paddleBY<0)
            paddleBY=0;
        if(paddleBY+paddleHeight> screenHeight)
            paddleBY= screenHeight -paddleHeight;
    }
    public void setPaddleWidth(int value){
        paddleWidth=value;
    }
    public void setPaddleHeight(int value){
        paddleHeight=value;
    }
    public void setBallDiameter(int value){
        ballDiameter =value;
    }
    public void setScreenWidth(int value){
        screenWidth =value;
    }
    public void setScreenHeight(int value){
        screenHeight =value;
    }

    public int getBallX(){
        return ballX;
    }
    public int getBallY(){
        return ballY;
    }
    public int getPaddleAY(){
        return paddleAY;
    }
    public int getPaddleBY(){
        return paddleBY;
    }
    public int getPaddleHeight(){
        return paddleHeight;
    }
    public int getPaddleWidth(){
        return paddleWidth;
    }
    public int getScreenHeight(){
        return screenHeight;
    }
    public int getScreenWidth(){
        return screenWidth;
    }
    public int getDiameter(){
        return ballDiameter;
    }
    public int getScoreA(){
        return scoreA;
    }
    public int getScoreB(){
        return scoreB;
    }
    public int getSpeed(){
        return ballXSpeed;
    }
    public long getTime(){
        return System.currentTimeMillis()-startTime;
    }
}

