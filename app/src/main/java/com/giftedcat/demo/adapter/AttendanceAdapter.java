package com.giftedcat.demo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giftedcat.demo.R;
import com.giftedcat.uploadhelp.db.Attendance;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author : giftedCat
 */
public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyHolder> {

    List<Attendance> dataList;
    private SimpleDateFormat dateFormat;

    public AttendanceAdapter(List<Attendance> dataList) {
        this.dataList = dataList;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance,
                parent, false);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return dataList != null && dataList.size() > 0 ? dataList.size() : 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView txt_id, txt_name, txt_date, txt_isupload;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            txt_id = itemView.findViewById(R.id.txt_id);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_isupload = itemView.findViewById(R.id.txt_isupload);
        }

        /**
         * 加载数据
         */
        public void setData(int position) {
            Attendance item = dataList.get(position);
            if (position != 0) {
                txt_id.setText(item.getId().toString());
                txt_name.setText(item.getName());
                txt_date.setText(dateFormat.format(item.getAttendanceDate()));
                txt_isupload.setText(item.getIsUpload().toString());
            } else {
                txt_id.setText("id");
                txt_name.setText("name");
                txt_date.setText("date");
                txt_isupload.setText("isUpload");
            }
        }
    }

}
