package com.example.weather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<viewHolder>{

   private  List<Model> dataList;
    private Context context;

    public RecyclerAdapter(List<Model> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.time.setText(dataList.get(position).getTime());
        holder.temperature.setText(dataList.get(position).getTemperature()+"°C");
        holder.windspeed.setText(dataList.get(position).getWindspeed()+"Km/h");

        String weathericonUrl=dataList.get(position).getIcon();
        Log.d("IMAGE",weathericonUrl);
        while(holder.weathericon==null) {
            Glide.with(context).load(weathericonUrl).into(holder.weathericon);
        }
        SimpleDateFormat inputFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat outputFormat=new SimpleDateFormat("hh:mm aa");
        try{
            Date t=inputFormat.parse(dataList.get(position).getTime());
            holder.time.setText(outputFormat.format(t));
        } catch (ParseException e) {
             e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
class viewHolder extends RecyclerView.ViewHolder{

     TextView time ,temperature ,windspeed ;
     ImageView weathericon;
    public viewHolder(@NonNull View itemView) {
        super(itemView);
        time=itemView.findViewById(R.id.recycler_time);
        temperature=itemView.findViewById(R.id.recycler_temperature);
        windspeed=itemView.findViewById(R.id.recycler_windspeed);
        weathericon=itemView.findViewById(R.id.recycler_image);
    }
}
