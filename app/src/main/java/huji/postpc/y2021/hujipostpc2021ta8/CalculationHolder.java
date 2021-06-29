package huji.postpc.y2021.hujipostpc2021ta8;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalculationHolder extends RecyclerView.ViewHolder {

    TextView textView;
    Button deleteButton;

    public CalculationHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.calculationTextView);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }

    public String getCalculationText(Calculation calculation) {
        if (calculation.getProgressPercent() != 100) {
            return "Calculation for " + calculation.getNumber() + " (" + calculation.getProgressPercent() + "%)";
        }
        else {
            if (calculation.getRoot1() == 1 || calculation.getRoot2() == 1) {
                return calculation.getNumber() + " is prime";
            } else {
                return "Roots for " + calculation.getNumber() + ": " + calculation.getRoot1() + " x " + calculation.getRoot2();
            }
        }
    }
}