/*
 * Created on 13.11.2007
 *
 */
package ch.codez.spaceflight;

import java.io.File;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.x.LibXUtil;
import ch.codez.spaceflight.gui.MainFrame;

import com.sun.jna.NativeLibrary;

public class Main {

    /**
     * Log level, used only if the -Dvlcj.log= system property has not already
     * been set.
     */
    private static final String VLCJ_LOG_LEVEL = "INFO";

    /**
     * Change this to point to your own vlc installation, or comment out the
     * code if you want to use your system default installation.
     * <p>
     * This is a bit more explicit than using the -Djna.library.path= system
     * property.
     */
    private static final String NATIVE_LIBRARY_SEARCH_PATH = "vlc/lib/";

    /**
     * Set to true to dump out native JNA memory structures.
     */
    private static final String DUMP_NATIVE_MEMORY = "false";

    private static final Logger log = Logger.getLogger(Main.class);

    /**
     * Static initialisation.
     */
    static {

    }

    public static void main(String[] args) throws Exception {
        AppOptions opts = AppOptions.getInstance();
        assertDirectoryExistance(opts.getPathAstronauts());
        assertDirectoryExistance(opts.getPathMovies());
        if (new File(opts.getPathMovies()).listFiles().length == 0) {
            System.err.println("No movies found in '" + opts.getPathMovies() + "'");
            System.exit(1);
        }
        initVlc();
        setOSXOptions();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                frame.runFullScreen();
            }
        });
    }

    private static void initVlc() {
        if (null == System.getProperty("vlcj.log")) {
            System.setProperty("vlcj.log", VLCJ_LOG_LEVEL);
        }

        // Safely try to initialise LibX11 to reduce the opportunity for native
        // crashes - this will silently throw an Error on Windows (and maybe
        // MacOS)
        // that can safely be ignored
        LibXUtil.initialise();

        if (null != NATIVE_LIBRARY_SEARCH_PATH) {
            log.info("Explicitly adding JNA native library search path: '"
                    + NATIVE_LIBRARY_SEARCH_PATH + "'");
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
                    NATIVE_LIBRARY_SEARCH_PATH);
        }

        System.setProperty("jna.dump_memory", DUMP_NATIVE_MEMORY);
    }

    private static void setOSXOptions() {
        // fake full screen allows using webcam settings
        if (AppOptions.getInstance().getIsFakeFullscreen()) {
            System.setProperty("apple.awt.fakefullscreen", "true");
        }

        // System.setProperty("apple.awt.fullscreenusefade", "true");
        // System.setProperty("apple.laf.useScreenMenuBar", "true");
    }

    private static void assertDirectoryExistance(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                log.error("Could not create directory " + path);
            } else {
                log.info("Created directory " + path);
            }
        }
    }
}
