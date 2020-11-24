package com.example.zhihudaily;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;


public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Map<String, Object>> list;
    public Context context;
    public final int ITEM_VIEW = 1;
    public final int DATE_VIEW = 2;

    public MainAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (Integer.valueOf(list.get(position).get("type").toString()) == DATE_VIEW) {
            return DATE_VIEW;
        } else
            return ITEM_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == DATE_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_diliver, parent, false);
            return new DateViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
            return new NewsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DateViewHolder) {
            DateViewHolder viewHolder = (DateViewHolder) holder;
            viewHolder.show_date.setText(list.get(position).get("date").toString());
        } else {
            NewsViewHolder viewHolder = (NewsViewHolder) holder;
            viewHolder.show_title.setText(list.get(position).get("title").toString());
            viewHolder.show_hint.setText(list.get(position).get("hint").toString());
            Glide.with(context).load(list.get(position).get("image").toString()).into(viewHolder.imageView);
            viewHolder.home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = list.get(position).get("id").toString();
                    String url = list.get(position).get("url").toString();
                    String hint = list.get(position).get("hint").toString();
                    String title = list.get(position).get("title").toString();
                    String image = list.get(position).get("image").toString();
                    Intent intent = new Intent(MainAdapter.this.context, NewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("url", url);
                    bundle.putString("hint", hint);
                    bundle.putString("title", title);
                    bundle.putString("image", image);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView show_title;
        private TextView show_hint;
        private ImageView imageView;
        private View home;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            show_title = itemView.findViewById(R.id.textView);
            show_hint = itemView.findViewById(R.id.textView5);
            imageView = itemView.findViewById(R.id.imageView2);
            home = itemView.findViewById(R.id.LinearLayout_item);

        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        public TextView show_date;

        DateViewHolder(@NonNull View itemView) {
            super(itemView);
            show_date = itemView.findViewById(R.id.date10);
        }
    }
}
