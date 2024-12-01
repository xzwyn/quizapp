package com.example.quizzingapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// Helper class to manage the input fields of a question

public class QuestionInputView {
    private EditText editQuestionText;
    private RadioGroup radioGroupQuestionType;
    private RadioButton radioSingleChoice;
    private RadioButton radioMultipleChoice;
    private LinearLayout optionsContainer;
    private Button buttonAddOption;

    private List<OptionInputView> optionInputViews = new ArrayList<>();

    public QuestionInputView(View view) {
        editQuestionText = view.findViewById(R.id.edit_question_text);
        radioGroupQuestionType = view.findViewById(R.id.radio_group_question_type);
        radioSingleChoice = view.findViewById(R.id.radio_single_choice);
        radioMultipleChoice = view.findViewById(R.id.radio_multiple_choice);
        optionsContainer = view.findViewById(R.id.options_container);
        buttonAddOption = view.findViewById(R.id.button_add_option);

        // Initializing with two options
        addOptionView();
        addOptionView();

        buttonAddOption.setOnClickListener(v -> addOptionView());
    }

    private void addOptionView() {
        if (optionInputViews.size() >= 10) { // Limit to 4 options
            Toast.makeText(editQuestionText.getContext(), "Maximum 4 options allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        View optionView = LayoutInflater.from(editQuestionText.getContext()).inflate(R.layout.create_option_item, optionsContainer, false);
        optionsContainer.addView(optionView);

        OptionInputView optionInputView = new OptionInputView(optionView);
        optionInputViews.add(optionInputView);
    }

    // Getters for all question fields
    public String getQuestionText() {
        return editQuestionText.getText().toString().trim();
    }

    public String getQuestionType() {
        int selectedId = radioGroupQuestionType.getCheckedRadioButtonId();
        if (selectedId == radioMultipleChoice.getId()) {
            return "multiple choice";
        } else {
            return "single";
        }
    }

    public List<String> getOptions() {
        List<String> options = new ArrayList<>();
        for (OptionInputView option : optionInputViews) {
            String text = option.getOptionText();
            if (!text.isEmpty()) {
                options.add(text);
            }
        }
        return options;
    }

    public List<Integer> getCorrectAnswers() {
        List<Integer> correctAnswers = new ArrayList<>();
        for (int i = 0; i < optionInputViews.size(); i++) {
            if (optionInputViews.get(i).isCorrect()) {
                correctAnswers.add(i);
            }
        }
        return correctAnswers;
    }
}
