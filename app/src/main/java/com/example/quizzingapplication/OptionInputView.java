package com.example.quizzingapplication;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;


// Helper class to manage the input fields of an option
public class OptionInputView {
    private EditText editOptionText;
    private CheckBox checkboxCorrectAnswer;

    public OptionInputView(View view) {
        editOptionText = view.findViewById(R.id.edit_option_text);
        checkboxCorrectAnswer = view.findViewById(R.id.checkbox_correct_answer);
    }

    public String getOptionText() {
        return editOptionText.getText().toString().trim();
    }

    public boolean isCorrect() {
        return checkboxCorrectAnswer.isChecked();
    }
}