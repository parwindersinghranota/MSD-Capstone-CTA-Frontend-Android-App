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

import java.util.Calendar;
import java.util.List;

import ca.on.conestogac.R;
import ca.on.conestogac.adapter.FindRidesListAdapter;
import ca.on.conestogac.adapter.RidesListAdapter;
import ca.on.conestogac.interfaces.RideDeleteButtonClickListener;
import ca.on.conestogac.interfaces.RideListItemClickListener;
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

public class FindRidesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRides;
    private FindRidesListAdapter findRidesListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_rides);
        Utility.setActivityTitle(this, "Available Rides");
        initializeComponents();

        SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance(getBaseContext());
        userEmail = sharedPreferencesUtility.getString(Constants.USER_EMAIL);
        if(userEmail!=null)
        {
            getRidesListFromServer();
        } else{
            Toast.makeText(getBaseContext(), "Please login again !!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeComponents() {

        //Initializing components
        recyclerViewRides = findViewById(R.id.recyclerViewRides);

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
                findRidesListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                findRidesListAdapter.getFilter().filter(query);
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

    //http request to the get list of rides
    private void getRidesListFromServer() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<List<RidePost>> call = retrofitAPI.getLatestRidesList();
        call.enqueue(new Callback<List<RidePost>>() {
            @Override
            public void onResponse(Call<List<RidePost>> call, Response<List<RidePost>> response) {

                List<RidePost> ridePostFromAPI = response.body();
                findRidesListAdapter = new FindRidesListAdapter(FindRidesActivity.this, ridePostFromAPI, ridesListItemClickListener, null);
                recyclerViewRides.setAdapter(findRidesListAdapter);
            }

            @Override
            public void onFailure(Call<List<RidePost>> call, Throwable t) {
                Log.d(Constants.TRACE_TAG,t.getMessage());
                Toast.makeText(getBaseContext(), "Cannot fetch data. Please try again",
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


}