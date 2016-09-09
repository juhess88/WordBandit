package me.jhessstudios.wordbandit;

/**
 * Created by Juhess88 on 5/7/2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by justinhess on 4/16/15.
 */
public class RecyclerShareAdapter extends RecyclerView.Adapter<RecyclerShareAdapter.MyViewHolder> {

    private LayoutInflater inflater;

    List<RecyclerShareModel> data = Collections.emptyList();

    private Context context;

    public RecyclerShareAdapter(Context context, List<RecyclerShareModel> data) {

        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //represents root of custom row
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    //set data that correspond to current row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        RecyclerShareModel current = data.get(position);
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;

        MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listText);
            icon = (ImageView) itemView.findViewById(R.id.listImage);
        }
    }
}
