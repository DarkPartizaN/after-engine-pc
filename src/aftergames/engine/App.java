package aftergames.engine;

import aftergames.engine.utils.ResourceUtils;
import aftergames.engine.utils.StringUtils;

import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author KiQDominaN
 */
public class App {

    public Screen screen;

    public App() {
        setLibraryPath();
    }

    private void setLibraryPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osBit = System.getProperty("sun.arch.data.model");

        if (osBit.equals("32")) osBit = "86";//HACKHACK!!!

        if (osName.contains("win"))
            osName = "windows";
        else if (osName.contains("linux"))
            osName = "linux";
        else if (osName.contains("mac") || osName.contains("darwin"))
            osName = "mac";
        else {
            JOptionPane.showMessageDialog(new JFrame("Error!"), StringUtils.concat("Sorry, your operating system \"", osName, "\" is unsupported!"));
            System.exit(0);
        }

        //Find library files
        File libPath = new File(ResourceUtils.dir_root.concat("/lib/native/").concat(osName).concat("/x").concat(osBit));

        //TODO: engine error handling
        if (!libPath.exists()) {
            JOptionPane.showMessageDialog(new JFrame("Error!"), StringUtils.concat("Could not find library folder: ", libPath.getAbsolutePath(), File.separator));
            System.exit(0);
        }

        if (libPath.list().length == 0) {
            JOptionPane.showMessageDialog(new JFrame("Error!"), StringUtils.concat("Library folder: ", libPath.getAbsolutePath(), File.separator, " is empty!"));
            System.exit(0);
        }

        if (!new File(libPath, "lwjgl.dll").exists()) {
            JOptionPane.showMessageDialog(new JFrame("Error!"), StringUtils.concat("Could not find a required library: ", libPath.getAbsolutePath(), File.separator, "lwjgl.dll"));
            System.exit(0);
        }

        //Set enviroiment for LWJGL native libraries
        System.setProperty("org.lwjgl.librarypath", libPath.getAbsolutePath());

        //Debug output
        System.out.println("Platform: ".concat(osName).concat(osBit));
        System.out.println("Native library path: ".concat(System.getProperty("org.lwjgl.librarypath")));
    }
}
