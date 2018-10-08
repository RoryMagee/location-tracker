package rory.pmcm01_mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Event> mDataset;
    private static final String TAG = "ADAPTER";
    private Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView nameView;
        public TextView locationView;
        public TextView createdByView;
        public TextView completedView;
        public TextView startDateView;
        public TextView endDateView;
        public ViewHolder(View view) {
            super(view);
            cardView = (CardView)view.findViewById(R.id.cardView);
            nameView = (TextView)view.findViewById(R.id.name);
            locationView = (TextView)view.findViewById(R.id.location);
            createdByView = (TextView)view.findViewById(R.id.createdBy);
            completedView = (TextView)view.findViewById(R.id.completed);
            startDateView = (TextView)view.findViewById(R.id.startDate);
            endDateView = (TextView)view.findViewById(R.id.endDate);
        }
    }


    public MyAdapter(ArrayList<Event> myDataset, Context context) {
        this.mDataset = myDataset;
        this.mContext = context;
        Log.d(TAG, "MyAdapter: ADAPTER CREATED");
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.nameView.setText(mDataset.get(position).name);
        holder.locationView.setText(mDataset.get(position).location.getName());
        holder.createdByView.setText(mDataset.get(position).createdBy);
        holder.completedView.setText(mDataset.get(position).completed.toString());
        holder.startDateView.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(mDataset.get(position).startDate).toString());
        holder.endDateView.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(mDataset.get(position).endDate).toString());
        Log.d("Holder", holder.toString());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: card view clicked");
                Intent intent = new Intent(mContext, eventActivity.class);
                intent.putExtra("eventName", mDataset.get(position).name);
                intent.putExtra("eventLocation", mDataset.get(position).location.getName());
                intent.putExtra("eventCreatedBy", mDataset.get(position).createdBy);
                intent.putExtra("eventCompleted", mDataset.get(position).completed);
                intent.putExtra("eventStartDate", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(mDataset.get(position).startDate).toString());
                intent.putExtra("eventEndDate", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(mDataset.get(position).endDate).toString());
                intent.putParcelableArrayListExtra("invitedUsers", mDataset.get(position).invitedUsers);
                intent.putExtra("usersAttended", mDataset.get(position).usersAttended);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
