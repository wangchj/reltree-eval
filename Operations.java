import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementation of the operations defined in the publication.
 */
public class Operations {
    /**
     * @param tableName name of the table to run query.
     * @param nodeId the ID of the starting node. This node must be in the table specified by tableName.
     * @return ID of the root node; -1 if failure.
     */
    public static int GetRootAL(String tableName, int nodeId) throws IOException, SQLException
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
    public static int GetRootNS(String tableName, int nodeId) throws IOException, SQLException
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
}
