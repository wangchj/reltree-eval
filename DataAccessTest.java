import java.io.IOException;
import java.sql.*;

public class DataAccessTest
{
    public static void main(String[] args)throws IOException, SQLException
    {
        Timer t = Timer.getTimer();
        for(int i = 0; i < 10; i++)
        {
            t.reset();
            int id = Operations.GetRootAL("nf_f6_d8", 335923);
            System.out.println(t.timeSec() + "    " + id);
        }

        System.out.println();

        for(int i = 0; i < 10; i++)
        {
            t.reset();
            int id = Operations.GetRootNS("nf_f6_d8", 335923);
            System.out.println(t.timeSec() + "    " + id);
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