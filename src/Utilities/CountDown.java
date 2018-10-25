import java.util.Timer;
import java.util.TimerTask;

public class CountDown implements Runnable
{
    //initiate variables
    int secondsPassed = 0;
    boolean isOn = true;
    Timer timer = new Timer();

    TimerTask timerTask = new TimerTask()
    {
        @Override
        public void run()
        {
            //increase time
            secondsPassed++;

            //stop the timer and timerTask
            if (!isOn)
            {
                timerTask.cancel();
                timer.cancel();
            }
            //for test
            //System.out.println("Seconds passed: " + secondsPassed);
        }
    };

    public void setSecondsPassed(int secondsPassed)
    {
        this.secondsPassed = secondsPassed;
    }
    public void setOn(boolean on) {
        isOn = on;
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
