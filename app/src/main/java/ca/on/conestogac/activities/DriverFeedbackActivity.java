package ca.on.conestogac.activities;

import static ca.on.conestogac.utility.Constants.TRACE_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import ca.on.conestogac.R;
import ca.on.conestogac.adapter.DriverFeedbackAdapter;
import ca.on.conestogac.adapter.MessageListAdapter;
import ca.on.conestogac.model.Feedback;
import ca.on.conestogac.model.Message;
import ca.on.conestogac.model.RidePost;
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

public class DriverFeedbackActivity extends AppCompatActivity {

    private String userEmail;
    private String driverEmail;

    private RecyclerView recyclerViewDriverFeedbacks;
    private RecyclerView.LayoutManager layoutManager;

    private DriverFeedbackAdapter driverFeedbackAdapter;

    private FloatingActionButton floatingActionButtonPostFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_feedback);
        Utility.setActivityTitle(this, "Driver's Feedback Section");
        initializeComponents();

        fetchUserFeedbacksFromServer(driverEmail);
    }

    private void initializeComponents() {

        SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
        userEmail = sharedPreferencesUtility.getString(Constants.USER_EMAIL);
        driverEmail = getIntent().getStringExtra("DRIVER_EMAIL");

        Log.d(TRACE_TAG, driverEmail);

        recyclerViewDriverFeedbacks = findViewById(R.id.recyclerViewDriverFeedbacks);
        recyclerViewDriverFeedbacks.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewDriverFeedbacks.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        recyclerViewDriverFeedbacks.addItemDecoration(itemDecor);

        floatingActionButtonPostFeedback = findViewById(R.id.floatingActionButtonPostFeedback);
        floatingActionButtonPostFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfAleadyHadRideWithThisDriver(driverEmail, userEmail);
            }
        });
    }

    private void checkIfAleadyHadRideWithThisDriver(String driverEmail, String userEmail) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<String> call = retrofitAPI.findRideWithDriver(driverEmail, userEmail);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String serverResponse = response.body();
                if (serverResponse.equals(Constants.RIDE_EXIST_WITH_DRIVER)) {
                    getPreviousFeedback(driverEmail, userEmail);
                } else if (serverResponse.equals(Constants.RIDE_NOT_EXIST_WITH_DRIVER)) {
                    Toast.makeText(getBaseContext(), "You didn't ever ride with this driver, you cannot post feedback", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot fetch data. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPreviousFeedback(String driverEmail, String userEmail) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<Feedback> call = retrofitAPI.getFeedback(driverEmail, userEmail);
        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(Call<Feedback> call, Response<Feedback> response) {

                Feedback feedbackFromAPI = response.body();
                openPostFeedbackDialog(feedbackFromAPI);
            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {
                Log.d(TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot fetch data. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    ;

    //Save Feedback
    private void saveFeedback(Feedback feedback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<Feedback> call = retrofitAPI.postFeedback(feedback);
        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                Feedback serverResponse = response.body();
                Log.d(TRACE_TAG, serverResponse.toString());
                if (serverResponse.getId() != -1) {
                    fetchUserFeedbacksFromServer(driverEmail);
                }
            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {
                Log.d(TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot fetch data... Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Update Feedback
    private void updateFeedback(Feedback feedback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<Feedback> call = retrofitAPI.updateFeedback(feedback);
        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                Feedback serverResponse = response.body();
                if (serverResponse.getId() != 0) {
                    fetchUserFeedbacksFromServer(driverEmail);
                }
            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {
                Log.d(TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot fetch data. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean flag = false;

    //Method to open the custom dialog
    private void openPostFeedbackDialog(Feedback feedback) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_post_feedback_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Feedback");
        dialogBuilder.setCancelable(false);

        EditText editTextFeedbackText = dialogView.findViewById(R.id.editTextFeedbackText);
        RatingBar ratingBarRatings = dialogView.findViewById(R.id.ratingBarRatings);

        if (feedback != null && feedback.getId() != 0) {
            flag = true;
            editTextFeedbackText.setText(feedback.getMessage());
            ratingBarRatings.setRating((float) feedback.getRating());
        } else {
            flag = false;
        }

        //OK Button Click Listener
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!flag) {
                    Feedback feedback1 = new Feedback();
                    feedback1.setDriver(driverEmail);
                    feedback1.setUser(userEmail);
                    feedback1.setRating(ratingBarRatings.getRating());
                    feedback1.setMessage(editTextFeedbackText.getText().toString());
                    saveFeedback(feedback1);
                } else {
                    feedback.setRating(ratingBarRatings.getRating());
                    feedback.setMessage(editTextFeedbackText.getText().toString());
                    feedback.setDateTime(null);
                    updateFeedback(feedback);
                }
            }
        });

        //Cancel Button Click Listener
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ;
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void fetchUserFeedbacksFromServer(String email) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<List<Feedback>> call = retrofitAPI.getDriverFeedbackList(email);
        call.enqueue(new Callback<List<Feedback>>() {
            @Override
            public void onResponse(Call<List<Feedback>> call, Response<List<Feedback>> response) {

                List<Feedback> feedbackFromAPI = response.body();

                if (feedbackFromAPI != null && feedbackFromAPI.size() >= 0) {
                    driverFeedbackAdapter = new DriverFeedbackAdapter(feedbackFromAPI);
                    recyclerViewDriverFeedbacks.setAdapter(driverFeedbackAdapter);
                    recyclerViewDriverFeedbacks.scrollToPosition(driverFeedbackAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<Feedback>> call, Throwable t) {
                Log.d(TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot fetch data. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

}