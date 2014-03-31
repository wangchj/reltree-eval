import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Experiment
{
    public static void main(String[] args) throws Exception
    {
        ArrayList<JobSet> jobs = new ArrayList<JobSet>();
        jobs.add(new JobSet(Operation.Height, Algorithm.AL, 8, 8, 5, null, 3));
        jobs.add(new JobSet(Operation.Height, Algorithm.AL, 9, null, null, null, 3));
        jobs.add(new JobSet(Operation.Height, Algorithm.AL, 10, null, null, null, 3));
        jobs.add(new JobSet(Operation.Height, Algorithm.NS, null, null, null, null, 5));
        jobs.add(new JobSet(Operation.Height, Algorithm.SR, null, null, null, null, 5));
        runJobs(jobs);
        
        Operation[] o = {/*Operation.Height,*/ Operation.Depth, Operation.Path, Operation.Member};
        autoScan(o, null);
    }
    
    /**
     * Run a set of specific experiments in jobsets.
     * @param jobsets a set of specific jobs.
     */
    static void runJobs(List<JobSet> jobsets)
    {
        for(JobSet jobset : jobsets)
        {
            //A set of average values
            //ArrayList<ArrayList<Double>> table = new ArrayList<ArrayList<Double>>();
            //averages.put(op.toString() + al.toString(), table);

            for(int order : jobset.orders)
            {
                for(int depth : jobset.depths)
                {
                    System.out.printf("%s\t%s\tOrder: %d\tDepth: %d\n", jobset.op, jobset.al, order, depth);
                    
                    try
                    {
                        double[] r = time(jobset.op, jobset.al, null, order, depth, jobset.runs);
                        
                        //Print out run time for each trial
                        for(int i = 0; i < r.length; i++)
                        {
                            System.out.print(r[i]);
                            if(i != r.length - 1)
                                System.out.print(' ');
                        }
                            
                        System.out.println();
                        
                        //Print out average time of trials
                        System.out.println("Average: " + average(r));
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex);
                    }
                    finally
                    {
                        //Call garbage collection
                        System.gc();
                        //Sleep for 10 secs
                        //Thread.sleep(10000);
                    }
                        
                    System.out.println();
                }
            } 
        }
    }
    
    /**
     * Runs time() for the operations and algorithms specified in ops, and als.
     *
     * @param ops A list of operations to run. If null, all opeartions will be run.
     * @param als A list of algorithms that implement the operations to run. If null, all algorithms will be run for each operation.
     */
    static void autoScan(Operation[] ops, Algorithm[] als)
    {
        //The number of trails
        int defaultTrialCount = 5;

        //If ops or als is null, run everything
        if(ops == null) ops = Operation.values();
        if(als == null) als = Algorithm.values();
        
        //If an experiment needs different number of trials than the default, it can be specified here.
        //The key is the name of the operation plus algorithm. For example: "RootAL".
        HashMap<String, Integer> trialCount = new HashMap<String, Integer>();
        trialCount.put("LeavesAL", 1);
        
        //A Hashmap of 2D arrays used as tables to hold average of running time.
        HashMap<String, ArrayList<ArrayList<Double>>> averages = new HashMap<String, ArrayList<ArrayList<Double>>>();

        for(Operation op : ops)
        {
            for(Algorithm al : als)
            {
                //A set of average values
                ArrayList<ArrayList<Double>> table = new ArrayList<ArrayList<Double>>();
                averages.put(op.toString() + al.toString(), table);

                for(int order = 2; order <= 10; order++)
                {
                    //A row -> same order, increasing depth
                    ArrayList<Double> row = new ArrayList<Double>();
                    table.add(row);

                    for(int depth = 2; depth <=10; depth++)
                    {
                        System.out.printf("%s\t%s\tOrder: %d\tDepth: %d\n", op, al, order, depth);
                        int run = trialCount.containsKey(op.toString() + al.toString()) ? trialCount.get(op.toString() + al.toString()) : defaultTrialCount;
                        try
                        {
                            double[] r = time(op, al, null, order, depth, run);
                            
                            //Print out run time for each trial
                            for(int i = 0; i < r.length; i++)
                            {
                                System.out.print(r[i]);
                                if(i != r.length - 1)
                                    System.out.print(' ');
                            }
                            
                            System.out.println();
                            
                            //Print out average time of trials
                            double avg = average(r);
                            row.add(avg);
                            System.out.println("Average: " + average(r));
                        }
                        catch(Exception ex)
                        {
                            row.add(0.0);
                            System.out.println(ex);
                        }
                        finally
                        {
                            //Call garbage collection
                            System.gc();
                            //Sleep for 10 secs
                            //Thread.sleep(10000);
                        }
                        
                        System.out.println();
                    }
                } 
            }
        }

        //At the end, print out the result tables
        for(String key : averages.keySet())
        {
            System.out.println("Table for " + key);
            ArrayList<ArrayList<Double>> table = averages.get(key);
            for(ArrayList<Double> row : table)
            {
                for(double d : row)
                {
                    System.out.print(d);
                    System.out.print('\t');
                }
                System.out.println();
            }
            System.out.println();
        }
    }
    
    /**
     * Measures the running time of an operation.
     * @param op     the operation to run.
     * @param algo   the algorithm for the operation.
     *               and 1 means to use nested sets model.
     * @param params paramters that will be passed into operations.
     * @param fan    the out-degree (order) of the tree.
     * @param depth  the height of the tree.
     * @param runs   how many trials to run.
     * @return       Running time in milliseconds for each trial.
     */
    public static double[] time(Operation op, Algorithm algo, HashMap<String, Object> params,
        int fan, int depth, int runs) throws Exception
    {
        double[] res = new double[runs];
        Timer  timer = Timer.getTimer();
        String table = makeTableName(null, fan, depth);
        int    minId = getMinId(table);
        int    maxId = getMaxId(table);
        double time  = 0;
        //The first run always runs longer than other runs.
        //if(alg == 0) Operations.rootAL(table, node);
        //else         Operations.rootNS(table, node);
        
        for(int i = -1; i < runs; i++)
        {
            if(op == Operation.Root && algo == Algorithm.AL)
            {
                timer.reset();
                Operations.rootAL(table, maxId);
                time = timer.timeMs();
            }
            if(op == Operation.Root && algo == Algorithm.NS)
            {
                timer.reset();
                Operations.rootNS(table, maxId);
                time = timer.timeMs();
            }
            if(op == Operation.Root && algo == Algorithm.SR)
            {
                timer.reset();
                Operations.rootSR(table, maxId);
                time = timer.timeMs();
            }
            
            //Leaves
            if(op == Operation.Leaves && algo == Algorithm.AL)
            {
                timer.reset();
                Operations.leavesAL(table, minId);
                time = timer.timeMs();
            }
            if(op == Operation.Leaves && algo == Algorithm.NS)
            {
                timer.reset();
                Operations.leavesNS(table, minId);
                time = timer.timeMs();
            }
            if(op == Operation.Leaves && algo == Algorithm.SR)
            {
                timer.reset();
                Operations.leavesSR(table, minId);
                time = timer.timeMs();
            }
            
            //Height
            if(op == Operation.Height && algo == Algorithm.AL)
            {
                timer.reset();
                Operations.heightAL(table, minId);
                time = timer.timeMs();
            }
            if(op == Operation.Height && algo == Algorithm.NS)
            {
                timer.reset();
                Operations.heightNS(table, minId);
                time = timer.timeMs();
            }
            if(op == Operation.Height && algo == Algorithm.SR)
            {
                timer.reset();
                Operations.heightSR(table, minId);
                time = timer.timeMs();
            }
            
            //Depth
            if(op == Operation.Depth && algo == Algorithm.AL)
            {
                timer.reset();
                Operations.depthAL(table, maxId);
                time = timer.timeMs();
            }
            if(op == Operation.Depth && algo == Algorithm.NS)
            {
                timer.reset();
                Operations.depthNS(table, maxId);
                time = timer.timeMs();
            }
            
            //Path
            if(op == Operation.Path && algo == Algorithm.AL)
            {
                timer.reset();
                Operations.pathAL(table, maxId);
                time = timer.timeMs();
            }
            if(op == Operation.Path && algo == Algorithm.NS)
            {
                timer.reset();
                Operations.pathNS(table, maxId);
                time = timer.timeMs();
            }
            
            if(i != -1)
                res[i] = time;
            
            //Run garbage collector
            System.gc();
        }
        return res;
    }
    
    /**
     * Make database table name based on tree fanout and depth.
     * @param prefix A prefix to prepend to the name. This usually specify the kind of table.
     * @param fan The fan-out of the tree.
     * @param depth The depth of the tree.
     */
    public static String makeTableName(String prefix, Integer fan, Integer depth)
    {
        if(prefix == null) prefix = "nf_";
        return prefix + "f" + fan + "_d" + depth;
    }
    
    /**
     * Returns the min record id from table.
     * @param table name of database table.
     * @return database record id.
     */
    public static int getMinId(String table) throws Exception
    {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        ResultSet r = s.executeQuery("select min(id) from " + table);
        r.next();
        return r.getInt(1);
    }
    
    /**
     * Returns the max record id from table.
     * @param table name of database table.
     * @return database record id.
     */
    public static int getMaxId(String table) throws Exception
    {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        ResultSet r = s.executeQuery("select max(id) from " + table);
        r.next();
        return r.getInt(1);
    }
    
    /**
     * Calculates the average of a.
     * @param a the array to calculate the average of.
     * @return the average in double.
     */
    static double average(double[] a)
    {
        if(a == null || a.length == 0)
            return 0;
        double sum = 0;
        for(double d : a)
            sum += d;
        return sum / a.length;
    }
    
    /**
     * A specific set of experiments to run.
     */
    static class JobSet
    {
        Operation op; //The operation
        Algorithm al; //The implementation to use
        int[] orders; //A set of orders. Ex: {3, 5, 8}
        int[] depths; //A set of depths.
        int runs;     //How many trials to run for this experiment.
        
        /**
         * Constructor
         */
        public JobSet(Operation op, Algorithm al, int[] orders, int[] depths, int runs)
        {
            this.op = op;
            this.al = al;
            this.orders = orders;
            this.depths = depths;
            this.runs = runs;
        }
        
        /**
         * Constructor for consecutive orders, depths.
         */
        public JobSet(Operation op, Algorithm al, Integer minOrder, Integer maxOrder, Integer minDepth, Integer maxDepth, int runs)
        {   
            this(op, al, null, null, runs);
            
            if(minOrder == null) minOrder = 2;
            if(maxOrder == null) maxOrder = 10;
            if(minDepth == null) minDepth = 2;
            if(maxDepth == null) maxDepth = 10;
            
            orders = new int[maxOrder + 1 - minOrder];
            depths = new int[maxDepth + 1 - minDepth];
            
            for(int i = minOrder; i <= maxOrder; i++)
                orders[i - minOrder] = i;
            for(int i = minDepth; i <= maxDepth; i++)
                depths[i - minDepth] = i;
        }
    }
}
