package ru.samsung.itschool.fifteen_puzzle;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {
    private EditText playerName;
    private RadioGroup fieldSizeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        playerName = findViewById(R.id.playerName);
        fieldSizeGroup = findViewById(R.id.fieldSizeGroup);
        Button viewRecordsButton = findViewById(R.id.viewRecordsButton);
        Button startGameButton = findViewById(R.id.startGameButton);

        startGameButton.setOnClickListener(v -> {
            String name = playerName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(MainActivity.this, "Для дальнейшей игры введите имя.", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedSize = getSelectedFieldSize();

            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("playerName", name);
            intent.putExtra("fieldSize", selectedSize);
            startActivity(intent);
        });

        viewRecordsButton.setOnClickListener(v -> {
            int selectedSize = getSelectedFieldSize();
            Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
            intent.putExtra("selectedFieldSize", selectedSize);
            startActivity(intent);
        });
    }

    private int getSelectedFieldSize() {
        int selectedId = fieldSizeGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.size3x3) {
            return 3;
        } else if (selectedId == R.id.size4x4) {
            return 4;
        } else {
            return 5;
        }
    }
}
