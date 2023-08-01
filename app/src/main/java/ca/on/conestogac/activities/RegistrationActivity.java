package ca.on.conestogac.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import ca.on.conestogac.R;
import ca.on.conestogac.model.User;
import ca.on.conestogac.network.RetrofitAPI;
import ca.on.conestogac.utility.Constants;
import ca.on.conestogac.utility.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextCollegeName;
    private EditText editTextCampus;
    private EditText editTextProgram;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextSecurityAnswer;
    private EditText editTextMobileNo;
    private Spinner spinnerSecurityQuestion;
    private ProgressBar progressBarLoading;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Utility.setActivityTitle(this, "User Registration");
        initializeComponents();
    }

    private void initializeComponents() {

        //Refer components from XML layout file
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextCollegeName = findViewById(R.id.editTextCollegeName);
        editTextCampus = findViewById(R.id.editTextCampus);
        editTextProgram = findViewById(R.id.editTextProgram);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextSecurityAnswer = findViewById(R.id.editTextSecurityAnswer);
        editTextMobileNo = findViewById(R.id.editTextMobileNo);
        spinnerSecurityQuestion = findViewById(R.id.spinnerSecurityQuestion);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        //Register button with click event
        buttonRegister.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            User user = validateData();
            if (user != null) {
                progressBarLoading.setVisibility(View.VISIBLE);
                createUser(user);
            }
        }
    }

    private User validateData() {

        // Get data from all fields
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String collegeName = editTextCollegeName.getText().toString();
        String campus = editTextCampus.getText().toString();
        String program = editTextProgram.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String securityQuestion = spinnerSecurityQuestion.getSelectedItem().toString();
        String securityAnswer = editTextSecurityAnswer.getText().toString();
        String mobileNo = editTextMobileNo.getText().toString();

        boolean flag = true;

        if (firstName.trim().isEmpty()) {
            editTextFirstName.setError("Mandatory field");
            flag = false;
        }
        if (lastName.trim().isEmpty()) {
            editTextLastName.setError("Mandatory field");
            flag = false;
        }
        if (collegeName.trim().isEmpty()) {
            editTextCollegeName.setError("Mandatory field");
            flag = false;
        }
        if (campus.trim().isEmpty()) {
            editTextCampus.setError("Mandatory field");
            flag = false;
        }
        if (program.trim().isEmpty()) {
            editTextProgram.setError("Mandatory field");
            flag = false;
        }
        if (email.trim().isEmpty()) {
            editTextEmail.setError("Mandatory field");
            flag = false;
        }
        if (mobileNo.trim().isEmpty()) {
            editTextMobileNo.setError("Mandatory field");
            flag = false;
        }
        if (password.trim().isEmpty()) {
            editTextPassword.setError("Mandatory field");
            flag = false;
        }
        if (confirmPassword.trim().isEmpty()) {
            editTextConfirmPassword.setError("Mandatory field");
            flag = false;
        }
        if (securityAnswer.trim().isEmpty()) {
            editTextSecurityAnswer.setError("Mandatory field");
            flag = false;
        }

        if (flag) {
            if (!password.equals(confirmPassword)) {
                editTextConfirmPassword.setError("Password and Confirm Password do not match");
                return null;
            } else {
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPassword(password);
                user.setProgram(program);
                user.setCollege(collegeName);
                user.setCampus(campus);
                user.setSecurityQuestion(securityQuestion);
                user.setSecurityAnswer(securityAnswer);
                user.setActive(true);
                user.setMobileNo(mobileNo);
                return user;
            }
        }
        return null;
    }

    private void createUser(User user) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<User> call = retrofitAPI.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                progressBarLoading.setVisibility(View.INVISIBLE);
                User userFromAPI = response.body();
                Log.d(Constants.TRACE_TAG,userFromAPI.toString());
                if(userFromAPI.getEmail()==null){
                    editTextEmail.setError("Email already registered");
                } else{
                    Toast.makeText(getBaseContext(), "Registration done successfully !!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(Constants.TRACE_TAG,t.getMessage());
            }
        });
    }
}