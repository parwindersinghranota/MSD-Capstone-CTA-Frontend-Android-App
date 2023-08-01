package ca.on.conestogac.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ca.on.conestogac.R;
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

public class UserDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewFullName;
    private Button buttonManageRides;
    private Button buttonSearchRide;
    private Button buttonTransportationSurvey;
    private Button buttonDriverFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        Utility.setActivityTitle(this, "User Dashboard");

        initializeComponents();
    }

    private void initializeComponents() {

        //initialize components
        textViewFullName = findViewById(R.id.textViewFullname);
        buttonManageRides = findViewById(R.id.buttonManageRides);
        buttonSearchRide = findViewById(R.id.buttonSearchRide);
        buttonTransportationSurvey = findViewById(R.id.buttonTransportationSurvey);
        buttonDriverFeedback = findViewById(R.id.buttonDriverFeedback);

        //register events
        buttonManageRides.setOnClickListener(this);
        buttonSearchRide.setOnClickListener(this);
        buttonTransportationSurvey.setOnClickListener(this);
        buttonDriverFeedback.setOnClickListener(this);

        //show fullname
        SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
        String userFullName = sharedPreferencesUtility.getString(Constants.USER_FULL_NAME);
        textViewFullName.setText(userFullName);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        //Remove user email from shared prefrences
        SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
        sharedPreferencesUtility.removeKey(Constants.USER_EMAIL);
        sharedPreferencesUtility.removeKey(Constants.USER_FULL_NAME);

        finish();

        startActivity(new Intent(getBaseContext(), LoginActivity.class));
    }

    @Override
    public void onClick(View v) {
        if (v == buttonManageRides) {
            startActivity(new Intent(getBaseContext(), ManageRidesActivity.class));
        } else if (v == buttonSearchRide) {
            startActivity(new Intent(getBaseContext(), FindRidesActivity.class));
        } else if (v == buttonTransportationSurvey) {
            SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
            String userEmail = sharedPreferencesUtility.getString(Constants.USER_EMAIL);
            findSurvey(userEmail);

        } else if(v == buttonDriverFeedback){
            startActivity(new Intent(getBaseContext(), DriverFeedbackActivity.class));
        }
    }

    private void findSurvey(String userEmail) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<Survey> call = retrofitAPI.findSurvey(userEmail);

        call.enqueue(new Callback<Survey>() {

            @Override
            public void onResponse(Call<Survey> call, Response<Survey> response) {

                Survey survey = response.body();
                Log.d(Constants.TRACE_TAG, survey.toString());

                if(survey.getVersion() == 3){
                    new AlertDialog.Builder(UserDashboardActivity.this)
                            .setTitle("Maximum limit reached")
                            .setMessage("You have already submitted 3 survey responses.")
                            .setIcon(R.drawable.ic_warning_yellow)
                            .setPositiveButton("OK", null).show();
                }
                else if (survey.getVersion()!=-1) {

                    new AlertDialog.Builder(UserDashboardActivity.this)
                            .setTitle("Survey Response")
                            .setMessage("You have already submitted your responses. Do you want to submit new response?")
                            .setIcon(R.drawable.ic_warning_yellow)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    startActivity(new Intent(getBaseContext(), TransportationSurveyActivity.class).putExtra(Constants.SURVEY_VERSION, survey.getVersion()+1));
                                }
                            })
                            .setNegativeButton("NO", null)
                            .show();
                } else {
                    startActivity(new Intent(getBaseContext(), TransportationSurveyActivity.class));
                }
            }

            @Override
            public void onFailure(Call<Survey> call, Throwable t) {
                Log.d(Constants.TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "SERVER_PROBLEM",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePreviousSurveyResponse(String userEmail) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<String> call = retrofitAPI.deleteSurveyResponses(userEmail);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String serverResponse = response.body().toString();
                Log.d(Constants.TRACE_TAG, serverResponse);

                if (serverResponse.equals(Constants.SURVEY_DELETED)) {
                    Toast.makeText(getBaseContext(), Constants.SURVEY_DELETED,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), Constants.SURVEY_NOT_DELETED,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(Constants.TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), Constants.SURVEY_NOT_DELETED,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}