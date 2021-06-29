package huji.postpc.y2021.hujipostpc2021ta8;


import android.widget.Button;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29, application = CalculationsApplication.class)
public class MainActivityTests extends TestCase {

    @Test
    public void whenInitApplicationWithCalculation_shouldInitCalculationSuccessfully()
    {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();
        CalculationsDB database = CalculationsApplication.getInstance().getDatabase();
        database.addNewCalculation(new Calculation(45));
        Button viewById = mainActivity.findViewById(R.id.buttonCalculate);
        viewById.performClick();

        final Calculation calculation = database.allCalculations.get(0);
        assertEquals(calculation.getState(), State.IN_PROGRESS);
        assertEquals(calculation.getProgressPercent(), 0);
    }

    public void whenAddingTwoCalculation_shouldStoreThemInOrder()
    {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();
        CalculationsDB database = CalculationsApplication.getInstance().getDatabase();
        database.addNewCalculation(new Calculation(7));
        database.addNewCalculation(new Calculation(6));
        Button viewById = mainActivity.findViewById(R.id.buttonCalculate);
        viewById.performClick();

        assertEquals(database.allCalculations.get(0).getNumber(), 6);
        assertEquals(database.allCalculations.get(1).getNumber(), 7);
    }

    public void whenRemovingACalculation_ItIsDoneSuccessfully()
    {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();
        CalculationsDB database = CalculationsApplication.getInstance().getDatabase();
        Calculation calculation = new Calculation(1);
        database.addNewCalculation(calculation);
        database.deleteCalculation(calculation);

        assertEquals(database.allCalculations.size(), 0);
    }
}