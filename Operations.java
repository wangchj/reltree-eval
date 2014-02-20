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
        Connection con = Database.connect();
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
        Connection con = Database.connect();
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
        Connection con = Database.connect();
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
        Connection con = Database.connect();
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
    
    public static int heightNS(String table, int nodeId) throws Exception
    {
        return 0;
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
}
