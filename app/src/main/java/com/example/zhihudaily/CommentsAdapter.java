package com.example.zhihudaily;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context context;
    private List<Map<String, Object>> list;
    public final int ITEM_VIEW = 0;
    public final int COMMENT_L = 1;
    public final int COMMENT_S = 2;


    public CommentsAdapter(Context context,List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public int getItemViewType(int position) {
        if (Integer.valueOf(list.get(position).get("type").toString()) == ITEM_VIEW) {
            return ITEM_VIEW;
        } else if (Integer.valueOf(list.get(position).get("type").toString())==COMMENT_L){
            return COMMENT_L;
        }else
            return COMMENT_S;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_VIEW) {
            view = LayoutInflater.from(context).inflate(R.layout.item_comments, parent, false);
            return  new CommentsAdapter.CommentViewHolder(view);
        } else if(viewType == COMMENT_S){
            view = LayoutInflater.from(context).inflate(R.layout.comments_short, parent, false);
            return new CommentsAdapter.ShortViewHolder(view);
        }else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_long, parent, false);
        return new CommentsAdapter.LongViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CommentsAdapter.CommentViewHolder) {
            CommentsAdapter.CommentViewHolder viewHolder = (CommentsAdapter.CommentViewHolder) holder;
            viewHolder.show_name.setText(list.get(position).get("author").toString());
            viewHolder.show_comment.setText(list.get(position).get("content").toString());
            viewHolder.time.setText(secondToTime(list.get(position).get("time").toString()));
            Glide.with(context).load(list.get(position).get("image").toString()).into(viewHolder.imageView);
        }else if (holder instanceof CommentsAdapter.ShortViewHolder) {
            CommentsAdapter.ShortViewHolder viewHolder = (CommentsAdapter.ShortViewHolder) holder;
            viewHolder.show_s.setText((list.get(position).get("num").toString())+" 条短评");
        }else if(holder instanceof CommentsAdapter.LongViewHolder){
            CommentsAdapter.LongViewHolder viewHolder = (CommentsAdapter.LongViewHolder) holder;
            viewHolder.show_l.setText((list.get(position).get("num").toString())+" 条长评");

        }
    }

    @Override
    public int getItemCount()  {
        return list.size();
    }


    class LongViewHolder extends RecyclerView.ViewHolder {
        public TextView show_l;

        LongViewHolder(@NonNull View itemView) {
            super(itemView);
            show_l = itemView.findViewById(R.id.number);
        }
    }
    class ShortViewHolder extends RecyclerView.ViewHolder {
        public TextView show_s;

        ShortViewHolder(@NonNull View itemView) {
            super(itemView);
            show_s = itemView.findViewById(R.id.number);
        }
    }
    class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView show_name;
        private TextView show_comment;
        private ImageView imageView;
        private TextView time;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            show_name = itemView.findViewById(R.id.name);
            show_comment = itemView.findViewById(R.id.textView6);
            imageView = itemView.findViewById(R.id.pic);
            time = itemView.findViewById(R.id.textView8);

        }
    }
        public  String secondToTime(String time) {
            String dateStr = "1-1 08:00";
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            if (time.equals("0")) {
                return "";
            }
            Date miDate;
            String returnstr = "";
            try {
                miDate = sdf.parse(dateStr);
                Object t1 = miDate.getTime();
                long h1 = Long.parseLong(time.toString()) * 1000 + Long.parseLong(t1.toString());
                returnstr = sdf.format(h1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return returnstr;


    }
}
