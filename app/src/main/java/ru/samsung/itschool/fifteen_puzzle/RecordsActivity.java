package ru.samsung.itschool.fifteen_puzzle;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecordsActivity extends AppCompatActivity {
    private TableLayout recordsTable;
    private RecordsDatabaseHelper dbHelper;
    private int selectedFieldSize;
    private String currentSortColumn = "time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button clearHistoryButton = findViewById(R.id.clearHistoryButton);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        clearHistoryButton.setOnClickListener(v -> showClearHistoryDialog());

        selectedFieldSize = getIntent().getIntExtra("selectedFieldSize", 4);
        recordsTable = findViewById(R.id.recordsTable);
        Spinner fieldSizeSpinner = findViewById(R.id.fieldSizeSpinner);
        dbHelper = new RecordsDatabaseHelper(this);

        if (isFirstRun) {
            dbHelper.resetDatabase();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.field_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldSizeSpinner.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(String.valueOf(selectedFieldSize));
        fieldSizeSpinner.setSelection(spinnerPosition);

        fieldSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFieldSize = Integer.parseInt(parent.getItemAtPosition(position).toString());
                loadRecords();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        loadRecords();
    }

    private void showClearHistoryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Очистить историю")
                .setMessage("Вы уверены, что хотите удалить всю историю результатов? Это действие необратимо.")
                .setPositiveButton("Удалить", (dialog, which) -> clearAllRecords())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void clearAllRecords() {
        dbHelper.resetDatabase();
        loadRecords();
    }

    private void loadRecords() {
        List<String[]> records = dbHelper.getFilteredRecords(selectedFieldSize, currentSortColumn);
        displayRecords(records);
    }

    private void displayRecords(List<String[]> records) {
        recordsTable.removeAllViews();

        TableRow headerRow = new TableRow(this);

        addHeaderCell(headerRow, "Игрок", "player_name");
        addHeaderCell(headerRow, "Время", "time");
        addHeaderCell(headerRow, "Ходы", "moves");

        recordsTable.addView(headerRow);
        for (String[] record : records) {
            TableRow row = new TableRow(this);
            for (String cell : record) {
                TextView cellView = new TextView(this);
                cellView.setText(cell);
                cellView.setPadding(16, 8, 16, 8);
                row.addView(cellView);
            }
            recordsTable.addView(row);
        }
    }

    private void addHeaderCell(TableRow headerRow, String title, String columnName) {
        TextView headerCell = new TextView(this);
        headerCell.setText(title);
        headerCell.setPadding(16, 8, 16, 8);
        headerCell.setOnClickListener(v -> {
            currentSortColumn = columnName;
            loadRecords();
        });
        headerRow.addView(headerCell);
    }
}
