import java.util.Comparator;

/**
 * Created by Connor Lewis on 3/23/2017.
 */
public class TimeComparator implements Comparator<TimeEntered> {
    @Override
    public int compare(TimeEntered x, TimeEntered y){
        int xstamp = x.getTime();
        int ystamp = y.getTime();

        if(xstamp > ystamp){
            return 1;
        }
        if(xstamp < ystamp){
            return -1;
        }
        return 0;
    }
}