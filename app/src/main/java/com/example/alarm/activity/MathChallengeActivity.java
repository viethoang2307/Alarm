package com.example.alarm.activity;



import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import com.example.alarm.R;
import com.example.alarm.service.AlarmService;

import java.util.Random;

public class MathChallengeActivity extends AppCompatActivity {

    private TextView questionText;
    private EditText answerInput;
    private Button confirmBtn;

    private int correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_challenge);

        questionText = findViewById(R.id.questionText);
        answerInput = findViewById(R.id.answerInput);
        confirmBtn = findViewById(R.id.confirmButton);

        generateMathQuestion();

        confirmBtn.setOnClickListener(v -> {
            String input = answerInput.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập câu trả lời", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int userAnswer = Integer.parseInt(input);
                if (userAnswer == correctAnswer) {
                    stopService(new Intent(this, AlarmService.class));
                    Toast.makeText(this, "Báo thức đã tắt!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Sai rồi! Thử lại.", Toast.LENGTH_SHORT).show();
                    answerInput.setText("");
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Sai định dạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateMathQuestion() {
        Random rand = new Random();
        int a = rand.nextInt(30) + 10; // 10 - 39
        int b = rand.nextInt(30) + 10; // 10 - 39

        correctAnswer = a + b;
        questionText.setText("Giải toán để tắt báo:\n" + a + " + " + b + " = ?");
    }
}
