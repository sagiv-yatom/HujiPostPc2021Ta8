package huji.postpc.y2021.hujipostpc2021ta8;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;
import java.util.UUID;

public class CalculationsAdapter extends RecyclerView.Adapter<CalculationHolder> {

    Context context;
    CalculationsDB calculations;

    public CalculationsAdapter(Context c, CalculationsDB calculations) {
        this.context = c;
        this.calculations = calculations;
    }

    @NonNull
    @Override
    public CalculationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_calculation, parent, false);
        return new CalculationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalculationHolder holder, int position) {
        Calculation calculation = calculations.allCalculations.get(position);
        holder.textView.setText(holder.getCalculationText(calculation));

        holder.deleteButton.setOnClickListener(v -> {
            calculation.setState(State.DELETED);
            WorkManager.getInstance(context).cancelWorkById(UUID.fromString(calculation.getWorkerId()));
            calculations.deleteCalculation(calculation);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return calculations.allCalculations.size();
    }
}