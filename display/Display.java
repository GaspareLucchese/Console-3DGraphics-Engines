package display;

import geometry.Point2D;
import geometry.Point3D;
import geometry.Triangle;

public class Display 
{
    //Screen size to render
    private static final int DIMX = 300;
    private static final int DIMY = 200;

    public char[][] Monitor = new char[DIMY][DIMX];
    public Display()
    {}

    public void setMonitor(char[][] Monitor)
    {
        this.Monitor = Monitor;
    }

    public char[][] getMonitor()
    {
        return this.Monitor;
    }

    public static int getDIMX()
    {
        return DIMX;
    }

    public static int getDIMY()
    {
        return DIMY;
    }

    //Empty the display matrix
    public void reset()
    {
        for(int i = 0; i < DIMY; i++)
        {
            for(int j = 0; j < DIMX; j++)
            {
                Monitor[i][j] = ' ';
            }
        }
    }

    //Every pixel is added on the display matrix to the right position
    public void addPoint(int x, int y, char c)
    {
        this.Monitor[y][x] = c;
    }

    
    //[?]
    //Print all the the pixels on the monitor matrix
    public void print()
    {
        //STAMPA PER CARATTERE???
        /*
        for(int i = 0; i < DIMY; i++)
        {
            boolean voidline = true;

            for(int j = 0; j < DIMX; j++)
            {
                if(Monitor[i][j] != ' ')
                {
                    voidline = false;
                }
            }
            if(voidline == true)
            {
                System.out.println();
            }
            else
            {
                for(int j = 0; j < DIMX; j++)
                {
                    System.out.print(Monitor[i][j]);
                    System.out.print(Monitor[i][j]);
                }
                System.out.println();
            }

        }
        */
        
        //STAMPA PER FRAME???
        /*
        String out = "";
        for(int i = 0; i < DIMY; i++)
        {
            for(int j = 0; j < DIMX; j++)
            {
               out = out.concat("" + Monitor[i][j] + Monitor[i][j]);
            }
            out += '\n';
        }
        System.out.println(out);
        */

        StringBuilder out = new StringBuilder();
        for(int i = 0; i < DIMY; i++)
        {
            for(int j = 0; j < DIMX; j++)
            {
                out.append(Monitor[i][j]).append(Monitor[i][j]);
            }
        out.append('\n');
        }
        System.out.println(out);
    }


    //Rasterization: for filling all the polygonal faces
    public void Rasterization(Triangle T, double [][] buf)
    {
        int ymin, ymax, xmin, xmax;

        //We manage all triangle's points as clockwise sorted
        Point3D p1 = T.getTriangle()[0];
        Point3D p2 = T.getTriangle()[1];
        Point3D p3 = T.getTriangle()[2];

        //Calculate the min and the max values covered by the triangle
        ymin = (int) Math.min(Math.min(p1.getY(), p2.getY()), p3.getY());
        ymax = (int) Math.max(Math.max(p1.getY(), p2.getY()), p3.getY());

        xmin = (int) Math.min(Math.min(p1.getX(), p2.getX()), p3.getX());
        xmax = (int) Math.max(Math.max(p1.getX(), p2.getX()), p3.getX());
        
        //[?]
        //We can iterate only in the triangle tha contains the triangle
        for(int y = ymax; y >= ymin; y--)
        {
            for(int x = xmin; x <= xmax; x++)
            {
                Point2D p = new Point2D(x, y);
                
                //The cross product tell us if a given point p is placed between two vectors (the triangle's edges)
                double prod1 = cross_product(p1, p2, p);
                double prod2 = cross_product(p2, p3, p);
                double prod3 = cross_product(p3, p1, p);

                //[?]
                //Non dovevano essere tutti e tre positivi????????
                //If contained in all three vectors we can add the point, and verify if is conteined on the display
                if(((prod1 <= 0) && (prod2 <= 0) && (prod3 <= 0)) && (((int)Math.ceil(x) >= 0) && ((int)Math.ceil(y) >= 0) && ((int)Math.ceil(x) < DIMX) && ((int)Math.ceil(y) < DIMY)))
                {
                    //[?]
                    //Z-buffer da sistemare?????
                    double z = calculateZ(p1, p2, p3, p);
                    if(z <= buf[y][x])
                    {
                        buf[y][x] = z;
                        addPoint(x, y, T.getBrightness_char());
                    }
                }
            }
        }
    }
    
    public double cross_product(Point3D p1, Point3D p2, Point2D prova)
    {
        double ax = (p2.getX() - p1.getX());
        double ay = (p2.getY() - p1.getY());

        double bx = (prova.getX() - p1.getX());
        double by = (prova.getY() - p1.getY());

        return (ax*by - bx*ay);
    }

    //Obtain al the z values with plane equation
    public double calculateZ(Point3D vert1, Point3D vert2, Point3D vert3, Point2D p)
    {
        //Equation of plane passing through 3 points
        double a = (vert2.getY() - vert1.getY())*(vert3.getZ() - vert1.getZ()) - (vert3.getY() - vert1.getY())*(vert2.getZ() - vert1.getZ());
        double b = (vert2.getZ() - vert1.getZ())*(vert3.getX() - vert1.getX()) - (vert3.getZ() - vert1.getZ())*(vert2.getX() - vert1.getX());
        double c = (vert2.getX() - vert1.getX())*(vert3.getY() - vert1.getY()) - (vert3.getX() - vert1.getX())*(vert2.getY() - vert1.getY());
        double d = -1*(a * vert1.getX() + b * vert2.getY() + c * vert3.getZ());

        return (-a * p.getX() -b * p.getY() -d) / c;
    }
}
