package com.example.group_vien.Utils;

import android.service.credentials.BeginCreateCredentialRequest;
import android.util.Log;

import com.example.group_vien.Model.Quiz;
import com.example.group_vien.Model.QuizManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonQueryAdapter {
    public List<String> getAreas(String message) throws JSONException, IOException {
        JSONObject object = new JSONObject(message);
        JSONArray variables = object.getJSONArray("variables");
        JSONObject areaInfo = variables.getJSONObject(1);
        JSONArray valueText = areaInfo.getJSONArray("valueTexts");

        List<String> list = new ArrayList<String>();
        for (int i = 1; i < valueText.length(); i++) {
            list.add(valueText.getString(i));
        }

        return list;
    }

    public Map<String, String> getNumbericArea(String message) throws JSONException, IOException {
        JSONObject object = new JSONObject(message);
        JSONArray variables = object.getJSONArray("variables");
        JSONObject areaInfo = variables.getJSONObject(1);
        JSONArray values = areaInfo.getJSONArray("values");
        JSONArray valueTexts = areaInfo.getJSONArray("valueTexts");

        Map<String, String> map = new HashMap<String, String>();
        for (int i = 1; i < valueTexts.length(); i++) {
            map.put(valueTexts.getString(i), values.getString(i));
        }
        return map;
    }

    public String[] getGeoValues(String message) throws JSONException {
        String[] geoValues = new String[2];
        JSONArray array = new JSONArray(message);
        JSONObject object = array.getJSONObject(0);
        geoValues[0] = object.getString("lat");
        geoValues[1] = object.getString("lon");
        return geoValues;
    }

    public String[] getWeatherValues(String message) throws JSONException {
        String[] weatherValues = new String[3];
        JSONObject object = new JSONObject(message);
        JSONObject main = object.getJSONObject("main");
        JSONObject wind = object.getJSONObject("wind");
        weatherValues[0] = main.getString("temp");
        weatherValues[1] = main.getString("humidity");
        weatherValues[2] = wind.getString("speed");
        return weatherValues;
    }

    public String getPopulationQuery(String areaCode) throws JSONException {
        return String.format(
                "{\n" +
                        "  \"query\": [\n" +
                        "    {\n" +
                        "      \"code\": \"Alue\",\n" +
                        "      \"selection\": {\n" +
                        "        \"filter\": \"item\",\n" +
                        "        \"values\": [\n" +
                        "          \"%1$s\"\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"code\": \"Tiedot\",\n" +
                        "      \"selection\": {\n" +
                        "        \"filter\": \"item\",\n" +
                        "        \"values\": [\n" +
                        "          \"vaesto\"\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"response\": {\n" +
                        "    \"format\": \"json-stat2\"\n" +
                        "  }\n" +
                        "}", areaCode
        );
    }

    public String getWorkplaceQuery(String areaCode) throws JSONException {
        return String.format(
                "{\n" +
                "  \"query\": [\n" +
                "    {\n" +
                "      \"code\": \"Vuosi\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"item\",\n" +
                "        \"values\": [\n" +
                "          \"2022\"\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"Alue\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"item\",\n" +
                "        \"values\": [\n" +
                "          \"%1$s\"\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"response\": {\n" +
                "    \"format\": \"json-stat2\"\n" +
                "  }\n" +
                "}", areaCode
        );
    }

    public String getEmploymentRateQuery(String areaCode) throws JSONException {
        return String.format(
                "{\n" +
                        "  \"query\": [\n" +
                        "    {\n" +
                        "      \"code\": \"Alue\",\n" +
                        "      \"selection\": {\n" +
                        "        \"filter\": \"item\",\n" +
                        "        \"values\": [\n" +
                        "          \"%1$s\"\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"code\": \"Vuosi\",\n" +
                        "      \"selection\": {\n" +
                        "        \"filter\": \"item\",\n" +
                        "        \"values\": [\n" +
                        "          \"2022\"\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"code\": \"Tiedot\",\n" +
                        "      \"selection\": {\n" +
                        "        \"filter\": \"item\",\n" +
                        "        \"values\": [\n" +
                        "          \"tyollisyysaste\"\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"response\": {\n" +
                        "    \"format\": \"json-stat2\"\n" +
                        "  }\n" +
                        "}",areaCode
        );
    }

    public String getQuestionQuery(String area){
        return String.format(
                "{\n" +
                        "  \"model\": \"gpt-3.5-turbo\",\n" +
                        "  \"messages\": [\n" +
                        "    {\n" +
                        "      \"role\": \"user\",\n" +
                        "      \"content\": \"Generate 10 true/false questions about %1$s, Finland. Response as JSON.\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}", area
        );
    }

    public List<Integer> getPopulation(String message) throws JSONException {
        List<Integer> populations = new ArrayList<Integer>();
        JSONObject object = new JSONObject(message);
        JSONArray values = object.getJSONArray("value");
        for(int i = 0; i < values.length(); i++){
            populations.add(values.getInt(i));
        }
        return populations;
    }

    public String getWorkplaceSs(String message) throws JSONException {
        JSONObject object = new JSONObject(message);
        JSONArray value = object.getJSONArray("value");
        return value.getString(0);
    }

    public String getEmploymentRate(String message) throws JSONException {
        JSONObject object = new JSONObject(message);
        JSONArray value = object.getJSONArray("value");
        return value.getString(0);
    }

    public QuizManager getQuestion(String json) throws JSONException {
        QuizManager quizManager = new QuizManager();
        JSONObject object = new JSONObject(json);
        JSONArray choices = object.getJSONArray("choices");
        JSONObject zero = choices.getJSONObject(0);
        JSONObject message = zero.getJSONObject("message");
        JSONObject content = new JSONObject(message.getString("content"));
        JSONArray questions = content.getJSONArray("questions");
        Log.d("questions Length", Integer.toString(questions.length()));
        for(int i = 0; i< questions.length(); i++){
            JSONObject questionObject = questions.getJSONObject(i);
            String question = questionObject.getString("question");
            Boolean answer = Boolean.parseBoolean(questionObject.getString("answer"));
            quizManager.addQuiz(new Quiz(question, answer));
        }
        Log.d("questions", questions.toString());
        return quizManager;
    }
}


