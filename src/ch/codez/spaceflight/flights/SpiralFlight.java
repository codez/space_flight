package ch.codez.spaceflight.flights;

import java.awt.Image;

import ch.codez.spaceflight.AppOptions;

public class SpiralFlight extends FlightData {

    protected double ix, iy;

    protected double irotate;

    // generates random transformation direction and factor
    public SpiralFlight(Image image) {
        super(image);
    }

    /*
     * generates random coordinate values for all objects and generates random
     * size values for shapes
     */
    public void reset(int w, int h) {
        super.reset(w, h);
        int speed = AppOptions.getInstance().getSpiralFlightSpeed();
        int rotation = AppOptions.getInstance().getSpiralFlightRotation();

        scale = Math.random() * 0.4 + 0.1 * getScreenScale();

        irotate = Math.random() * rotation - rotation / 2.0;

        ix = Math.random() * speed - speed / 2.0;
        iy = Math.random() * speed - speed / 2.0;

        // lower bound for speed based on rotation speed
        ix = Math.signum(ix) * Math.max(Math.abs(ix), Math.abs(irotate));
        iy = Math.signum(iy) * Math.max(Math.abs(iy), Math.abs(irotate));

        double edge = Math.random();
        if (edge > 0.75) {
            y = 0 - getHalfHeight();
            iy = Math.abs(iy);
        } else if (edge > .5) {
            x = this.boundWidth + getHalfWidth();
            ix = -Math.abs(ix);
        } else if (edge > .25) {
            y = this.boundHeight + getHalfHeight();
            iy = -Math.abs(iy);
        } else {
            x = 0 - getHalfWidth();
            ix = Math.abs(ix);
        }

        log.debug("Spiral flight x=" + x + " y=" + y + " ix=" + ix + " iy=" + iy + " irotate="
                + irotate);
    }

    /*
     * calculates new transformation factors for the chosen transformation and
     * the current direction
     */
    public void step() {
        x += ix;
        y += iy;
        rotate += irotate;
        // log.debug("print at " + x + "x" + y + " * " + scale);
    }
}
