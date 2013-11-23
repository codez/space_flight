/*
 * Created on 21.11.2007
 *
 */
package ch.codez.spaceflight;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

public class AppOptions {

    public final static String CONFIG_FILE = "spaceflight.properties";

    private static AppOptions INSTANCE = new AppOptions();

    private static Logger log = Logger.getLogger(AppOptions.class);

    private PropertiesConfiguration config;

    public static AppOptions getInstance() {
        return INSTANCE;
    }

    private AppOptions() {
        this.initConfig();
    }

    public boolean getIsKioskMode() {
        return this.config.getBoolean("kiosk.mode", true);
    }

    public boolean getIsFakeFullscreen() {
        return this.config.getBoolean("fake.fullscreen", true);
    }

    public String getPathAstronauts() {
        return this.getPath("path.astronauts");
    }

    public String getPathMovies() {
        return this.getPath("path.movies");
    }

    public long getFlightFrequency() {
        return this.config.getLong("flight.frequency", 5000);
    }

    public double getMovieRatio() {
        return this.config.getDouble("movieRatio", 1.25);
    }

    public int getFrameRate() {
        return this.config.getInt("flight.frameRate", 10);
    }

    public int getStraightFlightCount() {
        return this.config.getInt("flight.straight", 50);
    }

    public int getSpiralFlightCount() {
        return this.config.getInt("flight.spiral", 45);
    }

    public int getShearFlightCount() {
        return this.config.getInt("flight.shear", 5);
    }

    public int getStraightFlightSpeed() {
        return this.config.getInt("flight.straight.speed", 6);
    }

    public int getStraightFlightSpeedZ() {
        return this.config.getInt("flight.straight.speedZ", 30);
    }

    public int getSpiralFlightSpeed() {
        return this.config.getInt("flight.spiral.speed", 20);
    }

    public int getSpiralFlightRotation() {
        return this.config.getInt("flight.spiral.rotation", 6);
    }

    public void save() {
        try {
            this.config.save();
        } catch (ConfigurationException e) {
            log.warn("Could not save configuration.", e);
        }
    }

    private String getPath(String key) {
        String path = this.config.getString(key, ".");
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        return path;
    }

    private void initConfig() {
        PropertiesConfiguration config;
        try {
            config = new PropertiesConfiguration(CONFIG_FILE);
            config.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            log.error("Configuration file " + CONFIG_FILE + " not found!", e);
            config = new PropertiesConfiguration();
        }
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> it = this.config.getKeys(); it.hasNext();) {
            String key = it.next();
            sb.append(key);
            sb.append('=');
            sb.append(this.config.getProperty(key));
            sb.append("\n");
        }
        return sb.toString();
    }

}
