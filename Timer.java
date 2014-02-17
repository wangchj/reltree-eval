/**
 * Author: Chih-Jye Wang
 * Date  : Feb 15, 2014
 *
 * Time class is used to measure elapsed time. This class uses System.nanoTime().
 *
 * The singleton instance of this class is not thread-safe.
 */
public class Timer
{
    public long startTime;
    public long endTime;
    
    private static Timer singleton;
    
    /**
     * Constructs an instance and set start time.
     */   
    public Timer()
    {
        startTime = System.nanoTime();
    }
    
    /**
     * Returns the singleton instance.
     * This method returns the Timer object if already exsit, else a new instace.
     * If the object already exists, calling this method does not reset the startTime of the singleton instance.
     */
    public static Timer getTimer()
    {
        if(singleton == null)
            singleton = new Timer();
        return singleton;
    }
    
    /**
     * Returns elapsed time in nanoseconds, but the precision depends on the OS.
     */
    public long time()
    {
        endTime = System.nanoTime();
        return endTime - startTime;
    }
    
    /**
     * Returns elapsed time in seconds.
     */
    public double timeSec()
    {
        long time = time();
        return time / 1000000000d;
    }
    
    /**
     * Resets start time.
     */
    public void reset()
    {
        startTime = System.nanoTime();
    }
}