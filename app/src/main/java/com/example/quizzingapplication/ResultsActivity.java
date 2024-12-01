package com.example.quizzingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {

    private TextView scoreView;

    private Button menuButton;
    private Button restartButton;

    private int score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        score = getIntent().getIntExtra("score", 0);

        scoreView = findViewById(R.id.score_value_view);

        menuButton = findViewById(R.id.menu_btn);
        restartButton = findViewById(R.id.restart_quiz_btn);

        scoreView.setText(String.valueOf(score));

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMenu();
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartQuiz();
            }
        });
    }

    // Method to restart a quiz (passing quiz data to the Quiz Activity)
    private void restartQuiz () {
        Intent intent = new Intent(ResultsActivity.this, QuizActivity.class);

        intent.putExtra("quiz_name", getIntent().getStringExtra("quiz_name"));
        intent.putExtra("quiz_timer_duration", getIntent().getIntExtra("quiz_timer_duration", 30));
        intent.putParcelableArrayListExtra("quiz_questions", getIntent().getParcelableArrayListExtra("quiz_questions"));

        startActivity(intent);
        finish();
    }
    // Method to navigate to the main menu
    private void navigateToMenu() {
        Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
