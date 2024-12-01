package com.example.quizzingapplication;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


// Quiz class, implements Parcelable to pass quizzes via Intents

public class Quiz implements Parcelable {
    private String name;
    private List<Question> questions;
    private int timerDuration; // in seconds

    // Constructor
    public Quiz(String name, List<Question> questions, int timerDuration) {
        this.name = name;
        this.questions = questions;
        this.timerDuration = timerDuration;
    }

    // Parcelable Constructor
    protected Quiz(Parcel in) {
        name = in.readString();
        questions = in.createTypedArrayList(Question.CREATOR);
        timerDuration = in.readInt();
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getTimerDuration() {
        return timerDuration;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    // Method to write a quiz to the Parcel
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeTypedList(questions);
        parcel.writeInt(timerDuration);
    }
}