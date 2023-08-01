package ca.on.conestogac.activities;

import static ca.on.conestogac.utility.Constants.TRACE_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.on.conestogac.R;
import ca.on.conestogac.model.RidePost;
import ca.on.conestogac.model.Survey;
import ca.on.conestogac.network.RetrofitAPI;
import ca.on.conestogac.utility.Constants;
import ca.on.conestogac.utility.SharedPreferencesUtility;
import ca.on.conestogac.utility.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TransportationSurveyActivity extends AppCompatActivity {

    private SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
    private String userEmail = sharedPreferencesUtility.getString(Constants.USER_EMAIL);

    private RadioGroup radioGroupAnswer1;
    private RadioGroup radioGroupAnswer2;
    private EditText editTextAnswer3;
    private RadioGroup radioGroupAnswer4;
    private RadioGroup radioGroupAnswer5;
    private EditText editTextAnswer6;
    private RadioGroup radioGroupAnswer7;
    private RadioGroup radioGroupAnswer8;
    private EditText editTextAnswer9;


    private final String TRUE = "TRUE";
    private final String FALSE = "FALSE";
    private int surveyVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation_survey);
        Utility.setActivityTitle(this, "Student Transportation Survey");
        initializeComponents();
    }

    private void initializeComponents(){

        surveyVersion = getIntent().getIntExtra(Constants.SURVEY_VERSION, 1);

        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        List<Survey> responsesList = new ArrayList<>();

        radioGroupAnswer1 = findViewById(R.id.answer_1);
        radioGroupAnswer2 = findViewById(R.id.answer_2);
        radioGroupAnswer4 = findViewById(R.id.answer_4);
        radioGroupAnswer5 = findViewById(R.id.answer_5);
        radioGroupAnswer7 = findViewById(R.id.answer_7);
        radioGroupAnswer8 = findViewById(R.id.answer_8);
        editTextAnswer3 = findViewById(R.id.answer_3);
        editTextAnswer6 = findViewById(R.id.answer_6);
        editTextAnswer9 = findViewById(R.id.answer_9);

        findViewById(R.id.survey_disclaimer).setSelected(true);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Question 1
                Survey survey1 = new Survey();
                survey1.setUser(userEmail);
                survey1.setQuestion(getString(R.string.question_1));
                survey1.setAnswer(((RadioButton)(radioGroupAnswer1.findViewById(radioGroupAnswer1.getCheckedRadioButtonId()))).getText().toString());
                survey1.setVersion(surveyVersion);

                //Question 2
                Survey survey2 = new Survey();
                survey2.setUser(userEmail);
                survey2.setQuestion(getString(R.string.question_2));
                survey2.setAnswer(((RadioButton)(radioGroupAnswer2.findViewById(radioGroupAnswer2.getCheckedRadioButtonId()))).getText().toString());
                survey2.setVersion(surveyVersion);

                //Question 3
                Survey survey3 = new Survey();
                survey3.setUser(userEmail);
                survey3.setQuestion(getString(R.string.question_3));
                survey3.setAnswer(editTextAnswer3.getText().toString().trim().isEmpty() ? "USER_NOT_ANSWERED" : editTextAnswer3.getText().toString().trim());
                survey3.setVersion(surveyVersion);

                //Question 4
                Survey survey4 = new Survey();
                survey4.setUser(userEmail);
                survey4.setQuestion(getString(R.string.question_4));
                survey4.setAnswer(((RadioButton)(radioGroupAnswer4.findViewById(radioGroupAnswer4.getCheckedRadioButtonId()))).getText().toString());
                survey4.setVersion(surveyVersion);

                //Question 5
                Survey survey5 = new Survey();
                survey5.setUser(userEmail);
                survey5.setQuestion(getString(R.string.question_5));
                survey5.setAnswer(((RadioButton)(radioGroupAnswer5.findViewById(radioGroupAnswer5.getCheckedRadioButtonId()))).getText().toString());
                survey5.setVersion(surveyVersion);

                //Question 6
                Survey survey6 = new Survey();
                survey6.setUser(userEmail);
                survey6.setQuestion(getString(R.string.question_6));
                survey6.setAnswer((editTextAnswer6.getText().toString().trim().isEmpty() ? "NOT_RESPONDED_BY_USER" : editTextAnswer6.getText().toString().trim()));
                survey6.setVersion(surveyVersion);

                //Question 7
                Survey survey7 = new Survey();
                survey7.setUser(userEmail);
                survey7.setQuestion(getString(R.string.question_7));
                survey7.setAnswer(((RadioButton)(radioGroupAnswer7.findViewById(radioGroupAnswer7.getCheckedRadioButtonId()))).getText().toString());
                survey7.setVersion(surveyVersion);

                //Question 8
                Survey survey8 = new Survey();
                survey8.setUser(userEmail);
                survey8.setQuestion(getString(R.string.question_8));
                survey8.setAnswer(((RadioButton)(radioGroupAnswer8.findViewById(radioGroupAnswer8.getCheckedRadioButtonId()))).getText().toString());
                survey8.setVersion(surveyVersion);

                //Question 9
                Survey survey9 = new Survey();
                survey9.setUser(userEmail);
                survey9.setQuestion(getString(R.string.question_9));
                survey9.setAnswer((editTextAnswer9.getText().toString().trim().isEmpty() ? "USER_NOT_ANSWERED" : editTextAnswer9.getText().toString().trim()));
                survey9.setVersion(surveyVersion);

                responsesList.add(survey1);
                responsesList.add(survey2);
                responsesList.add(survey3);
                responsesList.add(survey4);
                responsesList.add(survey5);
                responsesList.add(survey6);
                responsesList.add(survey7);
                responsesList.add(survey8);
                responsesList.add(survey9);

                postSurveyResponse(responsesList);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if(item.getItemId() == R.id.info){
            new AlertDialog.Builder(TransportationSurveyActivity.this)
                    .setTitle("Disclaimer")
                    .setMessage(getResources().getString(R.string.survey_disclaimer))
                    .setPositiveButton("OK", null).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.survey_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    void postSurveyResponse(List<Survey> responsesList){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<String> call = retrofitAPI.postSurveyResponse(responsesList);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String serverResponse = response.body().toString();
                Log.d(TRACE_TAG,serverResponse);
                if (serverResponse.equals(Constants.SURVEY_SAVED_SUCCESSFULLY)) {

                    Toast.makeText(getBaseContext(), Constants.SURVEY_SAVED_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TRACE_TAG,t.getMessage());
                Toast.makeText(getBaseContext(), "Ride not posted, please try again !!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}