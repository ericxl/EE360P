import java.util.Comparator;

/**
 * Created by Connor Lewis on 3/23/2017.
 */
public class TimeStamp {
    int logicalClock;
    int pid;
    public TimeStamp(int logicalClock, int pid){
        this.logicalClock = logicalClock;
        this.pid = pid;
    }

    public static int compare(TimeStamp x, TimeStamp y){
        TimeComparator compare = new TimeComparator();
        compare(x, y);
    }

    public int getLogicalClock(){
        return logicalClock;
    }
    public int getPid(){
        return pid;
    }
}

