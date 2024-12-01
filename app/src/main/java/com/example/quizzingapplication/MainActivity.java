package com.example.quizzingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Button quizzesBtn;
    private Button aboutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadToInternalStorage();

        quizzesBtn = findViewById(R.id.quizzes_btn);
        aboutBtn = findViewById(R.id.about_btn);

        // Navigate to the quiz list
        quizzesBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, QuizListActivity.class);
            startActivity(intent);
        });


        // Navigate to the about page
        aboutBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    // Method to load the default quizzes.json file from assets to the internal storage on initial load
    private void loadToInternalStorage() {
        File file = new File(getFilesDir(), "quizzes.json");
        if (!file.exists()) {
            try (InputStream is = getAssets().open("quizzes.json");
                 FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                fos.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
