package ca.on.conestogac.activities;

import static ca.on.conestogac.utility.Constants.TRACE_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import ca.on.conestogac.R;
import ca.on.conestogac.adapter.MessageListAdapter;
import ca.on.conestogac.adapter.RidesListAdapter;
import ca.on.conestogac.interfaces.AcceptRideRequestButtonClickListener;
import ca.on.conestogac.interfaces.DeclineRideRequestButtonClickListener;
import ca.on.conestogac.interfaces.DeleteMessageButtonClickListener;
import ca.on.conestogac.interfaces.ProfileInfoButtonClickListener;
import ca.on.conestogac.model.Message;
import ca.on.conestogac.model.RidePost;
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
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewProvider;
    private TextView textViewDateTime;
    private TextView textViewRideDescription;
    private TextView tvPreviousRide;
    private EditText editTextMessage;
    private ImageButton imageButtonSendMessage;
    private RecyclerView recyclerViewMessages;
    private RecyclerView.LayoutManager layoutManager;
    private Button buttonDriverFeedback;
    private int rideId;

    private MessageListAdapter messageListAdapter;

    SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
    String userEmail = sharedPreferencesUtility.getString(Constants.USER_EMAIL);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Utility.setActivityTitle(this, "Ride Details & Communication");
        initializeComponents();
    }

    private void initializeComponents() {
        textViewProvider = findViewById(R.id.textViewProvider);
        textViewDateTime = findViewById(R.id.textViewRideDateTime);
        textViewRideDescription = findViewById(R.id.textViewRideDescription);
        tvPreviousRide = findViewById(R.id.tvPreviousRide);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageButtonSendMessage = findViewById(R.id.imageButtonSendMessage);
        buttonDriverFeedback = findViewById(R.id.buttonDriverFeedback);

        imageButtonSendMessage.setOnClickListener(this);
        buttonDriverFeedback.setOnClickListener(this);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        //Recyclerview settings
        recyclerViewMessages.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewMessages.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        recyclerViewMessages.addItemDecoration(itemDecor);

        Bundle bundle = getIntent().getBundleExtra("ride_post_details");
        RidePost ridePost = (RidePost) bundle.get("ridePost");
        textViewDateTime.setText(ridePost.getDate() + ": " + ridePost.getTime());
        textViewProvider.setText(ridePost.getProvider());
        textViewRideDescription.setText(ridePost.getDescription());

        rideId = ridePost.getId();
        getMessagesListFromServer(rideId);

        //Fetch user email from shared preferences
        SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
        userEmail = sharedPreferencesUtility.getString(Constants.USER_EMAIL);

        checkIfAleadyHadRideWithThisDriver(ridePost.getProvider(), userEmail);
    }

    //Method to get the list of all messages.
    private void getMessagesListFromServer(int rideId) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<List<Message>> call = retrofitAPI.getMessages(rideId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {

                List<Message> messageFromAPI = response.body();

                if (messageFromAPI!=null && messageFromAPI.size() >= 0) {
                    messageListAdapter = new MessageListAdapter(MessageActivity.this,
                            messageFromAPI, profileInfoButtonClickListener,
                            deleteMessageButtonClickListener, userEmail, textViewProvider.getText().toString(),
                            acceptRideRequestButtonClickListener, declineRideRequestButtonClickListener);
                    recyclerViewMessages.setAdapter(messageListAdapter);
                    recyclerViewMessages.scrollToPosition(messageListAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.d(Constants.TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot fetch data. Please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method to send message from chat window
    private void postMessage(Message message) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<Message> call = retrofitAPI.postMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Message messageFromAPI = response.body();
                Log.d(Constants.TRACE_TAG, messageFromAPI != null ? messageFromAPI.toString() : "NULL_RETURNED_FROM_SERVER");
                if (messageFromAPI != null) {
                    Toast.makeText(getBaseContext(), "Message posted successfully !!",
                            Toast.LENGTH_SHORT).show();
                    editTextMessage.setText(null);
                    getMessagesListFromServer(rideId);
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.d(Constants.TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "Message not posted, please try again !!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method to update message from chat window
    private void updateRideStatus(Message message) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<Message> call = retrofitAPI.updateRideStatus(message.getId(), message.getRideConfirm());
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Message messageFromAPI = response.body();
                Log.d(Constants.TRACE_TAG, messageFromAPI != null ? messageFromAPI.toString() : "NULL_RETURNED_FROM_SERVER");
                if (messageFromAPI != null) {
                    Toast.makeText(getBaseContext(), "Ride request status changed",
                            Toast.LENGTH_SHORT).show();
                    getMessagesListFromServer(rideId);
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.d(Constants.TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "Message not updated, please try again !!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method to delete message
    private void deleteMessageFromServer(Integer messageId) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<String> call = retrofitAPI.deleteMessageById(messageId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String serverResponse = response.body().toString();
                Log.d(Constants.TRACE_TAG, serverResponse);
                if (serverResponse.equals(Constants.MESSAGE_DELETED)) {
                    Log.d(Constants.TRACE_TAG, Constants.MESSAGE_DELETED + " with Message ID: " + messageId);

                    Toast.makeText(getBaseContext(), Constants.MESSAGE_DELETED, Toast.LENGTH_SHORT).show();

                    if (userEmail != null) {
                        getMessagesListFromServer(rideId);
                    } else {
                        Toast.makeText(getBaseContext(), "Please login again !!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (serverResponse.equals(Constants.MESSAGE_NOT_FOUND)) {
                    Log.d(Constants.TRACE_TAG, Constants.MESSAGE_NOT_FOUND);
                    Toast.makeText(getBaseContext(), Constants.MESSAGE_NOT_FOUND,
                            Toast.LENGTH_SHORT).show();

                    if (userEmail != null) {
                        getMessagesListFromServer(rideId);
                    } else {
                        Toast.makeText(getBaseContext(), "Please login again !!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(Constants.TRACE_TAG, t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot delete the message, try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Method to share profileInfo with ride provider
    private void shareProfileInfo(){
        Message message = new Message();
        message.setMessage("PLEASE TAP ON PROFILE BUTTON TO SEE MY CONTACT DETAILS");
        message.setMessageTo(textViewProvider.getText().toString());
        message.setMessageFrom(userEmail);
        message.setRideId(rideId);
        message.setMessageType(Constants.MESSAGE_TYPE_PROFILE);
        postMessage(message);
    }

    //http request to the get user profile info
    private void fetchProfileDetails(String email) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<User> call = retrofitAPI.getUserProfile(email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                User userFromAPI = response.body();
                Log.d(Constants.TRACE_TAG, userFromAPI.toString());
                showProfileDialog(userFromAPI);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(Constants.TRACE_TAG,t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot fetch data. Please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //check if user had a ride previously with this driver
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
                   tvPreviousRide.setVisibility(View.VISIBLE);
                } else if (serverResponse.equals(Constants.RIDE_NOT_EXIST_WITH_DRIVER)) {
                    tvPreviousRide.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TRACE_TAG, t.getMessage());
                tvPreviousRide.setVisibility(View.GONE);
            }
        });
    }

    //Method to handle click events of various buttons
    @Override
    public void onClick(View v) {

        //method to handle post message event
        if (v == imageButtonSendMessage) {
            String messageText = editTextMessage.getText().toString().trim();
            if (messageText != null && !messageText.isEmpty()) {

                Message message = new Message();
                message.setMessage(messageText);
                message.setMessageTo(textViewProvider.getText().toString());
                message.setMessageFrom(userEmail);
                message.setRideId(rideId);
                message.setMessageType(Constants.MESSAGE_TYPE_MESSAGE);
                postMessage(message);

            }
        } else if(v == buttonDriverFeedback){
            Intent it = new Intent(getBaseContext(), DriverFeedbackActivity.class);
            it.putExtra("DRIVER_EMAIL", textViewProvider.getText().toString());
            startActivity(it);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.syncMessages) {
            getMessagesListFromServer(rideId);
        } else if (item.getItemId() == R.id.shareProfileDetails) {

            if(userEmail.equals(textViewProvider.getText().toString())){
                Toast.makeText(getBaseContext(), "You are trying to share your profile with your self.", Toast.LENGTH_LONG).show();
            } else {
                //show confirm dialog before sharing profile info
                new AlertDialog.Builder(this)
                        .setTitle("Share Profile Info")
                        .setMessage("Do you really want to share your profile information with ride provider ?")
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                shareProfileInfo();
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
            }
        }
        else if (item.getItemId() == R.id.requestRide) {

            if(userEmail.equals(textViewProvider.getText().toString())){
                Toast.makeText(getBaseContext(), "You are trying to request a ride to yourself only.", Toast.LENGTH_LONG).show();
            } else {
                //show confirm dialog before sharing profile info
                new AlertDialog.Builder(this)
                        .setTitle("Request Ride")
                        .setMessage("Are you sure to send ride request?")
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Message message = new Message();
                                message.setMessage(getString(R.string.accept_ride_message));
                                message.setMessageTo(textViewProvider.getText().toString());
                                message.setMessageFrom(userEmail);
                                message.setRideId(rideId);
                                message.setMessageType(Constants.MESSAGE_TYPE_RIDE_REQUEST);
                                message.setRideConfirm(-1);
                                postMessage(message);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProfileDialog(User user){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_user_profile_layout, null);
        dialogBuilder.setView(dialogView);

        TextView textViewFullName = dialogView.findViewById(R.id.textViewFullName);
        TextView textViewEmail = dialogView.findViewById(R.id.textViewEmail);
        TextView textViewMobileNo = dialogView.findViewById(R.id.textViewMobileNo);
        TextView textViewCollege = dialogView.findViewById(R.id.textViewCollege);
        TextView textViewCampus = dialogView.findViewById(R.id.textViewCampus);
        TextView textViewProgram = dialogView.findViewById(R.id.textViewProgram);

        textViewFullName.setText(user.getFirstName()+" "+user.getLastName());
        textViewEmail.setText(user.getEmail());
        textViewMobileNo.setText(user.getMobileNo());
        textViewCampus.setText(user.getCampus());
        textViewCollege.setText(user.getCollege());
        textViewProgram.setText(user.getProgram());

        dialogBuilder.setTitle("Profile Information")
        .setPositiveButton("OK", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    private ProfileInfoButtonClickListener profileInfoButtonClickListener = new ProfileInfoButtonClickListener() {
        @Override
        public void onProfileInfoButtonClick(Message message) {
            if(message.getMessageTo().equals(userEmail) || message.getMessageFrom().equals(userEmail)){
                fetchProfileDetails(message.getMessageFrom());
            } else{
                Toast.makeText(getBaseContext(), "You are not authorized to see this information", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private DeleteMessageButtonClickListener deleteMessageButtonClickListener = new DeleteMessageButtonClickListener() {
        @Override
        public void onDeleteMessageButtonClick(Message message) {
            if (userEmail.toLowerCase().equals(message.getMessageFrom())) {
                deleteMessageFromServer(message.getId());
            } else {
                Toast.makeText(getBaseContext(), "You can delete your own message only.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private AcceptRideRequestButtonClickListener acceptRideRequestButtonClickListener = new AcceptRideRequestButtonClickListener() {
        @Override
        public void onAcceptRideRequestButtonClick(Message message) {
            message.setRideConfirm(1);
            updateRideStatus(message);
        }
    };

    private DeclineRideRequestButtonClickListener declineRideRequestButtonClickListener = new DeclineRideRequestButtonClickListener() {
        @Override
        public void onDeclineRideRequestButtonClick(Message message) {
            message.setRideConfirm(0);
            updateRideStatus(message);
        }
    };
}