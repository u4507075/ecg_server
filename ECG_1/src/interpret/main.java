/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpret;

import ecg.Variable;

/**
 *
 * @author bon
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        args = new String[1];
        args[0] = "2803589";
        //args[0] = "3279470";
        Variable var = new Variable();
        var.init();
        
        GetECG g = new GetECG();
        g.run();
    }
}
