package com.example.quizzingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzingapplication.Question;
import com.example.quizzingapplication.Quiz;
import com.example.quizzingapplication.QuizActivity;
import com.example.quizzingapplication.QuizAdapter;
import com.example.quizzingapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


// Activity to display quiz list
public class QuizListActivity extends AppCompatActivity implements QuizAdapter.OnQuizClickListener {

    private RecyclerView quizzesRecyclerView;
    private QuizAdapter quizAdapter;
    private List<Quiz> quizList;

    private ImageButton createQuizButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_list_activity);

        quizzesRecyclerView = findViewById(R.id.quizzes_recycler_view);
        quizzesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        createQuizButton = findViewById(R.id.create_quiz_btn);

        // Create new quiz
        createQuizButton.setOnClickListener(view -> {
            Intent intent = new Intent(QuizListActivity.this, CreateQuizActivity.class);
            startActivity(intent);
        });

        loadAndDisplayQuizzes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayQuizzes();

    }
    // Loading quizzes and creating adapter object
    private void loadAndDisplayQuizzes() {
        quizList = loadQuizzes();
        quizAdapter = new QuizAdapter(this, quizList, this);
        quizzesRecyclerView.setAdapter(quizAdapter);
    }
    // Start quiz on click
    @Override
    public void onQuizClick(Quiz quiz) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("quiz_name", quiz.getName());
        intent.putExtra("quiz_timer_duration", quiz.getTimerDuration());
        intent.putParcelableArrayListExtra("quiz_questions", new ArrayList<>(quiz.getQuestions()));
        startActivity(intent);
    }
    // Delete quiz on click
    @Override
    public void onDeleteQuizClick(Quiz quiz) {
        // Confirm alert
        new AlertDialog.Builder(this)
                .setTitle("Delete Quiz")
                .setMessage("Are you sure you want to delete this quiz?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    quizAdapter.deleteQuiz(quiz); // Update RecyclerView
                    deleteQuiz(quiz);     // Delete quiz from JSON
                })
                .setNegativeButton("No", null)
                .show();
    }
    // Method to delete a quiz and update file
    private void deleteQuiz(Quiz quiz) {
        try {
            // Get the JSON file from internal storage
            File file = new File(getFilesDir(), "quizzes.json");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            fis.close();

            // Parse the JSON data
            String json = new String(buffer, "UTF-8");
            JSONArray quizArray = new JSONArray(json);

            // Remove the quiz by name
            for (int i = 0; i < quizArray.length(); i++) {
                JSONObject quizObject = quizArray.getJSONObject(i);
                if (quizObject.getString("name").equals(quiz.getName())) {
                    quizArray.remove(i);
                    break;
                }
            }

            // Write the updated JSON back to the file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(quizArray.toString().getBytes("UTF-8"));
            fos.close();

            Toast.makeText(this, "Quiz deleted", Toast.LENGTH_SHORT).show();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error deleting quiz", Toast.LENGTH_LONG).show();
        }
    }

    // Method to load quizzes from internal storage
    private List<Quiz> loadQuizzes() {
        List<Quiz> quizList = new ArrayList<>();

        try {
            File file = new File(getFilesDir(), "quizzes.json");
            FileInputStream fis = new FileInputStream(file);

            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            fis.close();

            String json = new String(buffer, "UTF-8");
            JSONArray quizArray = new JSONArray(json);

            for (int i = 0; i < quizArray.length(); i++) {
                JSONObject quizObject = quizArray.getJSONObject(i);

                String name = quizObject.getString("name");
                int timerDuration = quizObject.getInt("timer_duration");

                JSONArray questionsArray = quizObject.getJSONArray("questions");
                List<Question> questionList = new ArrayList<>();

                for (int j = 0; j < questionsArray.length(); j++) {
                    JSONObject questionObject = questionsArray.getJSONObject(j);

                    String text = questionObject.getString("text");
                    String type = questionObject.getString("type");

                    JSONArray optionsArray = questionObject.getJSONArray("options");
                    List<String> options = new ArrayList<>();
                    for (int k = 0; k < optionsArray.length(); k++) {
                        options.add(optionsArray.getString(k));
                    }

                    JSONArray answersArray = questionObject.getJSONArray("answers");
                    List<Integer> correctAnswers = new ArrayList<>();
                    for (int k = 0; k < answersArray.length(); k++) {
                        correctAnswers.add(answersArray.getInt(k));
                    }

                    Question question = new Question(text, type, options, correctAnswers);
                    questionList.add(question);
                }

                Quiz quiz = new Quiz(name, questionList, timerDuration);
                quizList.add(quiz);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading quizzes", Toast.LENGTH_LONG).show();
        }

        return quizList;
    }
}




