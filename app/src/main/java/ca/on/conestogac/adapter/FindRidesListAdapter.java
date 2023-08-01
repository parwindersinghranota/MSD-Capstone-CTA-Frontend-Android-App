package ca.on.conestogac.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.on.conestogac.R;
import ca.on.conestogac.interfaces.RideDeleteButtonClickListener;
import ca.on.conestogac.interfaces.RideListItemClickListener;
import ca.on.conestogac.model.RidePost;
import ca.on.conestogac.utility.Utility;

public class FindRidesListAdapter extends RecyclerView.Adapter<FindRidesListAdapter.ViewHolder>
        implements Filterable {

    private final Context context;
    private final List<RidePost> ridesList;
    private List<RidePost> ridesListFiltered;
    private RideListItemClickListener rideListItemClickListener;
    private RideDeleteButtonClickListener rideDeleteButtonClickListener;

    public FindRidesListAdapter(Context context, List<RidePost> ridesList,
                            RideListItemClickListener rideListItemClickListener,
                            RideDeleteButtonClickListener rideDeleteButtonClickListener) {
        this.context = context;
        this.ridesList = ridesList;
        this.ridesListFiltered = ridesList;
        this.rideListItemClickListener = rideListItemClickListener;
        this.rideDeleteButtonClickListener = rideDeleteButtonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_single_ride_details_layout, parent, false);
        FindRidesListAdapter.ViewHolder viewHolder = new FindRidesListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewProvider.setText(ridesListFiltered.get(position).getProvider());
        holder.textViewDateTime.setText(ridesListFiltered.get(position).getDate() + " " + ridesListFiltered.get(position).getTime());
        holder.textViewRideDescription.setText(ridesListFiltered.get(position).getDescription());
        holder.bind(ridesListFiltered.get(position), rideListItemClickListener);

    }


    @Override
    public int getItemCount() {
        return ridesListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    ridesListFiltered = ridesList;
                } else {
                    List<RidePost> tempList = new ArrayList<>();
                    for(RidePost ridePost : ridesList){
                        if(ridePost.getDescription().toLowerCase().contains(charSequence.toString().toLowerCase())){
                            tempList.add(ridePost);
                        }
                    }
                    ridesListFiltered = tempList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = ridesListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                ridesListFiltered = (ArrayList<RidePost>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewProvider;
        TextView textViewDateTime;
        TextView textViewRideDescription;
        ImageButton imageButtonDeleteRide;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProvider = itemView.findViewById(R.id.textViewProvider);
            textViewDateTime = itemView.findViewById(R.id.textViewRideDateTime);
            textViewRideDescription = itemView.findViewById(R.id.textViewRideDescription);
            imageButtonDeleteRide = itemView.findViewById(R.id.imageButtonDeleteRide);
            imageButtonDeleteRide.setVisibility(View.INVISIBLE);
        }

        //List item click event
        public void bind(RidePost item, RideListItemClickListener rideListItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rideListItemClickListener.onItemClick(item);
                }
            });
        }

        //Delete Icon click event
        public void bind(Integer rideId, RideDeleteButtonClickListener rideDeleteButtonClickListener) {
            imageButtonDeleteRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rideDeleteButtonClickListener.onDeleteButtonClick(rideId);
                }
            });
        }


    }
}
