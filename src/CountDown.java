import java.util.Timer;
import java.util.TimerTask;

public class CountDown implements Runnable
{

    int secondsPassed = 0;

    Timer timer = new Timer();

    TimerTask timerTask = new TimerTask()
    {
        @Override
        public void run()
        {
            secondsPassed++;

            System.out.println("Seconds passed: " + secondsPassed);

        }
    };

    public void setSecondsPassed(int secondsPassed)
    {
        this.secondsPassed = secondsPassed;
    }

    public int getSecondsPassed()
    {
        return secondsPassed;
    }

    @Override
    public void run()
    {
        start();
    }

    public void start()
    {
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }
}
