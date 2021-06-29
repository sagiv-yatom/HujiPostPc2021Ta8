package huji.postpc.y2021.hujipostpc2021ta8;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CalculateRootsWorker extends Worker {

    public CalculateRootsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        setProgressAsync(
                new Data.Builder()
                        .putInt("calcProgress", 0)
                        .build()
        );
    }

    @NonNull
    @Override
    public Result doWork() {
        // get the inputData from the request
        final int id = getInputData().getInt("id", 0);
        final long number = getInputData().getLong("number", 0);
        final long lastCalculation = getInputData().getLong("lastCalculation", 2);

        // save id and number in the work's progress
        setProgressAsync(
                new Data.Builder()
                        .putInt("id", id)
                        .putLong("number", number)
                        .build()
        );

        for (long i = lastCalculation; i <= number; i++) {
            System.out.println("number " + number + ", calcProgress: " + i);
            if (i >= 1000 && (i % 1000 == 0)) {
                // save the calculation progress in the work's progress
                setProgressAsync(
                        new Data.Builder()
                                .putInt("calcProgress", (int)(i * 100 / number))
                                .putLong("lastCalculation", i)
                                .build()
                );
            }


            if (number % i == 0) {

                return Result.success(
                        new Data.Builder()
                                .putLong("root1", i)
                                .putLong("root2", number / i)
                                .build()
                );
            }
        }

        return null;
    }
}