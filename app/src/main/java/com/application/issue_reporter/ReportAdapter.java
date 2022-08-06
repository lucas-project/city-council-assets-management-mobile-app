package com.application.issue_reporter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;


public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportItemViewHolder> {


    private LayoutInflater mInflater;
    private List<Report> mDatas;
    Context context;
    public ReportAdapter(Context context, List<Report> datas) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public ReportItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_report, parent, false);
        return new ReportItemViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ReportItemViewHolder holder, int i) {
        final  int position = i;
        Report report = mDatas.get(position);
        holder.item_title.setText(report.getSort());
        holder.item_ticket.setText("No:"+ report.getPushkey());
        holder.item_detail.setText(report.getDetail());
        RequestOptions options = new RequestOptions()
                .placeholder(new ColorDrawable(Color.parseColor("#f2f3f5")))
                .error(new ColorDrawable(Color.parseColor("#f2f3f5")));
        Glide.with(context).load(report.getImageurl()).apply(options).into(holder.item_img);
        holder.item_statusimg.setImageDrawable(report.getStatus().equals("1")?
                context.getResources().getDrawable(R.mipmap.info_prograss)
                : context.getResources().getDrawable(R.mipmap.info_finish));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.setOnItemClickListener(position);
                }
            }
        });
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Report> reportList) {
        mDatas.addAll(reportList);
        notifyDataSetChanged();
    }

    public Report getdata(int position) {
        return mDatas.get(position);
    }



    class ReportItemViewHolder extends RecyclerView.ViewHolder {
        public TextView item_title;
        public TextView item_detail;
        public TextView item_ticket;
        public ImageView item_img;
        public ImageView item_statusimg;
        public ReportItemViewHolder(View itemView) {
            super(itemView);
            item_title =  itemView.findViewById(R.id.item_title);
            item_detail =  itemView.findViewById(R.id.item_detail);
            item_ticket =  itemView.findViewById(R.id.ticket);
            item_img =  itemView.findViewById(R.id.item_img);
            item_statusimg =  itemView.findViewById(R.id.item_statusimg);

        }


    }

    private ItemClickListener listener;

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public interface ItemClickListener {
        void setOnItemClickListener(int position);
    }

}
