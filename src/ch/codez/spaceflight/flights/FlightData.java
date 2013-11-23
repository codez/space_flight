package ch.codez.spaceflight.flights;

import java.awt.Image;
import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;

public class FlightData {

    private Image image;

    protected double x, y;

    protected double scale = 1.0;

    protected double rotate = 0;

    protected float alpha = 1.0f;

    protected double w, h;

    protected double boundWidth, boundHeight;

    protected AffineTransform at = new AffineTransform();

    protected Logger log = Logger.getLogger(getClass());

    // generates random transformation direction and factor
    public FlightData(Image image) {
        this.image = image;
        this.w = image.getWidth(null);
        this.h = image.getHeight(null);
    }

    public Image getImage() {
        return image;
    }

    /*
     * generates random coordinate values for all objects and generates random
     * size values for shapes
     */
    public void reset(int w, int h) {
        this.boundWidth = w;
        this.boundHeight = h;
        this.x = Math.random() * w * 0.8 + w * 0.1;
        this.y = Math.random() * h * 0.8 + h * 0.1;
        this.scale = 1.0;
        this.rotate = 0;
    }

    public AffineTransform getTransform() {
        at.setToIdentity();
        at.rotate(Math.toRadians(rotate), x, y);
        at.translate(x - getHalfWidth(), y - getHalfHeight());
        // scale relative to screen
        at.scale(scale * getScreenScale(), scale * getScreenScale());
        return at;
    }

    public float getAlpha() {
        if (alpha < 0f) {
            return 0f;
        } else if (alpha > 1.0f) {
            return 1.0f;
        } else {
            return alpha;
        }
    }

    public boolean isGone() {
        double half = Math.max(getHalfHeight(), getHalfWidth());
        return x - half > boundWidth || y - half > boundHeight || x + half < 0 || y + half < 0;
    }

    protected double getHalfWidth() {
        return w * scale * getScreenScale() / 2.0;
    }

    protected double getHalfHeight() {
        return h * scale * getScreenScale() / 2.0;
    }

    protected double getScreenScale() {
        return Math.max(boundWidth, boundHeight) / Math.max(w, h);
    }

    /*
     * calculates new transformation factors for the chosen transformation and
     * the current direction
     */
    public void step() {
    }

}
