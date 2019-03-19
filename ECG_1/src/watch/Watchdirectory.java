/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package watch;

import ecg.ECGRecorder;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bon
 */
public class Watchdirectory extends Thread{
    ECGRecorder main;
    Path path;
    boolean recursive;
    public Watchdirectory(ECGRecorder main, String path, boolean recursive)
    {
        this.main = main;
        this.path = Paths.get(path);
        this.recursive = recursive;
    }
    @Override
    public void run()
    {
        try {
            Watch watch = new Watch(main, path, recursive);
            watch.processEvents();
        } catch (IOException ex) {
            Logger.getLogger(Watchdirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
