/**
 * Created by Connor Lewis on 3/23/2017.
 */
public class TimeEntered {
    int time;
    int pid;
    public TimeEntered(int logicalClock, int pid) {
        this.time = logicalClock;
        this.pid = pid;
    }
    public static int compare(TimeEntered a, TimeEntered b) {

        if (a.time > b.time)
            return 1;
        if (a.time <  b.time)
            return -1;
        if (a.pid > b.pid) return 1;
        if (a.pid < b.pid)
            return -1;

        return 0;
    }
    public int getTime() {
        return time;
    }
    public int getPid() {
        return pid;
    }
}
