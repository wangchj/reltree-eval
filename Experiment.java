import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;

public class Experiment
{
    public static void main(String[] args) throws Exception
    {
        Operation op    = Operation.Root;
        Algo      algo  = Algo.AL;
        int       fan   = 6;
        int       depth = 8;
        int       runs  = 5;
           
        double[] r = time(op, algo, null, fan, depth, runs);
        for(int i = 0; i < r.length; i++)
            System.out.println(r[i]);
         
        System.out.println();

        algo = Algo.NS;
        r = time(op, algo, null, fan, depth, runs);
        for(int i = 0; i < r.length; i++)
            System.out.println(r[i]);
            
         
        System.out.println();

        op   = Operation.Leaves;
        algo = Algo.AL;
        //r = time(op, algo, null, fan, depth, runs);
        //for(int i = 0; i < r.length; i++)
        //    System.out.println(r[i]);
        
        System.out.println();

        //op   = Operation.Leaves;
        algo = Algo.NS;
        r = time(op, algo, null, fan, depth, runs);
        for(int i = 0; i < r.length; i++)
            System.out.println(r[i]);
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
    public static double[] time(Operation op, Algo algo, HashMap<String, Object> params,
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
            if(op == Operation.Root && algo == Algo.AL)
            {
                timer.reset();
                Operations.rootAL(table, maxId);
                time = timer.timeMs();
            }
            if(op == Operation.Root && algo == Algo.NS)
            {
                timer.reset();
                Operations.rootNS(table, maxId);
                time = timer.timeMs();
            }
            
            //Leaves
            if(op == Operation.Leaves && algo == Algo.AL)
            {
                timer.reset();
                Operations.leavesAL(table, minId);
                time = timer.timeMs();
            }
            if(op == Operation.Leaves && algo == Algo.NS)
            {
                timer.reset();
                Operations.leavesNS(table, minId);
                time = timer.timeMs();
            }
            
            //Height
            if(op == Operation.Height && algo == Algo.AL)
            {
                timer.reset();
                Operations.heightAL(table, minId);
                time = timer.timeMs();
            }
            if(op == Operation.Height && algo == Algo.NS)
            {
                timer.reset();
                Operations.heightNS(table, minId);
                time = timer.timeMs();
            }
            
            //Depth
            if(op == Operation.Depth && algo == Algo.AL)
            {
                timer.reset();
                Operations.depthAL(table, maxId);
                time = timer.timeMs();
            }
            if(op == Operation.Depth && algo == Algo.NS)
            {
                timer.reset();
                Operations.depthNS(table, maxId);
                time = timer.timeMs();
            }
            
            //Path
            if(op == Operation.Path && algo == Algo.AL)
            {
                timer.reset();
                Operations.pathAL(table, maxId);
                time = timer.timeMs();
            }
            if(op == Operation.Path && algo == Algo.NS)
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
}