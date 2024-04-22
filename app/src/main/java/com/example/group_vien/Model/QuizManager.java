package com.example.group_vien.Model;

import android.service.credentials.BeginCreateCredentialRequest;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class QuizManager {
    List<Quiz> quizList;
    public QuizManager(){ quizList = new ArrayList<>();}
    public void addQuiz(Quiz quiz){
        quizList.add(quiz);
    }

    public int calculatePoint(List<Boolean> answers){
        int count = 0;
        for(int i = 0; i < quizList.size(); i++){
            if(answers.get(i) == quizList.get(i).getAnswer()) count++;
        }
        Log.d("answer", answers.toString());
        return count;
    }

    public void getAnswer(){
        List<Boolean> list = new ArrayList<>();
        for(int i = 0; i < quizList.size(); i++){
            list.add(quizList.get(i).getAnswer());
        }
        Log.d("Answer",list.toString());
    }
    public String getQuestion(int i){
        return quizList.get(i).getQuestion();
    }
}
