package rasterization;

import display.Display;
import geometry.Point2D;
import geometry.Point3D;
import geometry.Triangle;
import pixelprocessing.PhongPixelShader;

//This class is used to rasterize a triangle in 3D space into a 2D image using a z-buffer for depth testing.
public class Rasterization
{
    private final Triangle T;
    private final double[][] zBuffer;
    private final Display display;
    Point3D p1, p2, p3;
    private int ymin, ymax, xmin, xmax;
    private double alpha, beta, gamma;
    
    public Rasterization(Triangle T, double[][] zBuffer, Display display)
    {
        this.T = T;
        this.zBuffer = zBuffer;
        this.display = display;

        //We manage all triangle's points as clockwise sorted
        p1 = T.getTriangle()[0];
        p2 = T.getTriangle()[1];
        p3 = T.getTriangle()[2];

        this.triangleSetup();
    }

    public void triangleSetup()
    { 
        //Calculate the min and the max values covered by the triangle
        this.ymin = (int) Math.min(Math.min(p1.getY(), p2.getY()), p3.getY());
        this.ymax = (int) Math.max(Math.max(p1.getY(), p2.getY()), p3.getY());

        this.xmin = (int) Math.min(Math.min(p1.getX(), p2.getX()), p3.getX());
        this.xmax = (int) Math.max(Math.max(p1.getX(), p2.getX()), p3.getX());

        this.triangleTraversal();
    }

    public void triangleTraversal()
    {
        //We can iterate only in the triangle tha contains the triangle
        for(int y = ymax; y >= ymin; y--)
        {
            for(int x = xmin; x <= xmax; x++)
            {
                if (x >= 0 && y >= 0 && x < Display.getDIMX() && y < Display.getDIMY())
                {
                    Point2D p = new Point2D(x, y);

                    //The cross product tell us if a given point p is placed between two vectors (the triangle's edges)
                    double prod1 = cross_product(p1, p2, p);
                    double prod2 = cross_product(p2, p3, p);
                    double prod3 = cross_product(p3, p1, p);

                    //[TO-DO] The three cross-products should not be all positive???
                    //If contained in all three vectors we can add the point, and verify if is conteined on the display
                    if(((prod1 <= 0) && (prod2 <= 0) && (prod3 <= 0)) && (((int)Math.ceil(x) >= 0) && ((int)Math.ceil(y) >= 0) && ((int)Math.ceil(x) < Display.getDIMX()) && ((int)Math.ceil(y) < Display.getDIMY())))
                    {
                        double z = calculateZ(p1, p2, p3, p);
                        //If a point covered another one on monitor, we can store it in the z-buffer
                        if(z >= zBuffer[y][x])
                        {
                            zBuffer[y][x] = z;
                            //We set the parameters for the barycentric coordinate system (alpha, beta, gamma)
                            setBarycentricCoordinates(T, p);
                            //We can calculate the interpolated normal on the pixel
                            Point3D pixelNormal = interpolatedNormal(T.getNormals()[0], T.getNormals()[1], T.getNormals()[2]);
                            //We compute the pixel value to add to the monitor screen
                            display.addPoint(x, y, PhongPixelShader.pixelProcessing(pixelNormal));
                        }
                    }
                } 
            }
        }
    }

    //[TO-DO: MOVE IN ANOTHER CLASS]
    //Cross product between two vectors
    public double cross_product(Point3D p1, Point3D p2, Point2D p3)
    {
        double ax = (p2.getX() - p1.getX());
        double ay = (p2.getY() - p1.getY());

        double bx = (p3.getX() - p1.getX());
        double by = (p3.getY() - p1.getY());

        return (ax*by - bx*ay);
    }

    //[TO-DO: MOVE IN ANOTHER CLASS]
    //Obtain al the z values with plane equation
    public double calculateZ(Point3D vert1, Point3D vert2, Point3D vert3, Point2D p)
    {
        //Equation of plane passing through 3 points
        double a = (vert2.getY() - vert1.getY())*(vert3.getZ() - vert1.getZ()) - (vert3.getY() - vert1.getY())*(vert2.getZ() - vert1.getZ());
        double b = (vert2.getZ() - vert1.getZ())*(vert3.getX() - vert1.getX()) - (vert3.getZ() - vert1.getZ())*(vert2.getX() - vert1.getX());
        double c = (vert2.getX() - vert1.getX())*(vert3.getY() - vert1.getY()) - (vert3.getX() - vert1.getX())*(vert2.getY() - vert1.getY());
        if(c == 0)
        {
            return Float.POSITIVE_INFINITY;
        }
        double d = -1*(a * vert1.getX() + b * vert1.getY() + c * vert1.getZ());

        return (-a * p.getX() -b * p.getY() -d) / c;
    }

    //We set the parameters for the barycentric coordinate system
    public void setBarycentricCoordinates(Triangle t, Point2D p)
    {
        //We calculate the barycentric coordinates as the ratio of the area of the triangle formed by the point and two vertices of the triangle to the area of the triangle itself
        double areaT = area(t.getTriangle()[0], t.getTriangle()[1], t.getTriangle()[2]);
        this.alpha = area(p, t.getTriangle()[1], t.getTriangle()[2]) / areaT;
        this.beta = area(p, t.getTriangle()[2], t.getTriangle()[0]) / areaT;
        this.gamma = area(p, t.getTriangle()[0], t.getTriangle()[1]) / areaT;
    }

    //[TO-DO] Check this method
    //We calculate the area of a triangle using the determinant formula
    public double area(Point2D p1, Point3D p2, Point3D p3)
    {
        return Math.abs((p1.getX() * (p2.getY() - p3.getY()) +
                        p2.getX() * (p3.getY() - p1.getY()) +
                        p3.getX() * (p1.getY() - p2.getY())) / 2.0);
    }

    //We calculate the interpolated normal using the barycentric coordinates
    private Point3D interpolatedNormal(Point3D n1, Point3D n2, Point3D n3)
    {
        Point3D interpolatedNormal = new Point3D(
            alpha * n1.getX() + beta * n2.getX() + gamma * n3.getX(),
            alpha * n1.getY() + beta * n2.getY() + gamma * n3.getY(),
            alpha * n1.getZ() + beta * n2.getZ() + gamma * n3.getZ());
        return interpolatedNormal.normalized();
    }


}
