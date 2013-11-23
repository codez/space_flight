package ch.codez.spaceflight.flights;

import java.awt.Image;
import java.awt.geom.AffineTransform;

public class ShearFlight extends FlightData {

    protected double iscale;

    private double ialpha;

    private double scaleX;

    private double scaleY;

    private boolean appearing = true;

    private boolean growing = true;

    // generates random transformation direction and factor
    public ShearFlight(Image image) {
        super(image);
    }

    /*
     * generates random coordinate values for all objects and generates random
     * size values for shapes
     */
    public void reset(int w, int h) {
        super.reset(w, h);
        alpha = 0f;
        scale = Math.random() * 0.3 + 0.05 * getScreenScale();
        ialpha = Math.random() * 0.005 + 0.004;

        log.debug("Shear flight x=" + x + " y=" + y + " scale=" + scale);
    }

    public AffineTransform getTransform() {
        at.setToIdentity();
        at.translate(x - getHalfWidth(), y - getHalfHeight());
        at.scale(scaleX, scaleY);
        return at;
    }

    protected double getHalfHeight() {
        return h * scaleY / 2.0;
    }

    protected double getHalfWidth() {
        return w * scaleX / 2.0;
    }

    /*
     * calculates new transformation factors for the chosen transformation and
     * the current direction
     */
    public void step() {
        if (appearing) {
            if ((alpha += ialpha) > 1.0f) {
                appearing = false;
            }
        } else {
            alpha -= ialpha;
        }

        if (growing) {
            if ((scaleY += scale / 40.0) > scale) {
                growing = false;
            }
        } else {
            if ((scaleY -= scale / 40.0) < -scale) {
                growing = true;
            }
        }
        scaleX = Math.min(scale * scale * 1.0 / Math.abs(scaleY), 2.0);

        // log.debug("print at " + x + "x" + y + " * " + scale);
    }

    public float getAlpha() {
        return Math.max((float) Math.pow(1f + alpha, 2f) / 4f - .25f, 0f);
    }

    public boolean isGone() {
        return alpha < 0;
    }
}
