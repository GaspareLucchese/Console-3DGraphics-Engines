//       _o_                                   
//  ,-.'-----`.__ ;   -Developer: Gaspare Lucchese                    
// ((j`=======',-'    -Data Ultima Modifica: 22/05/2025     
//  `-\       /       -Descrizione: Used to map the triangle to the screen coordinates.
//     `-===-'         The screen coordinates are in the range [0, 1] for both x and y.
//                     The triangle is mapped to the screen coordinates by centering it and scaling it based on the screen size.

package geometryprocessing;

import display.Display;
import geometry.Triangle;

/*
This class is used to map the triangle to the screen coordinates.
The screen coordinates are in the range [0, 1] for both x and y.
The triangle is mapped to the screen coordinates by centering it and scaling it based on the screen size.
*/
public class ScreenMapping 
{
    //This method maps the triangle to the screen coordinates
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
