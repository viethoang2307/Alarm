package com.example.alarm.activity;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class QuickAlarmDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String[] items = {"5 phút", "10 phút", "15 phút"};
        return new AlertDialog.Builder(getActivity())
                .setTitle("Báo thức nhanh")
                .setItems(items, (dialog, which) -> {
                    // Xử lý đặt báo thức nhanh ở đây
                })
                .setNegativeButton("Hủy", null)
                .create();
    }
} 