package com.example.surveyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";
    public static final String EXTRA_OUTPUT = "OUTPUT_NAME"; //reading into next activity

    String outputName;
    Button mapNext;
    CSVWriting csvWriter = new CSVWriting();

    QuestionBank questionBank;
    Question question;

    // THIS IS MENU STUFF
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.end_survey:
                endSurvey();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void endSurvey(){
        new AlertDialog.Builder(MapActivity.this)
                .setTitle("End Survey")
                .setMessage("Are you sure you want to end this survey? All results will be saved and the app will restart")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        navigateUpTo(new Intent(MapActivity.this, FirstPageActivity.class));
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    // THIS IS MENU STUFF

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        questionBank = (QuestionBank) getIntent().getSerializableExtra("questionBank");
        question = questionBank.getPrevQuestion();

        // Grabs output name from FirstPageActivity for CSVWriting
        Intent intent = getIntent();
        outputName = intent.getStringExtra(FirstPageActivity.EXTRA_OUTPUT);

        // sets up image
        ImageView imageView = (ImageView) findViewById(R.id.mapImage);
        int imageResource = getResources().getIdentifier("@drawable/"+question.getImgPath(), null, this.getPackageName());
        imageView.setImageResource(imageResource);

        TextView title = findViewById(R.id.textTitleMap);
        title.setText("Set " + String.valueOf(questionBank.getSetChoiceString()+1) + ": "+ question.getType());
        // sets up prompt
        TextView prompt = findViewById(R.id.mapPrompt);
        prompt.setText(question.getQuestion());

        // matches buttons with xml id
        mapNext = findViewById(R.id.mapNext);

        InitChoiceButtons buttons = new InitChoiceButtons(this,"mapChoice",question.getAnswerOptions());

        // detects tap on screen, records timestamp
        ConstraintLayout cLayout = findViewById(R.id.map);
        cLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttons.getTimeStamps().updateTimeStamp();
            }
        });

        // "next" button
        mapNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttons.getTimeStamps().updateTimeStamp();
                Log.d("MapActivity", "selected: " + buttons.getSelected() );
                csvWriter.WriteAnswers(outputName, MapActivity.this, buttons.getTimeStamps(), question.getAnswerType(), buttons.getSelected(), question.getCorrectAnswer());
                ActivitySwitch();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Disable back button
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
                intent.putExtra(EXTRA_OUTPUT, outputName); // this sends the io name to the next activity
                intent.putExtra("questionBank", questionBank);
                Log.d("Activity", "Activity: " + nextQuestion.getTypeActivity() );
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
