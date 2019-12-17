package com.example.battleship;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.battleship.model.GameStats;

import java.util.List;

class StatsViewHolderItem {
    TextView user1;
    TextView user2;
    TextView score1;
    TextView score2;
    TextView date;
}

public class StatsAdapter extends ArrayAdapter<GameStats> {
    private LayoutInflater inflater;
    private int layoutResourceId;
    private List<GameStats> stats;

    public StatsAdapter(Context context, int resource, List<GameStats> stats) {
        super(context, resource, stats);
        this.stats = stats;
        this.layoutResourceId = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        StatsViewHolderItem statsViewHolderItem;

        if (convertView == null) {

            convertView = inflater.inflate(layoutResourceId, parent, false);

            statsViewHolderItem = new StatsViewHolderItem();
            statsViewHolderItem.user1 = convertView.findViewById(R.id.user1_stats);
            statsViewHolderItem.user2 = convertView.findViewById(R.id.user2_stats);
            statsViewHolderItem.score1 = convertView.findViewById(R.id.score1_stats);
            statsViewHolderItem.score2 = convertView.findViewById(R.id.score2_stats);
            statsViewHolderItem.date = convertView.findViewById(R.id.date_stats);

            convertView.setTag(statsViewHolderItem);

        } else {
            statsViewHolderItem = (StatsViewHolderItem) convertView.getTag();
        }

        GameStats stat = stats.get(position);

        statsViewHolderItem.user1.setText(stat.user1Email);
        statsViewHolderItem.user2.setText(stat.user2Email);

        statsViewHolderItem.score1.setText(String.valueOf(stat.score1));
        statsViewHolderItem.score2.setText(String.valueOf(stat.score2));
        String date = new SimpleDateFormat("yyyy-MM-dd").format(stat.date) + "\n" +
                new SimpleDateFormat("hh:mm:ss").format(stat.date);
        statsViewHolderItem.date.setText(date);

        return convertView;
    }
}
