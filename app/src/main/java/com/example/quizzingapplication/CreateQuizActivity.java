package com.example.quizzingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    private EditText editQuizName;
    private EditText editTimerDuration;
    private Button buttonAddQuestion;
    private Button buttonSubmitQuiz;
    private Button buttonCancelQuiz;
    private LinearLayout questionsContainer;

    private List<QuestionInputView> questionInputViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_quiz_activity);

        editQuizName = findViewById(R.id.edit_quiz_name);
        editTimerDuration = findViewById(R.id.edit_timer_duration);
        buttonAddQuestion = findViewById(R.id.button_add_question);
        buttonSubmitQuiz = findViewById(R.id.button_submit_quiz);
        buttonCancelQuiz = findViewById(R.id.button_cancel_quiz);
        questionsContainer = findViewById(R.id.questions_container);

        buttonAddQuestion.setOnClickListener(view -> addQuestionView());
        buttonSubmitQuiz.setOnClickListener(view -> submitQuiz());
        buttonCancelQuiz.setOnClickListener(view -> cancelQuiz());


        addQuestionView();
    }

    private void addQuestionView() {
        View questionView = LayoutInflater.from(this).inflate(R.layout.create_question_item, questionsContainer, false);
        questionsContainer.addView(questionView);

        QuestionInputView questionInputView = new QuestionInputView(questionView);
        questionInputViews.add(questionInputView);
    }

    // Method to navigate back to the quiz list
    private void cancelQuiz(){
        Intent intent = new Intent(CreateQuizActivity.this, QuizListActivity.class);
        startActivity(intent);
    }

    // Method to create a quiz
    private void submitQuiz() {

        // Get data from the input fields
        String quizName = editQuizName.getText().toString().trim();
        String timerDurationStr = editTimerDuration.getText().toString().trim();

        // Check the name input
        if (TextUtils.isEmpty(quizName)) {
            editQuizName.setError("Quiz name is required");
            return;
        }

        // Check the timer input
        if (TextUtils.isEmpty(timerDurationStr)) {
            editTimerDuration.setError("Timer duration is required");
            return;
        }

        int timerDuration;

        // Checking whether the number is valid
        try {
            timerDuration = Integer.parseInt(timerDurationStr);
            if (timerDuration <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            editTimerDuration.setError("Enter a valid number");
            return;
        }

        List<Question> questions = new ArrayList<>();

        // Looping over questions
        for (int i = 0; i < questionInputViews.size(); i++) {
            QuestionInputView questionInputView = questionInputViews.get(i);
            String questionText = questionInputView.getQuestionText();
            String questionType = questionInputView.getQuestionType();
            List<String> options = questionInputView.getOptions();
            List<Integer> correctAnswers = questionInputView.getCorrectAnswers();


            // Check the question text input
            if (TextUtils.isEmpty(questionText)) {
                Toast.makeText(this, "Question " + (i + 1) + " must have text", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check the options input
            if (options.size() < 2) {
                Toast.makeText(this, "Question " + (i + 1) + " must have at least 2 options", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check the correct answer input
            if (correctAnswers.isEmpty()) {
                Toast.makeText(this, "Question " + (i + 1) + " must have at least one correct answer", Toast.LENGTH_SHORT).show();
                return;
            }

            // Creating a new Question object and appending it to the list
            questions.add(new Question(questionText, questionType, options, correctAnswers));
        }

        // Creating Quiz Object
        Quiz quiz = new Quiz(quizName, questions, timerDuration);

        // Save the quiz to quizzes.json file
        if (saveQuiz(quiz)) {
            Toast.makeText(this, "Quiz created!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error while creating quiz", Toast.LENGTH_LONG).show();
        }
    }

    // Method to save a quiz to the JSON file from the internal storage
    private boolean saveQuiz(Quiz newQuiz) {
        try {

            // Getting the quizzes file from the internal storage
            File file = new File(getFilesDir(), "quizzes.json");

            // Load existing quizzes
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            fis.close();

            String json = new String(buffer, "UTF-8");
            JSONArray quizArray = new JSONArray(json);

            // Converting quiz to JSON Object
            JSONObject newQuizObject = new JSONObject();
            newQuizObject.put("name", newQuiz.getName());
            newQuizObject.put("timer_duration", newQuiz.getTimerDuration());

            JSONArray questionsArray = new JSONArray();
            for (Question q : newQuiz.getQuestions()) {
                JSONObject questionObject = new JSONObject();
                questionObject.put("text", q.getText());
                questionObject.put("type", q.getType());

                JSONArray optionsJsonArray = new JSONArray(q.getOptions());
                questionObject.put("options", optionsJsonArray);

                JSONArray answersJsonArray = new JSONArray(q.getCorrectAnswers());
                questionObject.put("answers", answersJsonArray);

                questionsArray.put(questionObject);
            }

            newQuizObject.put("questions", questionsArray);
            quizArray.put(newQuizObject);

            // Write new quiz to the JSON file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(quizArray.toString(4).getBytes("UTF-8"));
            fos.close();

            return true;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}