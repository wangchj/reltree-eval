import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Implementation of the operations defined in the publication.
 */
public class Operations {
    /**
     * @param tableName name of the table to run query.
     * @param nodeId the ID of the starting node. This node must be in the table specified by tableName.
     * @return ID of the root node; -1 if failure.
     */
    public static int rootAL(String tableName, int nodeId) throws IOException, SQLException
    {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();

        //Check nodeId is valid
        ResultSet r = s.executeQuery("select * from " + tableName + " where id=" + nodeId);
        if(!r.next()) return -1;

        int parent = r.getInt("parent");
        int id = r.getInt("id");
        while(parent != 0)
        {
            r = s.executeQuery("select * from " + tableName + " where id=" + parent);
            r.next();
            parent = r.getInt("parent");
            id = r.getInt("id");
        }

        return id;
    }

    /**
     * Root operation via nested sets model.
     * 
     * @param tableName name of the table to run query.
     * @param nodeId the ID of the starting node. This node must be in the table specified by tableName.
     * @return ID of the root node; -1 if failure.
     */
    public static int rootNS(String tableName, int nodeId) throws IOException, SQLException
    {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();

        //Check nodeId is valid
        ResultSet rset = s.executeQuery("select * from " + tableName + " where id=" + nodeId);
        if(!rset.next()) return -1;

        int l = rset.getInt("l");
        int r = rset.getInt("r");
        String query = String.format("select * from %1s where l<= %2d and r >= %3d and parent is NULL", tableName, l, r);
        rset = s.executeQuery(query);
        rset.next();
        return rset.getInt("id");
    }
    
    /**
     * Siblings operation via adjacency list relational model. This method returns id of
     * nodes that are siblings of the node identified by nodeId.
     * @param tableName The name of the table that contains the nodes.
     * @param nodeId The nodeId.
     */
    public static ArrayList<Integer> siblings(String tableName, int nodeId) throws IOException, SQLException
    {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        String query = String.format("select * from %1$s where parent=(select parent from %1$s where id = %2$d) and id != %2$d", tableName, nodeId);
        ResultSet rs = s.executeQuery(query);
        ArrayList<Integer> result = new ArrayList<Integer>();
        while(rs.next())
        {
            result.add(rs.getInt("id"));
        }
        return result;
    }
    
    /**
     * Children operation using adjacency list model.
     * @param tableName The name of the table that contains the nodes.
     * @param nodeId The ID of the node for which to find the children.
     * @return The ID of the children, or empty list if the node is a leaf node.
     */
    public static ArrayList<Integer> children(String tableName, int nodeId) throws Exception
    {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        String query = String.format("select * from %1$s where parent=%2$d", tableName, nodeId);
        ResultSet rs = s.executeQuery(query);
        ArrayList<Integer> result = new ArrayList<Integer>();
        while(rs.next())
            result.add(rs.getInt("id"));
        return result;
    }
    
    /**
     * Leaves operation for the adjacency list model.
     * @param tableName The name of the table that contains the nodes.
     * @param nodeId ID of the node under which the leaves reside.
     */
    public static ArrayList<Integer> leavesAL(String tableName, int nodeId) throws Exception
    {
        ArrayList<Integer> result = new ArrayList<Integer>();
        leavesAL(tableName, nodeId, result);
        return result;
    }
    
    /**
     * Recursive algorithm of leaves operation for the adjacency list model.
     * @param tableName The name of the table that contains the nodes.
     * @param nodeId ID of the node under which the leaves reside.
     */
    private static void leavesAL(String tableName, int nodeId, ArrayList<Integer> result) throws Exception
    {
        ArrayList<Integer> children = children(tableName, nodeId);
        if(children.size() == 0)
        {
            result.add(nodeId);
            return;
        }
        for(int child : children)
            leavesAL(tableName, child, result);
    }
    
    /**
     * Leaves operation for the nested sets model.
     * @param tableName The name of the table that contains the nodes.
     * @param nodeId ID of the node under which the leaves reside.
     * @return The ID of the leaf nodes under the node of nodeId.
     */
    public static ArrayList<Integer> leavesNS(String tableName, int nodeId) throws Exception
    {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        String query = String.format(
            "select * from %1$s n inner join " +
            "(select l, r from %1$s where id=%2$d) lr " +
            "on (n.l >= lr.l and n.r <= lr.r) " +
            "where not exists (select id from %1$s where parent=n.id);",
            tableName, nodeId);
        ResultSet rs = s.executeQuery(query);
        ArrayList<Integer> result = new ArrayList<Integer>();
        while(rs.next())
            result.add(rs.getInt("id"));
        return result;
    }
    
    /**
     * Height operation for the adjacency list model.
     * @param table The name of the table that contains the nodes.
     * @param nodeId ID of the node to find the height.
     * @return Height of subtree rooted at node of nodeId.
     */
    public static int heightAL(String table, int nodeId) throws Exception
    {
        ArrayList<Integer> ch = children(table, nodeId);
        if(ch.isEmpty())
            return 1;
        int max = 0;
        for(int c : ch)
        {
            int height = heightAL(table, c);
            if(height > max)
                max = height;
        }
        return max + 1;
    }
    
    /**
     * Height operation of nested sets model.
     * @param table The name of the table that contains the nodes.
     * @param nodeId ID of the node to find the height.
     * @return Height of subtree rooted at node of nodeId.
     */
    public static int heightNS(String table, int nodeId) throws Exception
    {
        //Get leaf nodes
        ArrayList<Integer> leaves = leavesNS(table, nodeId);
        
        int max = 0;
        for(int leaf : leaves)
        {
            int depth = depthNS(table, leaf, nodeId);
            if(depth > max)
                max = depth;
        }
        return max;
    }
    
    /**
     * Depth operation for adjacency list model.
     * @param table the name of the table that contains the nodes.
     * @param nodeId the id of the node to find depth.
     * @return the depth of the node of nodeId.
     */
    public static int depthAL(String table, int nodeId) throws Exception
    {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();

        //Check nodeId is valid
        ResultSet r = s.executeQuery("select * from " + table + " where id=" + nodeId);
        if(!r.next()) throw new Exception("nodeId is invalid");

        int depth = 1;
        int parent = r.getInt("parent");

        while(parent != 0)
        {
            depth++;
            r = s.executeQuery("select * from " + table + " where id=" + parent);
            r.next();
            parent = r.getInt("parent");
        }

        return depth;
    }
    
    /**
     * Depth operation for nested sets model.
     * @param table the name of the table that contains the nodes.
     * @param nodeId the id of the node to find depth.
     * @return the depth of the node of nodeId.
     */
     public static int depthNS(String table, int nodeId) throws Exception
     {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        //Get the left and right values
        int[] lr = getLR(table, nodeId);
        //Get the count of nodes on path
        String query = String.format("select count(*) from %1$s where l <= %2$d and r >= %3$d", table, lr[0], lr[1]);
        ResultSet r = s.executeQuery(query);
        if(!r.next())
            throw new Exception("Query returned no result.");
        return r.getInt(1);
     }
     
     /**
     * Depth operation for nested sets model.
     * @param table The name of the table that contains the nodes.
     * @param nodeId The id of the node to find depth.
     * @param rootId Node ID of the root. This does not have to be absolute root of the tree.
     * @return The depth of the node of nodeId.
     */
     public static int depthNS(String table, int nodeId, int rootId) throws Exception
     {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        //Get the left and right values
        int[] lr = getLR(table, nodeId);
        int[] rootlr = getLR(table, rootId);
        
        //Get the count of nodes on path
        String query = String.format("select count(*) from %1$s where l <= %2$d and r >= %3$d and l >= %4$d and r <= %5$d", table, lr[0], lr[1], rootlr[0], rootlr[1]);
        ResultSet r = s.executeQuery(query);
        if(!r.next())
            throw new Exception("Query returned no result.");
        return r.getInt(1);
     }
     
     /**
      * Gets left and right values of the node identified by nodeId.
      * @param table the name of the table that contains the nodes.
      * @param nodeId the id of the node to find left and right values.
      * @return An array a, where a[0] is the left and a[1] is the right. If nodeId does not exist, null is returned.
      */
     public static int[] getLR(String table, int nodeId) throws Exception
     {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        String query = String.format("select l, r from %1$s where id=%2$d", table, nodeId);
        ResultSet r = s.executeQuery(query);
        if(!r.next())
            return null;
        int[] res = new int[2];
        res[0] = r.getInt("l");
        res[1] = r.getInt("r");
        return res;
     }
     
     /**
     * Path operation for adjacency list model.
     * @param table the name of the table that contains the nodes.
     * @param nodeId the id of the node to find the path.
     * @return Path to node or null if nodeId is invalid.
     */
     public static ArrayList<Integer> pathAL(String table, int nodeId) throws Exception
     {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();

        //Check nodeId is valid
        ResultSet r = s.executeQuery("select * from " + table + " where id=" + nodeId);
        if(!r.next())
            return null;

        ArrayList<Integer> res = new ArrayList<Integer>();
        int parent = r.getInt("parent");
        res.add(r.getInt("id"));
        
        while(parent != 0)
        {
            r = s.executeQuery("select * from " + table + " where id=" + parent);
            r.next();
            res.add(r.getInt("id"));
            parent = r.getInt("parent");
        }

        java.util.Collections.reverse(res);
        return res;
     }
     
     /**
     * Path operation for nested sets model.
     * @param table the name of the table that contains the nodes.
     * @param nodeId the id of the node to find the path.
     * @return Path to node or null if nodeId is invalid.
     */
     public static ArrayList<Integer> pathNS(String table, int nodeId) throws Exception
     {
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        ArrayList<Integer> res = new ArrayList<Integer>();
        
        //Get the left and right values
        int[] lr = getLR(table, nodeId);
        
        //Get the count of nodes on path
        String query = String.format("select * from %1$s where l <= %2$d and r >= %3$d", table, lr[0], lr[1]);
        ResultSet r = s.executeQuery(query);
        
        while(r.next())
            res.add(r.getInt("id"));
            
        if(res.isEmpty())
            return null;
            
        return res;
    }
    
    /**
     * Member operation for adjacency list model.
     * @param table the name of the table that contains the nodes.
     * @param dnode id of the descendent node.
     * @param anode id of the acescendent node.
     * @return true if denode is a descendent of anode; false otherwise.
     */
    public static boolean memberAL(String table, int dnode, int anode) throws Exception
    {
        if(dnode == anode)
            return true;
            
        Connection con = Database.getConnection();
        Statement s = con.createStatement();

        ResultSet r = s.executeQuery("select * from " + table + " where id=" + dnode);
        if(!r.next())
            return false;

        int parent = r.getInt("parent");
        
        while(parent != 0)
        {
            if(parent == anode)
                return true;
                
            r = s.executeQuery("select * from " + table + " where id=" + parent);
            r.next();
            parent = r.getInt("parent");
        }

        return false;
    }
    
    /**
     * Member operation for adjacency list model.
     * @param table the name of the table that contains the nodes.
     * @param dnode id of the descendent node.
     * @param anode id of the acescendent node.
     * @return true if denode is a descendent of anode; false otherwise.
     */
    public static boolean memberNS(String table, int dnode, int anode) throws Exception
    {
        if(dnode == anode)
            return true;
        
        int dl, dr, al, ar;
        
        Connection con = Database.getConnection();
        Statement s = con.createStatement();
        ResultSet r;
        
        r = s.executeQuery("select * from " + table + " where id=" + dnode);
        if(!r.next())
            return false;
        
        dl = r.getInt("l");
        dr = r.getInt("r");
        
        r = s.executeQuery("select * from " + table + " where id=" + anode);
        if(!r.next())
            return false;
        
        al = r.getInt("l");
        ar = r.getInt("r");
        
        return al < dl && ar > dr;
    }
    
}
