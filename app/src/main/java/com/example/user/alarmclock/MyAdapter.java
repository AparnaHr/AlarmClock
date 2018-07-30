package com.example.user.alarmclock;

import android.app.AlarmManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;
import com.example.user.alarmclock.model.Alarm;

import io.realm.Realm;
import io.realm.RealmResults;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Alarm> listItems;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Realm realm;
    private RealmResults<Alarm> results;
    private AlarmManager alarmManager;
    private int requestCode;
    private String setTime;
    private onClickTrashIconListener mOnClickTrashIconListener;


    // data is passed to the constructor
    MyAdapter(Context context, RealmResults<Alarm> data, Realm realm) {
        this.mInflater = LayoutInflater.from(context);
        this.results = data;
        this.realm = realm;
        listItems = realm.copyFromRealm(results);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = mInflater.inflate(R.layout.list_items, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String alarm = listItems.get(i).getAlarmTime();
        viewHolder.text.setText(alarm);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void updateAdapter(Alarm alarm) {
        listItems.add(alarm);
        notifyDataSetChanged();
    }

    public void deleteAlarm(int position, String setTime) {
        mOnClickTrashIconListener.cancelPendingIntent(setTime);
        listItems.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView text;
        public ImageButton imgDelete;
        public int pos;

        //public ImageButton btnDelete;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.idText);
            imgDelete = itemView.findViewById(R.id.idDelete);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String time = text.getText().toString();
                    deleteAlarm(pos, time);
                }
            });
        }

        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface onClickTrashIconListener {
        void cancelPendingIntent(String time);
    }

    public void setOnClickTrashIconListener(onClickTrashIconListener mOnClickTrashIconListener) {
        this.mOnClickTrashIconListener = mOnClickTrashIconListener;
    }

}
