import java.io.IOException;
import java.sql.*;

public class DataAccessTest
{
    public static void main(String[] args)throws InterruptedException
    {
        Timer t = new Timer();
        for(int i = 0; i < 10; i++)
        {
            t.reset();
            Thread.sleep(2000);
            System.out.println(t.timeSec() + "    " + t.startTime + "    " + t.endTime);
        }
    }
    
    public static void connectionTest() throws IOException, SQLException
    {
        Connection con = Database.connect();
      
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery("select * from nf_f2_d2");
        while (rs.next())
        {
            int id = rs.getInt("id");
            int parent = rs.getInt("parent");
            System.out.println(id + " " + parent);
         
//          String coffeeName = rs.getString("COF_NAME");
//          int supplierID = rs.getInt("SUP_ID");
//          float price = rs.getFloat("PRICE");
//          int sales = rs.getInt("SALES");
//          int total = rs.getInt("TOTAL");
//          System.out.println(coffeeName + "\t" + supplierID +
//                                "\t" + price + "\t" + sales +
//                                "\t" + total);
        }
    }
    
    
}