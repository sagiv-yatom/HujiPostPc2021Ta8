package huji.postpc.y2021.hujipostpc2021ta8;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Set;

public class CalculationsDB {
    ArrayList<Calculation> allCalculations;
    Context context;
    private static SharedPreferences sp;

    public CalculationsDB(Context context) {
        this.context = context;
        this.allCalculations = new ArrayList<>();
        sp = context.getSharedPreferences("local_db_items", Context.MODE_PRIVATE);
        initializeFromSp();
    }

    private void initializeFromSp() {
        Set<String> keys = sp.getAll().keySet();
        for (String key : keys) {
            String calcSavedAsString = sp.getString(key, null);
            Calculation calculation = stringToCalculation(calcSavedAsString);

            for (Calculation calc : allCalculations) {
                if (calculation.getNumber() < calc.getNumber() && calculation.getState() == State.IN_PROGRESS) {
                    int i = allCalculations.indexOf(calc);
                    allCalculations.add(i, calculation);
                    break;
                }
            }

            if (!allCalculations.contains(calculation)) {
                allCalculations.add(calculation);
            }
        }
    }

    Calculation stringToCalculation(String string) {
        if (string == null) return null;
        try {
            String[] split = string.split("#");

            String workerId = split[0];
            State state = State.IN_PROGRESS;
            if (split[1].equals("DONE"))
                state = State.DONE;
            else if (split[1].equals("CANCELED"))
                state = State.CANCELED;
            long number = Long.parseLong(split[2]);
            long currentCalculation = Long.parseLong(split[3]);
            int progressPercent = Integer.parseInt(split[4]);
            long root1 = Long.parseLong(split[5]);
            long root2 = Long.parseLong(split[6]);

            Calculation calculation = new Calculation(number);

            calculation.setWorkerId(workerId);
            calculation.setState(state);
            calculation.setCurrentCalculation(currentCalculation);
            calculation.setProgressPercent(progressPercent);
            calculation.setRoot1(root1);
            calculation.setRoot2(root2);

            return calculation;
        } catch (Exception e) {
            System.out.println("exception");
            return null;
        }
    }

    public void addNewCalculation(Calculation calculation) {
        for (Calculation calc : allCalculations) {
            if (calculation.getNumber() < calc.getNumber()) {
                int i = allCalculations.indexOf(calc);
                allCalculations.add(i, calculation);
                break;
            }
        }

        if (!allCalculations.contains(calculation)) {
            allCalculations.add(calculation);
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(calculation.getWorkerId().toString(), calculation.serialize());
        editor.apply();
    }

    public void updateWorkProgress(String id, int progress, long lastCalc) {
        for (Calculation calculation : allCalculations) {
            if (calculation.getWorkerId().equals(id)) {
                calculation.setProgressPercent(progress);
                calculation.setCurrentCalculation(lastCalc);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(id, calculation.serialize());
                editor.apply();
                return;
            }
        }
    }

    public void updateWorkIsDone(String id, long root1, long root2) {
        for (Calculation calculation : allCalculations) {
            if (calculation.getWorkerId().equals(id)) {
                calculation.setProgressPercent(100);
                calculation.setState(State.DONE);
                calculation.setRoot1(root1);
                calculation.setRoot2(root2);

                Calculation tempCalculation = calculation;
                allCalculations.remove(calculation);
                allCalculations.add(tempCalculation);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(id, calculation.serialize());
                editor.apply();
                return;
            }
        }
    }

    public void deleteCalculation(Calculation calculation) {
        allCalculations.remove(calculation);

        SharedPreferences.Editor editor = sp.edit();
        editor.remove(calculation.getWorkerId());
        editor.apply();
    }
}
