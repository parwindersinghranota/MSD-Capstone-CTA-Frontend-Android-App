package ca.on.conestogac.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import ca.on.conestogac.R;
import ca.on.conestogac.model.LoginStatus;
import ca.on.conestogac.model.User;
import ca.on.conestogac.network.RetrofitAPI;
import ca.on.conestogac.utility.Constants;
import ca.on.conestogac.utility.SharedPreferencesUtility;
import ca.on.conestogac.utility.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView registerTextView;
    private TextView forgotPasswordTextView;
    private Button loginButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Utility.setActivityTitle(this, "User Login");
        initializeComponents();
    }

    private void initializeComponents() {
        //Refer the components
        registerTextView = findViewById(R.id.registerTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        loginButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        //Register Click Event Listener
        registerTextView.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    //Menu Items Click Events
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Click Events for Different Views
    @Override
    public void onClick(View v) {
        if (v == registerTextView) {
            startActivity(new Intent(getBaseContext(), RegistrationActivity.class));
        } else if (v == forgotPasswordTextView) {
            startActivity(new Intent(getBaseContext(), ForgotPasswordActivity.class));
        } else if (v == loginButton) {
            User user = validateData();
            if(user != null){
                loginRequest(user);
            }
        }
    }

    private void loginRequest(User user){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<LoginStatus> call = retrofitAPI.login(user);
        call.enqueue(new Callback<LoginStatus>() {
            @Override
            public void onResponse(Call<LoginStatus> call, Response<LoginStatus> response) {


                LoginStatus loginStatusFromAPI = response.body();
                Log.d(Constants.TRACE_TAG,loginStatusFromAPI.toString());

                if(loginStatusFromAPI.getStatus().equals(Constants.LOGIN_SUCCESSFUL)){

                    Toast.makeText(getBaseContext(), loginStatusFromAPI.getMessage(),
                            Toast.LENGTH_SHORT).show();

                    //Save useremail in Shared pref. for further use in App until he/she is logged in
                    SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
                    sharedPreferencesUtility.setString(Constants.USER_EMAIL, loginStatusFromAPI.getEmail());
                    sharedPreferencesUtility.setString(Constants.USER_FULL_NAME, loginStatusFromAPI.getFullName());

                    //Open user dashboard
                    startActivity(new Intent(getBaseContext(), UserDashboardActivity.class));
                    finish();

                } else{
                    Toast.makeText(getBaseContext(), loginStatusFromAPI.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginStatus> call, Throwable t) {
                Log.d(Constants.TRACE_TAG,t.getMessage());
            }
        });
    }

    private User validateData() {

        // Get data from all fields
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean flag = true;
        if (email.trim().isEmpty()) {
            emailEditText.setError("Mandatory field");
            flag = false;
        }
        if (password.trim().isEmpty()) {
            passwordEditText.setError("Mandatory field");
            flag = false;
        }

        if(flag){
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            return user;
        }
        else{
            return null;
        }
    }
}