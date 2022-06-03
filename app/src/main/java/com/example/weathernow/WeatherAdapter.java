package com.example.weathernow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherObject> weatherObjectArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherObject> weatherObjectArrayList) {
        this.context = context;
        this.weatherObjectArrayList = weatherObjectArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        WeatherObject weatherObject = weatherObjectArrayList.get(position);
        holder.temperature.setText(weatherObject.getTemperature() + "°C");
        Picasso.get().load("https:".concat(weatherObject.getIcon())).into(holder.condition);
        holder.windSpeed.setText(weatherObject.getWindSpeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date date = input.parse(weatherObject.getTime());
            holder.time.setText(output.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherObjectArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windSpeed, temperature, time;
        private ImageView condition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windSpeed = itemView.findViewById(R.id.idWindSpeed);
            temperature = itemView.findViewById(R.id.idTemperatureTV);
            time = itemView.findViewById(R.id.idTimeTV);
            condition = itemView.findViewById(R.id.idCondition2TV);
        }
    }
}
