package com.example.alarm.adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alarm.R;
import com.example.alarm.model.Alarm;
import com.example.alarm.database.AlarmDatabaseHelper;
import com.example.alarm.util.AlarmUtils;

import java.util.List;
import java.util.Locale;

public class AlarmListAdapter extends BaseAdapter {

    private Context context;
    private List<Alarm> alarmList;
    private OnAlarmActionListener listener;

    public interface OnAlarmActionListener {
        void onEditAlarm(Alarm alarm);
        void onDeleteAlarm(Alarm alarm);
    }

    public AlarmListAdapter(Context context, List<Alarm> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
    }

    public void setOnAlarmActionListener(OnAlarmActionListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return alarmList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Alarm alarm = alarmList.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
            holder = new ViewHolder();
            holder.timeText = convertView.findViewById(R.id.timeText);
            holder.labelText = convertView.findViewById(R.id.labelText);
            holder.alarmSwitch = convertView.findViewById(R.id.alarmSwitch);
            holder.btnEdit = convertView.findViewById(R.id.btnEdit);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Format giờ:phút
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", alarm.getHour(), alarm.getMinute());
        holder.timeText.setText(timeFormatted);
        holder.labelText.setText(alarm.getLabel());

        holder.alarmSwitch.setChecked(alarm.isEnabled());
        holder.alarmSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            alarm.setEnabled(isChecked);

            // Update database
            AlarmDatabaseHelper db = new AlarmDatabaseHelper(context);
            db.updateAlarm(alarm);

            // Schedule or cancel alarm based on state
            if (isChecked) {
                AlarmUtils.scheduleAlarm(context, alarm);
            } else {
                AlarmUtils.cancelAlarm(context, alarm.getId());
            }

            // Animation khi bật/tắt
            View card = (View) buttonView.getParent().getParent();
            card.animate()
                    .scaleX(isChecked ? 1.02f : 1f)
                    .scaleY(isChecked ? 1.02f : 1f)
                    .alpha(isChecked ? 1f : 0.7f)
                    .setDuration(250)
                    .start();
        });

        // Set up edit button click listener
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditAlarm(alarm);
            }
        });

        // Set up delete button click listener
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteAlarm(alarm);
            }
        });
        convertView.setAlpha(0f);
        convertView.animate().alpha(1f).setDuration(300).start();

        return convertView;
    }

    static class ViewHolder {
        TextView timeText;
        TextView labelText;
        Switch alarmSwitch;
        ImageButton btnEdit;
        ImageButton btnDelete;
    }
}
