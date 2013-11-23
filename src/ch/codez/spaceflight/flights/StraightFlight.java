package ch.codez.spaceflight.flights;

import java.awt.Image;

import ch.codez.spaceflight.AppOptions;

public class StraightFlight extends FlightData {

    protected double ix, iy;

    protected double iscale;

    // generates random transformation direction and factor
    public StraightFlight(Image image) {
        super(image);
    }

    /*
     * generates random coordinate values for all objects and generates random
     * size values for shapes
     */
    public void reset(int w, int h) {
        super.reset(w, h);
        int speed = AppOptions.getInstance().getStraightFlightSpeed();
        int speedZ = AppOptions.getInstance().getStraightFlightSpeedZ();
        ix = Math.random() * speed - speed / 2.0;
        iy = Math.random() * speed - speed / 2.0;
        scale = 0.01;
        iscale = Math.random() * speedZ / 10000.0 + 0.001;
        log.debug("Straight flight w=" + w + " h=" + h + " x=" + x + " y=" + y + " ix=" + ix
                + " iy=" + iy + " scale=" + scale + " iscale=" + iscale);
    }

    /*
     * calculates new transformation factors for the chosen transformation and
     * the current direction
     */
    public void step() {
        x += ix * Math.pow(scale * 5, 2.0);
        y += iy * Math.pow(scale * 5, 2.0);
        scale = Math.pow(scale * 1000, 1.0 + iscale) / 1000;
        // log.debug("print at " + x + "x" + y + " * " + scale);
    }
}
