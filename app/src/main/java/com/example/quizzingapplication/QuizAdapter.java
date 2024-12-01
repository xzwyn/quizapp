package com.example.quizzingapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


// Class  to bind data to a RecyclerView that displays a quiz list
public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList;
    private Context context;
    // Listener for handling quiz click and delete
    private OnQuizClickListener listener;

    // Interface to handle click and delete for the quiz
    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
        void onDeleteQuizClick(Quiz quiz);
    }
    // Constructor to initialize the adapter
    public QuizAdapter(Context context, List<Quiz> quizList, OnQuizClickListener listener) {
        this.context = context;
        this.quizList = quizList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false);
        return new QuizViewHolder(view);
    }

    // Method to bind data to the views for each quiz item
    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.quizName.setText(quiz.getName());
        holder.questionCount.setText(String.format("%s questions", String.valueOf(quiz.getQuestions().size())));
        holder.startQuizButton.setOnClickListener(v -> listener.onQuizClick(quiz));

        // Handle delete button click
        holder.deleteQuizButton.setOnClickListener(v -> listener.onDeleteQuizClick(quiz));
    }

    // Return the total number of quizzes
    @Override
    public int getItemCount() {
        return quizList.size();
    }

    // Method to delete a quiz and update the RecyclerView
    public void deleteQuiz(Quiz quiz) {
        int position = quizList.indexOf(quiz);
        if (position != -1) {
            quizList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, quizList.size());
        }
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizName;
        TextView questionCount;
        Button startQuizButton;
        ImageButton deleteQuizButton;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizName = itemView.findViewById(R.id.quiz_name);
            startQuizButton = itemView.findViewById(R.id.start_quiz_button);
            deleteQuizButton = itemView.findViewById(R.id.delete_btn);
            questionCount = itemView.findViewById(R.id.question_count);
        }
    }
}


