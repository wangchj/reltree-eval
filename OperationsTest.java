import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Author: Chih-Jye Wang
 * Date  : Feb 17, 2014
 *
 * JUnit Test class for Operations class.
 */
public class OperationsTest
{
    @Test public void rootAL() throws Exception
    {
        Assert.assertTrue(Operations.rootAL("nf_f6_d8", 335923) == 1);
    }
    
    @Test public void rootNS() throws Exception
    {
        Assert.assertTrue(Operations.rootNS("nf_f6_d8", 335923) == 1);
    }
    
    @Test public void children() throws Exception
    {
        ArrayList<Integer> r = Operations.children("nf_f2_d3", 1);
        Assert.assertTrue(r.size() == 2);
        Assert.assertTrue(r.get(0) == 2);
        Assert.assertTrue(r.get(1) == 5);
        
        r = Operations.children("nf_f2_d3", 2);
        Assert.assertTrue(r.size() == 2);
        Assert.assertTrue(r.get(0) == 3);
        Assert.assertTrue(r.get(1) == 4);
        
        r = Operations.children("nf_f2_d3", 3);
        Assert.assertTrue(r.size() == 0);
        
        r = Operations.children("nf_f3_d3", 2);
        Assert.assertTrue(r.size() == 3);
        //Assert.assertTrue(r.get(0) == 6);
        //Assert.assertTrue(r.get(1) == 10);
    }
    
    @Test public void siblingsRoot() throws Exception
    {
        ArrayList<Integer> r = Operations.siblings("nf_f3_d3", 1);
        Assert.assertTrue(r.size() == 0);
    }
    
    @Test public void siblings() throws Exception
    {
        ArrayList<Integer> r = Operations.siblings("nf_f3_d3", 2);
        Assert.assertTrue(r.size() == 2);
        Assert.assertTrue(r.get(0) == 6);
        Assert.assertTrue(r.get(1) == 10);
        
        r = Operations.siblings("nf_f4_d4", 2);
        Assert.assertTrue(r.size() == 3);
        //Assert.assertTrue(r.get(0) == 6);
        //Assert.assertTrue(r.get(1) == 10);
    }
    
    @Test public void leavesAL() throws Exception
    {
        ArrayList<Integer> r = Operations.leavesAL("nf_f2_d3", 1);
        Assert.assertTrue(r.size() == 4);
        Assert.assertTrue(r.get(0) == 3);
        Assert.assertTrue(r.get(1) == 4);
        Assert.assertTrue(r.get(2) == 6);
        Assert.assertTrue(r.get(3) == 7);
        
        r = Operations.leavesAL("nf_f2_d3", 2);
        Assert.assertTrue(r.size() == 2);
        Assert.assertTrue(r.get(0) == 3);
        Assert.assertTrue(r.get(1) == 4);
        
        r = Operations.leavesAL("nf_f2_d3", 3);
        Assert.assertTrue(r.size() == 1);
        Assert.assertTrue(r.get(0) == 3);
        
        r = Operations.leavesAL("nf_f3_d3", 1);
        Assert.assertTrue(r.size() == 9);
        Assert.assertTrue(r.get(0) == 3);
        Assert.assertTrue(r.get(1) == 4);
        Assert.assertTrue(r.get(2) == 5);
        Assert.assertTrue(r.get(3) == 7);
        Assert.assertTrue(r.get(4) == 8);
        Assert.assertTrue(r.get(5) == 9);
        Assert.assertTrue(r.get(6) == 11);
        Assert.assertTrue(r.get(7) == 12);
        Assert.assertTrue(r.get(8) == 13);
    }
    
    @Test public void leavesNS() throws Exception
    {
        ArrayList<Integer> r = Operations.leavesNS("nf_f2_d3", 1);
        Assert.assertTrue(r.size() == 4);
        Assert.assertTrue(r.get(0) == 3);
        Assert.assertTrue(r.get(1) == 4);
        Assert.assertTrue(r.get(2) == 6);
        Assert.assertTrue(r.get(3) == 7);
        
        r = Operations.leavesNS("nf_f2_d3", 2);
        Assert.assertTrue(r.size() == 2);
        Assert.assertTrue(r.get(0) == 3);
        Assert.assertTrue(r.get(1) == 4);
        
        r = Operations.leavesNS("nf_f2_d3", 3);
        Assert.assertTrue(r.size() == 1);
        Assert.assertTrue(r.get(0) == 3);
    }
    
    @Test public void heightAL() throws Exception
    {
        Assert.assertTrue(Operations.heightAL("nf_f2_d3", 1) == 3);
        Assert.assertTrue(Operations.heightAL("nf_f2_d3", 2) == 2);
        
        Assert.assertTrue(Operations.heightAL("nf_f3_d4", 1) == 4);
    }
    
    @Test public void heightNS() throws Exception
    {
        Assert.assertTrue(Operations.heightNS("nf_f2_d3", 1) == 3);
        Assert.assertTrue(Operations.heightNS("nf_f2_d3", 2) == 2);
        
        Assert.assertTrue(Operations.heightNS("nf_f3_d4", 1) == 4);
    }
    
    @Test public void depthAL() throws Exception
    {
        Assert.assertTrue(Operations.depthAL("nf_f2_d3", 1) == 1); //root node
        Assert.assertTrue(Operations.depthAL("nf_f2_d3", 2) == 2);
        Assert.assertTrue(Operations.depthAL("nf_f2_d3", 3) == 3);
        Assert.assertTrue(Operations.depthAL("nf_f2_d3", 4) == 3);
        
        //Assert.assertTrue(Operations.depthAL("nf_f3_d4", 1) == 4);
    }
    
    @Test public void depthNS() throws Exception
    {
        Assert.assertTrue(Operations.depthNS("nf_f2_d3", 1) == 1); //root node
        Assert.assertTrue(Operations.depthNS("nf_f2_d3", 2) == 2);
        Assert.assertTrue(Operations.depthNS("nf_f2_d3", 3) == 3);
        Assert.assertTrue(Operations.depthNS("nf_f2_d3", 4) == 3);
        
        Assert.assertTrue(Operations.depthNS("nf_f3_d3", 3) == 3);
        
        Assert.assertTrue(Operations.depthNS("nf_f4_d8", 21845) == 8);
    }
    
    @Test public void getLR() throws Exception
    {
        int[] r = Operations.getLR("nf_f2_d3", 1);
        Assert.assertTrue(r[0] == 0);
        Assert.assertTrue(r[1] == 13);
        
        r = Operations.getLR("nf_f2_d3", 2);
        Assert.assertTrue(r[0] == 1);
        Assert.assertTrue(r[1] == 6);
        
        r = Operations.getLR("nf_f3_d3", 13);
        Assert.assertTrue(r[0] == 22);
        Assert.assertTrue(r[1] == 23);
    }
    
    @Test public void path() throws Exception
    {
        ArrayList<Integer> path = Operations.pathAL("nf_f2_d3", 1);
        Assert.assertTrue(path.size() == 1);
        Assert.assertTrue(path.get(0) == 1);
        
        path = Operations.pathAL("nf_f2_d3", 3);
        Assert.assertTrue(path.size() == 3);
        Assert.assertTrue(path.get(0) == 1);
        Assert.assertTrue(path.get(1) == 2);
        Assert.assertTrue(path.get(2) == 3);
        
        path = Operations.pathAL("nf_f2_d3", 6);
        Assert.assertTrue(path.size() == 3);
        Assert.assertTrue(path.get(0) == 1);
        Assert.assertTrue(path.get(1) == 5);
        Assert.assertTrue(path.get(2) == 6);
        
        //NS Test cases
        
        path = Operations.pathNS("nf_f2_d3", 1);
        Assert.assertTrue(path.size() == 1);
        Assert.assertTrue(path.get(0) == 1);
        
        path = Operations.pathNS("nf_f2_d3", 3);
        Assert.assertTrue(path.size() == 3);
        Assert.assertTrue(path.get(0) == 1);
        Assert.assertTrue(path.get(1) == 2);
        Assert.assertTrue(path.get(2) == 3);
        
        path = Operations.pathNS("nf_f2_d3", 6);
        Assert.assertTrue(path.size() == 3);
        Assert.assertTrue(path.get(0) == 1);
        Assert.assertTrue(path.get(1) == 5);
        Assert.assertTrue(path.get(2) == 6);
    }
}
