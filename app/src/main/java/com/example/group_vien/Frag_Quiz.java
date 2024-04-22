package com.example.group_vien;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.group_vien.Model.QuizManager;
import com.example.group_vien.Utils.ApiService;
import com.example.group_vien.Utils.JsonQueryAdapter;
import com.tencent.mmkv.MMKV;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Frag_Quiz extends Fragment {
    final String token = " "; // Add the provided API Key in here
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    TextView tv_questionCount, tv_question;
    RadioGroup radioGroup;
    Button btn_next, btn_previous;
    RadioButton btn_true, btn_false;
    QuizManager quizManager;
    Retrofit retrofit;
    ApiService apiService;
    RequestBody jsonQuery;
    MMKV kv;
    String area;
    JsonQueryAdapter jsonQueryAdapter;
    String questionQuery;
    int currentQuestion = 0;
    List<Boolean> answers;
    public Frag_Quiz() {}
    public static Frag_Quiz newInstance() {
        Frag_Quiz fragment = new Frag_Quiz();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_quiz, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_questionCount = view.findViewById(R.id.tv_question_count);
        tv_question = view.findViewById(R.id.tv_question);
        radioGroup = view.findViewById(R.id.radio_group);
        btn_true = view.findViewById(R.id.btn_true);
        btn_false = view.findViewById(R.id.btn_false);
        btn_next = view.findViewById(R.id.btn_next);
        btn_previous = view.findViewById(R.id.btn_previous);
        kv = MMKV.defaultMMKV();
        quizManager = new QuizManager();
        jsonQueryAdapter = new JsonQueryAdapter();
        answers = new ArrayList<>(Collections.nCopies(10,null));
        area = kv.decodeString("area");
        ExecutorService questionExecutor = Executors.newSingleThreadExecutor();
        questionExecutor.execute(() -> {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openai.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
            questionQuery = jsonQueryAdapter.getQuestionQuery(area);
            jsonQuery = RequestBody.create(questionQuery, JSON);
            Call<ResponseBody> questionCall = apiService.getQuestionData("Bearer "+token, jsonQuery);
            try{
                Response<ResponseBody> questionResponse = questionCall.execute();
                String questionMessage = questionResponse.body().string();
                quizManager = jsonQueryAdapter.getQuestion(questionMessage);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_question.setText(quizManager.getQuestion(currentQuestion));
                        currentQuestion++;
                        btn_true.setVisibility(View.VISIBLE);
                        btn_false.setVisibility(View.VISIBLE);
                        btn_next.setVisibility(View.VISIBLE);
                        tv_questionCount.setText(currentQuestion +"/10");
                        quizManager.getAnswer();
                    }
                });
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.btn_true){
                    answers.set(currentQuestion-1, true);
                }
                if(checkedId == R.id.btn_false){
                    answers.set(currentQuestion-1, false);
                }
            }
        });
        radioGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton radioButton = v.findViewById(radioGroup.getCheckedRadioButtonId());
                if(radioButton.isChecked()){
                    radioGroup.setActivated(false);
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentQuestion > 0 && currentQuestion < 10){
                    if(currentQuestion==9){
                        btn_next.setText("Finish");
                    }
                    tv_question.setText(quizManager.getQuestion(currentQuestion));
                    currentQuestion++;
                    tv_questionCount.setText(currentQuestion + "/10");
                    radioGroup.clearCheck();
                    if(currentQuestion >= 2) {
                        btn_previous.setVisibility(View.VISIBLE);
                    }
                } else if (currentQuestion==10) {
                    int scores = quizManager.calculatePoint(answers);
                    tv_question.setText("Score: "+ scores + "/10");
                    btn_previous.setVisibility(View.INVISIBLE);
                    btn_true.setVisibility(View.INVISIBLE);
                    btn_false.setVisibility(View.INVISIBLE);
                    btn_next.setText("Try again");
                    currentQuestion = 0;
                } else if (currentQuestion == 0){
                    answers = new ArrayList<>(Collections.nCopies(10,null));
                    tv_question.setText(quizManager.getQuestion(currentQuestion));
                    currentQuestion++;
                    tv_questionCount.setText(currentQuestion + "/10");
                    btn_next.setText("Next question");
                    btn_previous.setVisibility(View.INVISIBLE);
                    btn_true.setVisibility(View.VISIBLE);
                    btn_false.setVisibility(View.VISIBLE);
                    radioGroup.clearCheck();
                }
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestion--;
                if(currentQuestion < 2){
                    btn_previous.setVisibility(View.INVISIBLE);
                }
                if (currentQuestion < 10) {
                    btn_next.setText("Next question");
                }
                tv_question.setText(quizManager.getQuestion(currentQuestion-1));
                tv_questionCount.setText(currentQuestion +"/10");
                radioGroup.clearCheck();
            }
        });
    }
}