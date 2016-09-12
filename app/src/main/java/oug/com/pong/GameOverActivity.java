package oug.com.pong;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GameOverActivity extends AppCompatActivity {
    ViewFlipper viewFlipper;
    String[][] highScore = new String[10][2];
    long time;
    File hSFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        boolean gameWon = intent.getBooleanExtra("GAME_WON",false);
        time = intent.getLongExtra("TIME",-2);

        if(time==-2){
            Toast.makeText(this,R.string.error,Toast.LENGTH_SHORT);
            finish();
        }

        viewFlipper = (ViewFlipper) findViewById(R.id.gameOverViewFlipper);

        if(time==-1){
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.classicGame)));
            TextView gameOver = (TextView) findViewById(R.id.gameOverTextView);
            if(gameWon)
                gameOver.setText(R.string.gameWon);
            else
                gameOver.setText(R.string.gameLost);
        }
        else {
            if(loadHighScore()){
                viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.survivalGame)));
                TextView timeText = (TextView) findViewById(R.id.timeTextView);
                timeText.setText(String.format(String.format("%02d:%02d:%03d", time/60000,(time/1000)%60,time%1000 )));

                //if player made it to the high score, display "enter your name" editText
                if(time>Long.parseLong(highScore[highScore.length-1][1])){
                    EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
                    nameEditText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public boolean loadHighScore(){
        hSFile = new File(this.getFilesDir(), "hsfile");

        try {
            if(hSFile.createNewFile()){
                PrintWriter pw = new PrintWriter(hSFile);
                for(int i=0;i<10;i++){
                    pw.println("--- 0");
                }
                pw.close();
            }

            //read data from file and put scores in the array
            Scanner scan = new Scanner(hSFile);
            for(int i=0;i<highScore.length;i++){
                if(scan.hasNextLine()){
                    highScore[i] = scan.nextLine().split(" ");

                }
            }
            scan.close();

            return true;
        } catch (IOException e) {
            Toast.makeText(this,R.string.error,Toast.LENGTH_SHORT);
            finish();
        }
        return false;
    }



    public void updateHighScore(String name, long time){
        highScore[highScore.length-1][0]=name;
        highScore[highScore.length-1][1]=time+"";

        for(int i = highScore.length-1;i>0;i--){
            if(Long.parseLong(highScore[i][1])>Long.parseLong(highScore[i-1][1])){
                String[] temp = highScore[i-1];
                highScore[i-1]= highScore[i];
                highScore[i]=temp;
            }
            else
                break;
        }

        //write updated high score to the file
        try {
            PrintWriter pw = new PrintWriter(hSFile);
            for(String[] record:highScore){
                pw.println(record[0]+" "+ record[1]);
            }
            pw.close();
        } catch (IOException e) {}

    }

    public void showHighScore(View view){
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        if(nameEditText.getVisibility()==View.VISIBLE){
            updateHighScore(nameEditText.getText().toString(),time);
        }

        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.highScore)));
        TextView namesView = (TextView) findViewById(R.id.namesView);
        TextView scoresView = (TextView) findViewById(R.id.scoresView);


        for(String[] record:highScore) {
            namesView.append(record[0] + "\n");
            long time = Long.parseLong(record[1]);
            if (time > 0)
                scoresView.append(String.format("%02d:%02d:%03d \n", time / 60000, (time / 1000) % 60, time % 1000));
            else
                scoresView.append("---\n");
        }
    }

    public void continueToMain(View view){
        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
