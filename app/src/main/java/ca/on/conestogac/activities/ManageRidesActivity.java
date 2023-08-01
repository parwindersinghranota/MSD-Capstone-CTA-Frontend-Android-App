package ca.on.conestogac.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

import ca.on.conestogac.R;
import ca.on.conestogac.adapter.RidesListAdapter;
import ca.on.conestogac.interfaces.RideDeleteButtonClickListener;
import ca.on.conestogac.interfaces.RideListItemClickListener;
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

public class ManageRidesActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton floatingActionButtonPostRide;
    private String[] monthsArray = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov", "Dec"};

    private RecyclerView recyclerViewRides;
    private RidesListAdapter ridesListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rides);
        Utility.setActivityTitle(this, "Manage Rides");
        initializeComponents();

        SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
        userEmail = sharedPreferencesUtility.getString(Constants.USER_EMAIL);
        if(userEmail!=null)
        {
            getRidesListFromServer(userEmail);
        } else{
            Toast.makeText(getBaseContext(), "Please login again !!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeComponents() {

        //Initializing components
        floatingActionButtonPostRide = findViewById(R.id.floatingActionButtonPostRide);
        recyclerViewRides = findViewById(R.id.recyclerViewRides);

        //Register events with button
        floatingActionButtonPostRide.setOnClickListener(this);

        //Recyclerview settings
        recyclerViewRides.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewRides.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        recyclerViewRides.addItemDecoration(itemDecor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_rides_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_channel).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                ridesListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                ridesListAdapter.getFilter().filter(query);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
        if (v == floatingActionButtonPostRide) {

            openPostRideDialog();
        }
    }

    //Method to open the custom dialog
    private void openPostRideDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_post_ride_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Add Ride Details");
        dialogBuilder.setCancelable(false);

        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        EditText editTextTime = dialogView.findViewById(R.id.editTextTime);
        CheckBox checkboxActive = dialogView.findViewById(R.id.checkboxActive);
        ImageButton imageButtonDate = dialogView.findViewById(R.id.imageButtonDate);
        ImageButton imageButtonTime = dialogView.findViewById(R.id.imageButtonTime);

        //Date Picker Click Listener
        imageButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ManageRidesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDate.setText(monthsArray[month]+" "+dayOfMonth+", "+year);
                    }
                }, year, month, dayOfMonth);

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                datePickerDialog.show();
            }
        });

        //Time Picker Click Listener
        imageButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Create the TimePickerDialog and set the properties
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageRidesActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editTextTime.setText(hourOfDay+":"+minute);
                    }
                }, hourOfDay, minute, false);

                // Show the Time Picker dialog
                timePickerDialog.show();
            }
        });

        //OK Button Click Listener
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                RidePost ridePost = validateData();
                if(ridePost != null){
                    ridePostRequest(ridePost);
                }
            }

            private RidePost validateData(){
                // Get data from all fields
                String description = editTextDescription.getText().toString();
                String date = editTextDate.getText().toString();
                String time = editTextTime.getText().toString();
                boolean flag = true;
                if (description.trim().isEmpty()) {
                    editTextDescription.setError("Mandatory field");
                    flag = false;
                }
                if (date.trim().isEmpty()) {
                    editTextDate.setError("Mandatory field");
                    flag = false;
                }
                if (time.trim().isEmpty()) {
                    editTextTime.setError("Mandatory field");
                    flag = false;
                }

                if(flag){
                    RidePost ridePost = new RidePost();
                    ridePost.setDate(date);
                    ridePost.setTime(time);
                    ridePost.setDescription(description);

                    SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
                    String userEmail = sharedPreferencesUtility.getString(Constants.USER_EMAIL);
                    ridePost.setProvider(userEmail);
                    ridePost.setActive(true);

                    return ridePost;
                }
                else{
                    return null;
                }
            }
        });

        //Cancel Button Click Listener
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();;
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    //http request to the server to add ride
    private void ridePostRequest(RidePost ridePost) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<RidePost> call = retrofitAPI.postRide(ridePost);
        call.enqueue(new Callback<RidePost>() {
            @Override
            public void onResponse(Call<RidePost> call, Response<RidePost> response) {

                RidePost ridePostFromAPI = response.body();
                Log.d(Constants.TRACE_TAG,ridePostFromAPI.toString());
                if(ridePostFromAPI.getProvider()!=null){
                    Toast.makeText(getBaseContext(), "Ride posted successfully !!",
                            Toast.LENGTH_SHORT).show();
                    getRidesListFromServer(ridePostFromAPI.getProvider());
                }
            }

            @Override
            public void onFailure(Call<RidePost> call, Throwable t) {
                Log.d(Constants.TRACE_TAG,t.getMessage());
                Toast.makeText(getBaseContext(), "Ride not posted, please try again !!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //http request to the get list of rides
    private void getRidesListFromServer(String email) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<List<RidePost>> call = retrofitAPI.getRidesList(email);
        call.enqueue(new Callback<List<RidePost>>() {
            @Override
            public void onResponse(Call<List<RidePost>> call, Response<List<RidePost>> response) {

                List<RidePost> ridePostFromAPI = response.body();
                ridesListAdapter = new RidesListAdapter(ManageRidesActivity.this, ridePostFromAPI, ridesListItemClickListener, rideDeleteButtonClickListener);
                recyclerViewRides.setAdapter(ridesListAdapter);
            }

            @Override
            public void onFailure(Call<List<RidePost>> call, Throwable t) {
                Log.d(Constants.TRACE_TAG,t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot fetch data. Please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //http request to the delete selected ride
    private void deleteRideFromServer(Integer rideId) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<String> call = retrofitAPI.deleteRideById(rideId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String serverResponse = response.body().toString();
                Log.d(Constants.TRACE_TAG, serverResponse);
                if(serverResponse.equals(Constants.RIDE_DELETED)){
                    Log.d(Constants.TRACE_TAG,Constants.RIDE_DELETED+" with Ride ID: "+rideId);

                    Toast.makeText(getBaseContext(), Constants.RIDE_DELETED,
                            Toast.LENGTH_SHORT).show();

                    if(userEmail!=null)
                    {
                        getRidesListFromServer(userEmail);
                    } else{
                        Toast.makeText(getBaseContext(), "Please login again !!",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                else if(serverResponse.equals(Constants.RIDE_NOT_FOUND)){
                    Log.d(Constants.TRACE_TAG,Constants.RIDE_NOT_FOUND+" with Ride ID: "+rideId);
                    Toast.makeText(getBaseContext(), Constants.RIDE_NOT_FOUND,
                            Toast.LENGTH_SHORT).show();

                    if(userEmail!=null)
                    {
                        getRidesListFromServer(userEmail);
                    } else{
                        Toast.makeText(getBaseContext(), "Please login again !!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(Constants.TRACE_TAG,t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot delete the ride, try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Ride Item Click Listener
    private RideListItemClickListener ridesListItemClickListener = new RideListItemClickListener(){

        @Override
        public void onItemClick(RidePost ridePost) {
            Intent it = new Intent(getBaseContext(), MessageActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("ridePost", ridePost);
            it.putExtra("ride_post_details", b);
            startActivity(it);
        }
    };

    //Delete Ride Click Event
    private RideDeleteButtonClickListener rideDeleteButtonClickListener = new RideDeleteButtonClickListener() {
        @Override
        public void onDeleteButtonClick(Integer rideId) {
            deleteRideFromServer(rideId);
        }
    };

}
