package geometry.processing;

import display.Display;
import geometry.Triangle;

public class ScreenMapping 
{
    public static Triangle mapping_the_screen(Triangle tri)
    {
        //Center in screen and scale, based on the screen size
        double centerX = 1;
        double centerY = 1;
        tri.getTriangle()[0].setX(tri.getTriangle()[0].getX() + centerX);
        tri.getTriangle()[0].setY(tri.getTriangle()[0].getY() + centerY);
        tri.getTriangle()[1].setX(tri.getTriangle()[1].getX() + centerX);
        tri.getTriangle()[1].setY(tri.getTriangle()[1].getY() + centerY);
        tri.getTriangle()[2].setX(tri.getTriangle()[2].getX() + centerX);
        tri.getTriangle()[2].setY(tri.getTriangle()[2].getY() + centerY);
        int w = Display.getDIMX();
        int h = Display.getDIMY();
        double b = 0.5;

        tri.getTriangle()[0].setX(tri.getTriangle()[0].getX() * b * w);
        tri.getTriangle()[0].setY(tri.getTriangle()[0].getY() * b * h);
        tri.getTriangle()[1].setX(tri.getTriangle()[1].getX() * b * w);
        tri.getTriangle()[1].setY(tri.getTriangle()[1].getY() * b * h);
        tri.getTriangle()[2].setX(tri.getTriangle()[2].getX() * b * w);
        tri.getTriangle()[2].setY(tri.getTriangle()[2].getY() * b * h);

        return tri;
    }
    
}
