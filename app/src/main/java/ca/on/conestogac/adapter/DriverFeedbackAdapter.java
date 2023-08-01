package ca.on.conestogac.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import ca.on.conestogac.R;
import ca.on.conestogac.model.Feedback;

public class DriverFeedbackAdapter extends RecyclerView.Adapter<DriverFeedbackAdapter.ViewHolder>{

    private final List<Feedback> feedbackList;

    public DriverFeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_single_layout_driver_feedback, parent, false);
        DriverFeedbackAdapter.ViewHolder viewHolder = new DriverFeedbackAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewEmail.setText(feedbackList.get(position).getUser());
        holder.textViewFeedback.setText(feedbackList.get(position).getMessage());
        holder.ratingBarRatings.setRating((float)(feedbackList.get(position).getRating()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        String formattedTimestamp = sdf.format(feedbackList.get(position).getDateTime());
        holder.textViewDateTime.setText(formattedTimestamp);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEmail;
        TextView textViewFeedback;
        TextView textViewDateTime;
        RatingBar ratingBarRatings;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewFeedback = itemView.findViewById(R.id.textViewFeedback);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            ratingBarRatings = itemView.findViewById(R.id.ratingBarRatings);
        }
    }
}
