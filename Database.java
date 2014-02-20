import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Database
{
    private static Connection con;
    
    public static Connection connect() throws IOException, SQLException
    {
        return connect("config.txt");
    }
    
    public static Connection connect(String cfgFile) throws IOException, SQLException
    {
        if(cfgFile == null || cfgFile.length() == 0)
            throw new IllegalArgumentException();
            
        //Read config from file
        Properties p = new Properties();
        p.load(new FileReader(cfgFile));
        
        //Create database connection
        String url = "jdbc:mysql://" + p.getProperty("db.host") + "/" + p.getProperty("db.dbname");
        Connection con = DriverManager.getConnection(
            url,
            p.getProperty("db.user"),
            p.getProperty("db.pwd")
        );
        return con;
    }
    
    /**
     * Get a connection to the database. If there already exist an open connection,
     * it will be returned; else, connect() will be called to create a new connection,
     * and returned next time this method is called.
     */
    public static Connection getConnection() throws IOException, SQLException
    {
        if(con == null)
            con = connect();
        return con;
    }
}