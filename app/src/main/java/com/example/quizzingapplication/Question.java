package com.example.quizzingapplication;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;


// Question class, implements Parcelable to pass questions via Intents

public class Question implements Parcelable {
    private String text;
    private String type;
    private List<String> options;
    private List<Integer> correctAnswers;

    // Constructor class
    public Question(String text, String type, List<String> options, List<Integer> correctAnswers) {
        this.text = text;
        this.type = type;
        this.options = options;
        this.correctAnswers = correctAnswers;
    }

    // Getters
    public String getText() { return text; }
    public String getType() { return type; }
    public List<String> getOptions() { return options; }
    public List<Integer> getCorrectAnswers() { return correctAnswers; }

    // Parcelable constructor
    protected Question(Parcel in) {
        text = in.readString();
        type = in.readString();
        options = in.createStringArrayList();
        correctAnswers = in.readArrayList(Integer.class.getClassLoader());
    }

    // Method to write the data to the Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(type);
        dest.writeStringList(options);
        dest.writeList(correctAnswers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}