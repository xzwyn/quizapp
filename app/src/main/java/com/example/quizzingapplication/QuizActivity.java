package com.example.quizzingapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// Class for Quiz Activity

public class QuizActivity extends AppCompatActivity {

    private TextView questionView;
    private TextView scoreView;
    private TextView titleView;
    private TextView questionTypeView;
    private TextView repeatQuestionsView;
    private TextView timerView;

    private Button firstOption;
    private Button secondOption;
    private Button thirdOption;
    private Button fourthOption;

    private CheckBox firstCheckbox;
    private CheckBox secondCheckbox;
    private CheckBox thirdCheckbox;
    private CheckBox fourthCheckbox;

    private Button submitBtn;
    private Button finishBtn;

    private int currentQuestionIndex = 0;
    private int score = 0;
    private int repeatCount = 0;
    private int selectedOptionIndex = -1;
    private Question currentQuestion;

    private CountDownTimer timer;
    private long timeLeft = 20000;

    private String quizName;
    private int timerDuration;
    private List<Question> questionList;
    private List<Question> incorrectQuestions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        questionView = findViewById(R.id.text_question);
        scoreView = findViewById(R.id.score_view);
        questionTypeView = findViewById(R.id.question_type_view);
        repeatQuestionsView = findViewById(R.id.repeat_questions_view);
        timerView = findViewById(R.id.timer_view);
        titleView = findViewById(R.id.title_view);

        firstOption = findViewById(R.id.button_option1);
        secondOption = findViewById(R.id.button_option2);
        thirdOption = findViewById(R.id.button_option3);
        fourthOption = findViewById(R.id.button_option4);

        firstCheckbox = findViewById(R.id.checkbox_option1);
        secondCheckbox = findViewById(R.id.checkbox_option2);
        thirdCheckbox = findViewById(R.id.checkbox_option3);
        fourthCheckbox = findViewById(R.id.checkbox_option4);

        submitBtn = findViewById(R.id.submit_btn);
        finishBtn = findViewById(R.id.finish_btn);

        repeatQuestionsView.setVisibility(View.GONE);

        score = 0;
        currentQuestionIndex = 0;
        incorrectQuestions.clear();

        // Getting quiz data from Intent

        quizName = getIntent().getStringExtra("quiz_name");
        timerDuration = getIntent().getIntExtra("quiz_timer_duration", 30);
        questionList = getIntent().getParcelableArrayListExtra("quiz_questions");


        // Setting quiz data to the UI
        if (questionList != null && !questionList.isEmpty()) {
            setTitle(quizName);

            currentQuestion = questionList.get(currentQuestionIndex);
            displayQuestion(currentQuestion);
        } else {
            Toast.makeText(this, "No questions.", Toast.LENGTH_LONG).show();
            finish();
        }


        // Submit answer
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(currentQuestion);
            }
        });

        // Finish quiz
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishQuiz();
            }
        });

        // Initialize score and quiz title
        scoreView.setText(String.valueOf(score));
        titleView.setText(quizName);
    }
    // Method to display a question
    private void displayQuestion(Question question) {

        resetVisibility();
        resetButtonColors();
        resetCheckboxColors();

        questionView.setText(question.getText());
        List<String> options = question.getOptions();
        List<Integer> answers = question.getCorrectAnswers();
        String questionType = question.getType();

        if (Objects.equals(questionType, "multiple choice")) {
            // Multiple-answer question: checkboxes
            setCheckboxes(options);
            questionTypeView.setText(String.format("Multiple - %d correct answers", answers.size()));
            submitBtn.setVisibility(View.VISIBLE);
        } else if (Objects.equals(questionType, "single")) {
            // Single-answer question: buttons
            setButtons(options);
            questionTypeView.setText("Single");

        }

        startTimer(timerDuration);
    }

    // Method to load next question
    private void loadNextQuestion() {
        currentQuestionIndex++;
        // Specify the repeats count
        int MAX_REPEATS = 1;
        // Starting with the main questions list
        if (currentQuestionIndex < questionList.size()) {
            currentQuestion = questionList.get(currentQuestionIndex);
            displayQuestion(currentQuestion);
            timeLeft = timerDuration * 1000L;

        } else if (!incorrectQuestions.isEmpty() && repeatCount < MAX_REPEATS) {
            // Repeating questions
            questionList = new ArrayList<>(incorrectQuestions);
            incorrectQuestions.clear();
            currentQuestionIndex = 0;
            currentQuestion = questionList.get(currentQuestionIndex);
            repeatCount++;
            repeatQuestionsView.setVisibility(View.VISIBLE);
            displayQuestion(currentQuestion);
            timeLeft = timerDuration * 1000L;
        } else {
            // Finish quiz if no questions left
            Toast.makeText(this, "Quiz Finished!", Toast.LENGTH_SHORT).show();
            finishQuiz();
        }
    }

    private void finishQuiz(){

        // Deactivate timer
        if (timer != null) {
            timer.cancel();
        }

        // Pass the quiz data to ResultsActivity
        Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("quiz_name", quizName);
        intent.putExtra("quiz_timer_duration", timerDuration);
        intent.putParcelableArrayListExtra("quiz_questions", new ArrayList<>(questionList)); // Parcelable
        startActivity(intent);
        finish();
    }

    // Reset visibility of UI elements after each question
    private void resetVisibility() {
        firstOption.setVisibility(View.GONE);
        secondOption.setVisibility(View.GONE);
        thirdOption.setVisibility(View.GONE);
        fourthOption.setVisibility(View.GONE);

        firstCheckbox.setVisibility(View.GONE);
        secondCheckbox.setVisibility(View.GONE);
        thirdCheckbox.setVisibility(View.GONE);
        fourthCheckbox.setVisibility(View.GONE);

        submitBtn.setVisibility(View.GONE);
    }


    // Set buttons for single answer questions
    private void setButtons(List<String> options) {
        if (options.size() > 0) {
            firstOption.setText(options.get(0));
            firstOption.setVisibility(View.VISIBLE);
            firstOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedOptionIndex = 0;
                    checkAnswer(currentQuestion);
                }
            });
        }

        if (options.size() > 1) {
            secondOption.setText(options.get(1));
            secondOption.setVisibility(View.VISIBLE);
            secondOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedOptionIndex = 1;
                    checkAnswer(currentQuestion);
                }
            });
        }

        if (options.size() > 2) {
            thirdOption.setText(options.get(2));
            thirdOption.setVisibility(View.VISIBLE);
            thirdOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedOptionIndex = 2;
                    checkAnswer(currentQuestion);
                }
            });
        }

        if (options.size() > 3) {
            fourthOption.setText(options.get(3));
            fourthOption.setVisibility(View.VISIBLE);
            fourthOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedOptionIndex = 3;
                    checkAnswer(currentQuestion);
                }
            });
        }
    }
    // Set checkboxes for single answer questions
    private void setCheckboxes(List<String> options) {
        if (options.size() > 0) {
            firstCheckbox.setText(options.get(0));
            firstCheckbox.setVisibility(View.VISIBLE);
        }
        if (options.size() > 1) {
            secondCheckbox.setText(options.get(1));
            secondCheckbox.setVisibility(View.VISIBLE);
        }
        if (options.size() > 2) {
            thirdCheckbox.setText(options.get(2));
            thirdCheckbox.setVisibility(View.VISIBLE);
        }
        if (options.size() > 3) {
            fourthCheckbox.setText(options.get(3));
            fourthCheckbox.setVisibility(View.VISIBLE);
        }
    }

    private void resetCheckboxes() {
        firstCheckbox.setChecked(false);
        secondCheckbox.setChecked(false);
        thirdCheckbox.setChecked(false);
        fourthCheckbox.setChecked(false);
    }


    // Method to highlight a button depending on answer
    private void highlightButton(int index, int color) {
        ColorStateList highlightColor = ColorStateList.valueOf(color);

        switch (index) {
            case 0:
                firstOption.setBackgroundTintList(highlightColor);
                break;
            case 1:
                secondOption.setBackgroundTintList(highlightColor);
                break;
            case 2:
                thirdOption.setBackgroundTintList(highlightColor);
                break;
            case 3:
                fourthOption.setBackgroundTintList(highlightColor);
                break;
            default:
                break;
        }
    }
    // Method to highlight a checkbox border depending on answer
    private void highlightCheckbox(int index, int color) {

        GradientDrawable dottedBorder = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.dotted_border);

        if (dottedBorder != null) {
            dottedBorder.setStroke(2, ContextCompat.getColor(this, color), 4, 2);
        }

        switch (index) {
            case 0:
                firstCheckbox.setBackground(dottedBorder);
                break;
            case 1:
                secondCheckbox.setBackground(dottedBorder);
                break;
            case 2:
                thirdCheckbox.setBackground(dottedBorder);
                break;
            case 3:
                fourthCheckbox.setBackground(dottedBorder);
                break;
            default:
                break;
        }
    }

    // Disable buttons after submitting answer
    private void disableButtons() {
        firstOption.setEnabled(false);
        secondOption.setEnabled(false);
        thirdOption.setEnabled(false);
        fourthOption.setEnabled(false);

        firstOption.setTextColor(Color.WHITE);
        secondOption.setTextColor(Color.WHITE);
        thirdOption.setTextColor(Color.WHITE);
        fourthOption.setTextColor(Color.WHITE);
    }
    // Disable checkboxes after submitting answer
    private void disableCheckboxes() {
        firstCheckbox.setEnabled(false);
        secondCheckbox.setEnabled(false);
        thirdCheckbox.setEnabled(false);
        fourthCheckbox.setEnabled(false);
    }

    // Resetting button colors for each new question
    private void resetButtonColors() {
        ColorStateList defaultColor = ContextCompat.getColorStateList(this, R.color.default_purple);

        firstOption.setBackgroundTintList(defaultColor);
        secondOption.setBackgroundTintList(defaultColor);
        thirdOption.setBackgroundTintList(defaultColor);
        fourthOption.setBackgroundTintList(defaultColor);

        firstOption.setEnabled(true);
        secondOption.setEnabled(true);
        thirdOption.setEnabled(true);
        fourthOption.setEnabled(true);

        firstOption.setTextColor(Color.WHITE);
        secondOption.setTextColor(Color.WHITE);
        thirdOption.setTextColor(Color.WHITE);
        fourthOption.setTextColor(Color.WHITE);
    }
    // Resetting checkbox colors for each new question
    private void resetCheckboxColors() {

        GradientDrawable dottedBorder = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.dotted_border);

        if (dottedBorder != null) {
            dottedBorder.setStroke(2, ContextCompat.getColor(this, R.color.default_purple), 4, 2);
        }

        firstCheckbox.setBackground(dottedBorder);
        secondCheckbox.setBackground(dottedBorder);
        thirdCheckbox.setBackground(dottedBorder);
        fourthCheckbox.setBackground(dottedBorder);

        firstCheckbox.setTextColor(ContextCompat.getColor(this, R.color.black));
        secondCheckbox.setTextColor(ContextCompat.getColor(this, R.color.black));
        thirdCheckbox.setTextColor(ContextCompat.getColor(this, R.color.black));
        fourthCheckbox.setTextColor(ContextCompat.getColor(this, R.color.black));

        firstCheckbox.setEnabled(true);
        secondCheckbox.setEnabled(true);
        thirdCheckbox.setEnabled(true);
        fourthCheckbox.setEnabled(true);

        resetCheckboxes();
    }

    // Start timer after displaying question
    private void startTimer(int duration) {

        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(duration * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                Toast.makeText(QuizActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
                // Load new question if the time is up
                loadNextQuestion();
            }
        }.start();
    }

    // Method to update timerView
    private void updateTimer() {
        int seconds = (int) (timeLeft / 1000) % 60;
        int minutes = (int) ((timeLeft / (1000 * 60)) % 60);

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerView.setText(timeFormatted);
    }

    // Method to check answer after submission
    private void checkAnswer(Question question) {
        List<Integer> selectedAnswers = new ArrayList<>();
        List<Integer> correctAnswers = question.getCorrectAnswers();

        if (Objects.equals(question.getType(), "multiple choice")) {

            // Get selected answers
            if (firstCheckbox.isChecked()) selectedAnswers.add(0);
            if (secondCheckbox.isChecked()) selectedAnswers.add(1);
            if (thirdCheckbox.isChecked()) selectedAnswers.add(2);
            if (fourthCheckbox.isChecked()) selectedAnswers.add(3);

            // Correct answer - highlight checkbox with green
            for (int index : correctAnswers) {
                highlightCheckbox(index, R.color.green);
            }

            // Wrong answer - highlight checkbox with red
            for (int index : selectedAnswers) {
                if (!correctAnswers.contains(index)) {
                    highlightCheckbox(index, R.color.red);
                }
            }

            Collections.sort(selectedAnswers);
            Collections.sort(correctAnswers);

            if (selectedAnswers.equals(correctAnswers)) {
                // Update score
                score += 1;
                scoreView.setText(String.valueOf(score));
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
                incorrectQuestions.add(question);
            }
            // Disable after submission
            disableCheckboxes();

        } else if (Objects.equals(question.getType(), "single")) {

            // Check whether an option was selected
            if (selectedOptionIndex == -1) {
                Toast.makeText(this, "Please select an option.", Toast.LENGTH_SHORT).show();
                return;
            }

            int correctAnswerIndex = correctAnswers.get(0);

            // Correct answer - highlight button with green
            highlightButton(correctAnswerIndex, Color.GREEN);

            if (selectedOptionIndex == correctAnswerIndex) {
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                score += 1;
                scoreView.setText(String.valueOf(score));
            } else {
                // Correct answer - highlight button with red
                highlightButton(selectedOptionIndex, Color.RED);
                Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
                incorrectQuestions.add(question);
            }

            // Disable after submission, reset selectedOptionIndex
            disableButtons();
            selectedOptionIndex = -1;
        }

        // Stop timer
        if (timer != null) {
            timer.cancel();
        }


        // Delay for feedback after displaying new question
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNextQuestion();
            }
        }, 2000);

    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }
}
