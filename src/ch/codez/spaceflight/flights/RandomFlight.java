package ch.codez.spaceflight.flights;

import java.awt.Image;
import java.awt.geom.AffineTransform;

public class RandomFlight {

    private Image image;

    static final int UP = 0;

    static final int DOWN = 1;

    protected double x, y;

    protected double ix = 5, iy = 3;

    int rotate;

    double scale, shear;

    int scaleDirection, shearDirection;

    boolean doShear = true, doTranslate = true, doScale = true, doRotate = true;

    protected AffineTransform at = new AffineTransform();

    // generates random transformation direction and factor
    public RandomFlight(Image image) {
        this.image = image;
        rotate = (int) (Math.random() * 360);
        scale = Math.random() * 1.5;
        scaleDirection = Math.random() > 0.5 ? UP : DOWN;
        shear = Math.random() * 0.5;
        shearDirection = Math.random() > 0.5 ? UP : DOWN;
    }

    public Image getImage() {
        return image;
    }

    public AffineTransform getTransform() {
        return at;
    }

    /*
     * generates random coordinate values for all objects and generates random
     * size values for shapes
     */
    public void reset(int w, int h) {
        x = Math.random() * w;
        y = Math.random() * h;
    }

    /*
     * calculates new transformation factors for the chosen transformation and
     * the current direction
     */
    public void step(int w, int h) {
        at.setToIdentity();
        if (doRotate) {
            if ((rotate += 5) == 360) {
                rotate = 0;
            }
            at.rotate(Math.toRadians(rotate), x, y);
        }
        at.translate(x, y);
        if (doTranslate) {
            x += ix;
            y += iy;
            if (x > w) {
                x = w - 1;
                ix = Math.random() * -w / 32 - 1;
            }
            if (x < 0) {
                x = 2;
                ix = Math.random() * w / 32 + 1;
            }
            if (y > h) {
                y = h - 2;
                iy = Math.random() * -h / 32 - 1;
            }
            if (y < 0) {
                y = 2;
                iy = Math.random() * h / 32 + 1;
            }
        }
        if (doScale && scaleDirection == UP) {
            if ((scale += 0.05) > 1.5) {
                scaleDirection = DOWN;
            }
        } else if (doScale && scaleDirection == DOWN) {
            if ((scale -= .05) < 0.5) {
                scaleDirection = UP;
            }
        }
        if (doScale) {
            at.scale(scale, scale);
        }
        if (doShear && shearDirection == UP) {
            if ((shear += 0.05) > 0.5) {
                shearDirection = DOWN;
            }
        } else if (doShear && shearDirection == DOWN) {
            if ((shear -= .05) < -0.5) {
                shearDirection = UP;
            }
        }
        if (doShear) {
            at.shear(shear, shear);
        }
    }
}
