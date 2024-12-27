package ru.samsung.itschool.fifteen_puzzle;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private int fieldSize;
    private String playerName;
    private int movesCount = 0;

    private GridLayout gameGrid;
    private TextView timerText;
    private TextView movesText;

    private final Handler timerHandler = new Handler();
    private int secondsElapsed = 0;

    private Puzzle game;
    private Button restartButton;
    private Button recordsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fieldSize = getIntent().getIntExtra("fieldSize", 4);
        playerName = getIntent().getStringExtra("playerName");
        restartButton = findViewById(R.id.restartButton);
        recordsButton = findViewById(R.id.recordsButton);

        game = new Puzzle(fieldSize);

        gameGrid = findViewById(R.id.gameGrid);
        timerText = findViewById(R.id.timerText);
        movesText = findViewById(R.id.movesText);
        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(view -> confirmExit());
        setupOnBackPressedCallback();

        gameGrid.setRowCount(fieldSize);
        gameGrid.setColumnCount(fieldSize);

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int edgeSpacing = 20;
        gameGrid.setPadding(edgeSpacing, edgeSpacing, edgeSpacing, edgeSpacing);
        int screenWidth = metrics.widthPixels;

        int spacing = 10;
        int totalSpacing = (fieldSize + 1) * spacing;
        int tileSize = (screenWidth - totalSpacing * 2 - edgeSpacing * 2) / fieldSize;

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                Button tileButton = new Button(this);
                tileButton.setTextSize(30);

                tileButton.setBackgroundResource(R.drawable.puzzle_tile);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = tileSize;
                params.height = tileSize;
                params.setMargins(spacing, spacing, spacing, spacing);
                tileButton.setLayoutParams(params);
                int finalI = i;
                int finalJ = j;

                tileButton.setOnClickListener(v -> handleTileClick(finalI, finalJ));

                gameGrid.addView(tileButton);
            }
        }

        updateUI();
        startTimer();
        hideEndGameOptions();

        restartButton.setOnClickListener(v -> restartGame());
        recordsButton.setOnClickListener(v -> showRecords());
    }

    private void setupOnBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmExit();
            }
        });
    }

    private void confirmExit() {
        if (game.isSolved()) {
            finish();
        } else {
            new AlertDialog.Builder(GameActivity.this)
                    .setMessage("Вы уверены, что хотите выйти? Игра не завершена, текущий прогресс не сохранится.")
                    .setCancelable(false)
                    .setPositiveButton("Да", (dialog, id) -> finish())
                    .setNegativeButton("Нет", (dialog, id) -> dialog.dismiss())
                    .show();
        }
    }

    private void handleTileClick(int row, int col) {
        if (game.moveTile(row, col)) {
            movesCount++;
            updateUI();

            if (game.isSolved()) {
                stopTimer();
                Toast.makeText(this, "Поздравляем! Вы справились!", Toast.LENGTH_LONG).show();
                saveResult();
                showEndGameOptions();
            }
        }
    }

    private void updateUI() {
        int[][] board = game.getBoard();
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                Button tileButton = (Button) gameGrid.getChildAt(i * fieldSize + j);
                if (game.getBoard()[i][j] == 0) {
                    tileButton.setBackgroundResource(R.drawable.empty_tile);
                } else {
                    tileButton.setBackgroundResource(R.drawable.puzzle_tile);
                }
                int value = board[i][j];
                tileButton.setText(value == 0 ? "" : String.valueOf(value));
                tileButton.setEnabled(value != 0 && !game.isSolved());
            }
        }

        timerText.setText(String.format("Время: %d:%02d", secondsElapsed / 60, secondsElapsed % 60));
        movesText.setText("Ходы: " + movesCount);
    }

    private void startTimer() {
        timerHandler.post(new Runnable() {
            @Override
            public void run() {
                secondsElapsed++;
                timerText.setText(String.format("Время: %d:%02d", secondsElapsed / 60, secondsElapsed % 60));
                timerHandler.postDelayed(this, 1000);
            }
        });
    }

    private void stopTimer() {
        timerHandler.removeCallbacksAndMessages(null);
    }

    private void saveResult() {
        RecordsDatabaseHelper dbHelper = new RecordsDatabaseHelper(this);
        dbHelper.addRecord(playerName, secondsElapsed, movesCount, fieldSize);
    }

    private void showEndGameOptions() {
        restartButton.setVisibility(View.VISIBLE);
        recordsButton.setVisibility(View.VISIBLE);
    }

    private void hideEndGameOptions() {
        restartButton.setVisibility(View.GONE);
        recordsButton.setVisibility(View.GONE);
    }

    private void restartGame() {
        game = new Puzzle(fieldSize);
        movesCount = 0;
        secondsElapsed = 0;
        updateUI();
        startTimer();
        hideEndGameOptions();
    }

    private void showRecords() {
        Intent intent = new Intent(this, RecordsActivity.class);
        intent.putExtra("selectedFieldSize", fieldSize);
        startActivity(intent);
    }
}