package huji.postpc.y2021.hujipostpc2021ta8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    CalculationsApplication app;
    WorkManager workManager;
    CalculationsDB database;
    CalculationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (CalculationsApplication) getApplication();
        workManager = WorkManager.getInstance(this);

//        workManager.cancelAllWork();
//        workManager.pruneWork();

        if (savedInstanceState == null || !savedInstanceState.containsKey("dataBase")) {
            database = app.getDatabase();
        } else {
            database = (CalculationsDB) savedInstanceState.getSerializable("dataBase");
        }

        Button buttonCalculate = findViewById(R.id.buttonCalculate);
        EditText editTextInsertNumber = findViewById(R.id.editTextInsertNumber);
        RecyclerView recyclerView = findViewById(R.id.recyclerCalculationsList);

        LinearLayoutManager layout = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        adapter = new CalculationsAdapter(this, (CalculationsDB) database);
        recyclerView.setAdapter(adapter);

        buttonCalculate.setEnabled(false);

        // if there is an unfinished calculation, create a new request for it
        for (Calculation calculation : database.allCalculations) {
            if (calculation.getProgressPercent() < 100) {
                database.removeCalculationFromSP(calculation);
                String newId = setOneTimeWorkRequest(calculation);
                database.addCalculationToSP(newId);
            }
        }

        editTextInsertNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String editTextInput = editTextInsertNumber.getText().toString();
                try {
                    long inputNumber = Long.parseLong(editTextInput);
                    if (inputNumber > 0) {
                        buttonCalculate.setEnabled(true);
                    }
                }
                catch (NumberFormatException e) {
                    Log.e("NumberFormatException", "The input value is not a number.");
                    buttonCalculate.setEnabled(false);
                }
            }
        });

        // listen to the status (liveData) of the request
        LiveData<List<WorkInfo>> allLiveData = workManager.getWorkInfosByTagLiveData("calculation");
        allLiveData.observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                for (WorkInfo workInfo : workInfos) {
                    Data progress = workInfo.getProgress();
                    String id = workInfo.getId().toString();
                    Data workData = workInfo.getOutputData();
                    if (workInfo.getState() == WorkInfo.State.RUNNING) {
                        int calcProgress = progress.getInt("calcProgress", 0);
                        long lastCalculation = progress.getLong("lastCalculation", 2);
                        database.updateWorkProgress(id, calcProgress, lastCalculation);
                        adapter.notifyDataSetChanged();
                    } else if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        long root1 = workData.getLong("root1", -1);
                        long root2 = workData.getLong("root2", -1);
                        database.updateWorkIsDone(id, root1, root2);
                        adapter.notifyDataSetChanged();
                    }

                }
            }
        });

        buttonCalculate.setOnClickListener(v -> {
            long inputNumber = Long.parseLong(editTextInsertNumber.getText().toString());
            Calculation newCalculation = new Calculation(inputNumber);
            setOneTimeWorkRequest(newCalculation);
            database.addNewCalculation(newCalculation);
            adapter.notifyDataSetChanged();
            editTextInsertNumber.setText("");
        });
    }

    private String setOneTimeWorkRequest(Calculation calculation) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(CalculateRootsWorker.class)
                .addTag("calculation")
                .setInputData(
                        new Data.Builder()
                                .putLong("number", calculation.getNumber())
                                .putLong("lastCalculation", calculation.getLastCalculation())
                                .build()
                )
                .build();
        workManager.enqueue(request);

        calculation.setWorkerId(request.getId().toString());
        return request.getId().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Calculation calculation : database.allCalculations) {
            if (calculation.getProgressPercent() < 100) {
                workManager.cancelWorkById(UUID.fromString(calculation.getWorkerId()));
            }
        }
    }
}
