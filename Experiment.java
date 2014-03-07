import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.ArrayList;

public class Experiment
{
    public static void main(String[] args) throws Exception
    {
        /*Operation op    = Operation.Root;
        Algorithm algo  = Algorithm.AL;
        int       fan   = 6;
        int       depth = 8;
        int       runs  = 5;
           
        double[] r = time(op, algo, null, fan, depth, runs);
        for(int i = 0; i < r.length; i++)
            System.out.println(r[i]);
         
        System.out.println();

        algo = Algorithm.NS;
        r = time(op, algo, null, fan, depth, runs);
        for(int i = 0; i < r.length; i++)
            System.out.println(r[i]);
            
         
        System.out.println();

        op   = Operation.Leaves;
        algo = Algorithm.AL;
        //r = time(op, algo, null, fan, depth, runs);
        //for(int i = 0; i < r.length; i++)
        //    System.out.println(r[i]);
        
        System.out.println();

        //op   = Operation.Leaves;
        algo = Algorithm.NS;
        r = time(op, algo, null, fan, depth, runs);
        for(int i = 0; i < r.length; i++)
            System.out.println(r[i]);*/
        
        autoScan();
        
//         double[] r = time(Operation.Leaves, Algorithm.AL, null, 4, 9, 1);
//         for(double d : r)
//             System.out.println(d);
    }
    
    static void autoScan()
    {
        //The number of trails
        int defaultTrialCount = 5;

        //If an experiment needs different number of trials than the default, it can be specified here.
        //The key is the name of the operation plus algorithm. For example: "RootAL".
        HashMap<String, Integer> trialCount = new HashMap<String, Integer>();
        trialCount.put("LeavesAL", 1);
        
        //A Hashmap of 2D arrays used as tables to hold average of running time.
        HashMap<String, ArrayList<ArrayList<Double>>> averages = new HashMap<String, ArrayList<ArrayList<Double>>>();

        for(Operation op : Operation.values())
        {
            for(Algorithm al : Algorithm.values())
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
    
    static double average(double[] a)
    {
        if(a == null || a.length == 0)
            return 0;
        double sum = 0;
        for(double d : a)
            sum += d;
        return sum / a.length;
    }
}
