package ch.codez.spaceflight.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ch.codez.spaceflight.AppOptions;
import ch.codez.spaceflight.flights.FlightData;
import ch.codez.spaceflight.flights.ShearFlight;
import ch.codez.spaceflight.flights.SpiralFlight;
import ch.codez.spaceflight.flights.StraightFlight;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;

public class FlightAnimation extends Window implements Runnable {

    private final static Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);

    private List<FlightData> astronauts = Collections.synchronizedList(new ArrayList<FlightData>());

    private Thread thread;

    private BufferedImage bimg;

    private List<File> astronautFiles = new ArrayList<File>();

    private long nextAstronautTime;

    private static Logger log = Logger.getLogger(FlightAnimation.class);

    public FlightAnimation(Window owner) {
        super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
        AWTUtilities.setWindowOpaque(this, false);
        setLayout(null);
    }

    public void paint(Graphics g) {
        Rectangle rect = getRect();

        step();
        Graphics2D g2 = createGraphics2D(rect.width, rect.height);
        drawAstronauts(g2);
        g2.dispose();
        g.drawImage(bimg, rect.x, rect.y, this);
    }

    public void pause() {
        if (thread == null) {
            start();
        } else {
            stop();
        }
    }

    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public synchronized void stop() {
        thread = null;
    }

    public void run() {
        long freq = AppOptions.getInstance().getFlightFrequency();
        long rate = AppOptions.getInstance().getFrameRate();
        Thread me = Thread.currentThread();
        while (thread == me) {
            if (System.currentTimeMillis() > nextAstronautTime) {
                loadAstronaut();
                nextAstronautTime = (long) (System.currentTimeMillis() + freq / 2 + Math.random()
                        * freq);
            }
            repaint();
            try {
                thread.sleep(rate);
            } catch (InterruptedException e) {
                break;
            }
        }
        thread = null;
    }

    // calls ObjectData.reset for each item in vector
    private void reset(int w, int h) {
        synchronized (astronauts) {
            for (FlightData data : astronauts) {
                data.reset(w, h);
            }
        }
    }

    // calls ObjectData.step for each item in vector
    private void step() {
        synchronized (astronauts) {
            Iterator<FlightData> it = astronauts.listIterator();
            while (it.hasNext()) {
                FlightData data = it.next();
                data.step();
                if (data.isGone()) {
                    it.remove();
                }
            }
        }
    }

    private void drawAstronauts(Graphics2D g2) {
        synchronized (astronauts) {
            for (FlightData data : astronauts) {
                g2.setTransform(data.getTransform());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, data.getAlpha()));
                g2.drawImage(data.getImage(), 0, 0, this);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));
            }
        }
    }

    private void loadAstronaut() {
        File file = getNextAstronautFile();
        if (file == null) {
            log.warn("No astronauts available");
            return;
        }

        String url = file.getAbsolutePath();
        log.debug("Loading astronaut " + url);

        FlightData flight = getRandomFlight(getImage(url));
        Rectangle rect = getRect();
        flight.reset(rect.width, rect.height);
        astronauts.add(0, flight);
    }

    private File getNextAstronautFile() {
        if (astronautFiles.isEmpty()) {
            File folder = new File(AppOptions.getInstance().getPathAstronauts());
            File[] files = folder.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".png");
                }
            });
            astronautFiles.addAll(Arrays.asList(files));
            Collections.shuffle(astronautFiles);
        }

        return astronautFiles.isEmpty() ? null : astronautFiles.remove(0);
    }

    private FlightData getRandomFlight(Image img) {
        AppOptions opts = AppOptions.getInstance();
        int straightCount = opts.getStraightFlightCount();
        int spiralCount = opts.getSpiralFlightCount();
        int shearCount = opts.getShearFlightCount();
        int total = straightCount + spiralCount + shearCount;

        double f = Math.random() * total;
        if (f > total - straightCount) {
            return new StraightFlight(img);
        } else if (f > shearCount) {
            return new SpiralFlight(img);
        } else {
            return new ShearFlight(img);
        }
    }

    private Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            reset(w, h);

        }
        g2 = bimg.createGraphics();
        g2.setBackground(TRANSPARENT);
        g2.clearRect(0, 0, w, h);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g2;
    }

    private Rectangle getRect() {
        Dimension d = getSize();
        Rectangle rect = new Rectangle();
        double ratio = AppOptions.getInstance().getMovieRatio();
        if (d.width / (double) d.height > ratio) {
            rect.x = (int) (d.width - d.height * ratio) / 2;
            rect.y = 0;
        } else {
            rect.x = 0;
            rect.y = (int) (d.height - d.width / ratio) / 2;
        }
        rect.width = d.width - rect.x * 2;
        rect.height = d.height - rect.y * 2;
        return rect;
    }

    private Image getImage(String name) {
        Image img = getToolkit().getImage(name);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }
}
