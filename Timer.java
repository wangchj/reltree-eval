/**
 * Author: Chih-Jye Wang
 * Date  : Feb 15, 2014
 *
 * Time class is used to measure elapsed time. This class uses System.nanoTime().
 */
public class Timer
{
    public long startTime;
    public long endTime;
 
    /**
     * Constructs an instance and set start time.
     */   
    public Timer()
    {
        startTime = System.nanoTime();
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