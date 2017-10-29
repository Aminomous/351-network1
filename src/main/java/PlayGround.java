import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PlayGround {
    public static void main(String[] args) {
        final int[] counter = {0};
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println(new Date().getTime());
            }
        };
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                System.out.println(counter[0]);
                counter[0]++;
                if (counter[0] == 4) {
                    this.cancel();
                }
            }
        };
//        while (true) {
            timer.scheduleAtFixedRate(task, 0, 5000);
            timer.schedule(task2, 0, 1000);
//        }
    }
}
