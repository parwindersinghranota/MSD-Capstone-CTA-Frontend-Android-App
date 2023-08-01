package ca.on.conestogac.adapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.on.conestogac.R;
import ca.on.conestogac.interfaces.AcceptRideRequestButtonClickListener;
import ca.on.conestogac.interfaces.DeclineRideRequestButtonClickListener;
import ca.on.conestogac.interfaces.DeleteMessageButtonClickListener;
import ca.on.conestogac.interfaces.ProfileInfoButtonClickListener;
import ca.on.conestogac.interfaces.RideDeleteButtonClickListener;
import ca.on.conestogac.interfaces.RideListItemClickListener;
import ca.on.conestogac.model.Message;
import ca.on.conestogac.model.RidePost;
import ca.on.conestogac.utility.Constants;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private final Context context;
    private final List<Message> messagesList;
    private ProfileInfoButtonClickListener profileInfoButtonClickListener;
    private DeleteMessageButtonClickListener deleteMessageButtonClickListener;
    private AcceptRideRequestButtonClickListener acceptRideRequestButtonClickListener;
    private DeclineRideRequestButtonClickListener declineRideRequestButtonClickListener;
    private String userEmail;
    private String rideProviderEmail;

    public MessageListAdapter(Context context, List<Message> messagesList,
                              ProfileInfoButtonClickListener profileInfoButtonClickListener,
                              DeleteMessageButtonClickListener deleteMessageButtonClickListener,
                              String userEmail,
                              String rideProviderEmail,
                              AcceptRideRequestButtonClickListener acceptRideRequestButtonClickListener,
                              DeclineRideRequestButtonClickListener declineRideRequestButtonClickListener) {
        this.context = context;
        this.messagesList = messagesList;
        this.profileInfoButtonClickListener = profileInfoButtonClickListener;
        this.deleteMessageButtonClickListener = deleteMessageButtonClickListener;
        this.userEmail = userEmail;
        this.rideProviderEmail = rideProviderEmail;
        this.acceptRideRequestButtonClickListener = acceptRideRequestButtonClickListener;
        this.declineRideRequestButtonClickListener = declineRideRequestButtonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_single_message_layout, parent, false);
        MessageListAdapter.ViewHolder viewHolder = new MessageListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewMessageFrom.setText(messagesList.get(position).getMessageFrom());
        holder.textViewMessage.setText(messagesList.get(position).getMessage());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        String formattedTimestamp = sdf.format(messagesList.get(position).getTimeStamp());
        holder.textViewDateTime.setText(formattedTimestamp);

        //show delete button if current logged in user matches to user
        if (userEmail.equals(messagesList.get(position).getMessageFrom())) {
            holder.imageButtonDeleteMessage.setVisibility(View.VISIBLE);
        }

        if (messagesList.get(position).getMessageType().equals(Constants.MESSAGE_TYPE_PROFILE)) {
            holder.imageButtonProfileInfo.setVisibility(View.VISIBLE);
        } else if (messagesList.get(position).getMessageType().equals(Constants.MESSAGE_TYPE_RIDE_REQUEST)) {

            //If logged in user is the person to whom request is sent then show him accept/reject button
            if (messagesList.get(position).getRideConfirm() == -1) {
                if (userEmail.equals(messagesList.get(position).getMessageTo())) {
                    holder.buttonAcceptRideRequest.setVisibility(View.VISIBLE);
                    holder.buttonDeclineRideRequest.setVisibility(View.VISIBLE);
                } else {
                    holder.buttonAcceptRideRequest.setVisibility(View.GONE);
                    holder.buttonDeclineRideRequest.setVisibility(View.GONE);
                }
                holder.textViewRideRequestStatus.setVisibility(View.GONE);
            } else if (messagesList.get(position).getRideConfirm() == 1) {
                holder.textViewRideRequestStatus.setVisibility(View.VISIBLE);
                holder.textViewRideRequestStatus.setText("REQUEST ACCEPTED");
                holder.textViewRideRequestStatus.setTextColor(context.getColor(android.R.color.holo_green_dark));
            } else if (messagesList.get(position).getRideConfirm() == 0) {
                holder.textViewRideRequestStatus.setVisibility(View.VISIBLE);
                holder.textViewRideRequestStatus.setText("REQUEST DECLINED");
                holder.textViewRideRequestStatus.setTextColor(context.getColor(android.R.color.holo_red_dark));
            }
        }

        holder.bind(messagesList.get(position), profileInfoButtonClickListener);
        holder.bind(messagesList.get(position), deleteMessageButtonClickListener);
        holder.bind(messagesList.get(position), acceptRideRequestButtonClickListener);
        holder.bind(messagesList.get(position), declineRideRequestButtonClickListener);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessageFrom;
        TextView textViewMessage;
        TextView textViewDateTime;
        ImageButton imageButtonDeleteMessage;
        ImageButton imageButtonProfileInfo;
        Button buttonAcceptRideRequest;
        Button buttonDeclineRideRequest;
        TextView textViewRideRequestStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessageFrom = itemView.findViewById(R.id.textViewMessageFrom);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            imageButtonDeleteMessage = itemView.findViewById(R.id.imageButtonDeleteMessage);
            imageButtonProfileInfo = itemView.findViewById(R.id.imageButtonProfileInfo);
            buttonDeclineRideRequest = itemView.findViewById(R.id.buttonDeclineRideRequest);
            buttonAcceptRideRequest = itemView.findViewById(R.id.buttonAcceptRideRequest);
            textViewRideRequestStatus = itemView.findViewById(R.id.textViewRideRequestStatus);
        }

        //profile info click event
        public void bind(Message message, DeleteMessageButtonClickListener deleteMessageButtonClickListener) {
            imageButtonDeleteMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMessageButtonClickListener.onDeleteMessageButtonClick(message);
                }
            });
        }

        //delete icon click event
        public void bind(Message message, ProfileInfoButtonClickListener profileInfoButtonClickListener) {
            imageButtonProfileInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileInfoButtonClickListener.onProfileInfoButtonClick(message);
                }
            });
        }

        //delete icon click event
        public void bind(Message message, AcceptRideRequestButtonClickListener acceptRideRequestButtonClickListener) {
            buttonAcceptRideRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptRideRequestButtonClickListener.onAcceptRideRequestButtonClick(message);
                }
            });
        }

        //delete icon click event
        public void bind(Message message, DeclineRideRequestButtonClickListener declineRideRequestButtonClickListener) {
            buttonDeclineRideRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    declineRideRequestButtonClickListener.onDeclineRideRequestButtonClick(message);
                }
            });
        }
    }
}
