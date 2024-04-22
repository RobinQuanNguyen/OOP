package com.example.group_vien.Model;

public class Quiz {
    String question;
    Boolean answer;


    public Quiz(String question, Boolean answer){
        this.question = question;
        this.answer = answer;
    }
    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public Boolean getAnswer() {
        return answer;
    }
    public String getQuestion() {
        return question;
    }

    public Boolean checkAnswer(Boolean answer){
        return this.answer==answer?true:false;
    }
}
