package com.example.surveyapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class WordSearchActivity extends AppCompatActivity {

    Button word1;
    Button word2;
    Button word3;
    GetTimeStamp timeStamps = new GetTimeStamp();
    CSVWriting csvWriter = new CSVWriting();
    String outputName;
    String answer;
    Button next;
    String[] posSize = new String[12];
    TextView prompt;

    QuestionBank questionBank;
    Question question;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordsearch);

        // Grabs output name from FirstPageActivity for CSVWriting
        Intent intent = getIntent();
        outputName = intent.getStringExtra(FirstPageActivity.EXTRA_OUTPUT);

        // read values for pos and size
        String[] posSize = question.getQuestionCode().split(",");

        word1 = findViewById(R.id.wordSearchWord1);
        word2 = findViewById(R.id.wordSearchWord2);
        word3 = findViewById(R.id.wordSearchWord3);
        prompt = findViewById(R.id.wordSearchPrompt);
        next = findViewById(R.id.wordSearchNext);
        word1.setBackgroundColor(Color.TRANSPARENT);
        word2.setBackgroundColor(Color.TRANSPARENT);
        word3.setBackgroundColor(Color.TRANSPARENT);
        prompt.setText(question.getInstruction());

        ImageView imageView = (ImageView) findViewById(R.id.wordSearchImg);
        int imageResource = getResources().getIdentifier("@drawable/" + question.getImgPath(), null, this.getPackageName());
        imageView.setImageResource(imageResource);

        ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.wordSearch);
        ConstraintSet constraint = new ConstraintSet();
        constraint.clone(constraintLayout);

        //button1
        constraint.constrainPercentHeight(R.id.wordSearchWord1,Integer.parseInt(posSize[0]));
        constraint.constrainPercentWidth(R.id.wordSearchWord1,Integer.parseInt(posSize[1]));
        constraint.setVerticalBias(R.id.wordSearchWord1,Integer.parseInt(posSize[3]));
        constraint.setHorizontalBias(R.id.wordSearchWord1,Integer.parseInt(posSize[2]));

        //button 2
        constraint.constrainPercentHeight(R.id.wordSearchWord2,Integer.parseInt(posSize[4]));
        constraint.constrainPercentWidth(R.id.wordSearchWord2,Integer.parseInt(posSize[5]));
        constraint.setVerticalBias(R.id.wordSearchWord2,Integer.parseInt(posSize[7]));
        constraint.setHorizontalBias(R.id.wordSearchWord2,Integer.parseInt(posSize[6]));

        //button 3
        constraint.constrainPercentHeight(R.id.wordSearchWord3,Integer.parseInt(posSize[8]));
        constraint.constrainPercentWidth(R.id.wordSearchWord3,Integer.parseInt(posSize[9]));
        constraint.setVerticalBias(R.id.wordSearchWord3,Integer.parseInt(posSize[11]));
        constraint.setHorizontalBias(R.id.wordSearchWord3,Integer.parseInt(posSize[10]));

        constraint.applyTo(constraintLayout);
        // figure out how to show when selected

        // detects tap on screen, records timestamp
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeStamps.updateTimeStamp();
            }
        });

        // testing for item found
        word1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStamps.updateTimeStamp();
                word1.setBackgroundColor(getResources().getColor(R.color.ummaize));
                word1.setSelected(true);
            }
        });
        word2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStamps.updateTimeStamp();
                word2.setBackgroundColor(getResources().getColor(R.color.ummaize));
                word2.setSelected(true);
            }
        });
        word3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStamps.updateTimeStamp();
                word3.setBackgroundColor(getResources().getColor(R.color.ummaize));
                word3.setSelected(true);
            }
        });

        // "next" button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStamps.updateTimeStamp();
                if(word1.isSelected()){
                    answer += question.getAnswerOptions()[0];
                }
                if(word2.isSelected()){
                    answer += question.getAnswerOptions()[1];
                }
                if(word3.isSelected()){
                    answer += question.getAnswerOptions()[2];
                }
                csvWriter.WriteAnswers(outputName, WordSearchActivity.this, timeStamps, question.getTypeActivity(), answer, question.getCorrectAnswer());
                ActivitySwitch();
            }
        });
    }
    public void ActivitySwitch() {
        Question nextQuestion = questionBank.pop();
        if(nextQuestion==null){
            Intent intent = new Intent(this, FinalPageActivity.class);
            Log.d("Activity", "Activity: FINAL" );
            startActivity(intent);
        }else{
            try {
                String nextClassName = "com.example.surveyapp." + nextQuestion.getTypeActivity();
                Intent intent = new Intent(this, Class.forName(nextClassName));
                intent.putExtra("questionBank", questionBank);
                Log.d("Activity", "Activity: " + nextQuestion.getTypeActivity() );
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
