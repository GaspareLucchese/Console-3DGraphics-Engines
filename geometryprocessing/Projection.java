package geometryprocessing;

import display.Display;
import geometry.Point3D;
import geometry.Triangle;

public class Projection 
{
    private double[][] matrixPerspective = new double[4][4];

    //[!] Move all in Projection.java
    public Projection()
    {
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                matrixPerspective[i][j] = 0;
            }
        }

        //Setting the perspective matrix
        double fNear = 0.01;
        double fFar = 1000.0;
        double fFov = Math.toRadians(90.0);
        double fAspectRatio = (double)(Display.getDIMX()/Display.getDIMY());
        double fFovRad = 1.0/ Math.tan(fFov / 2);
        matrixPerspective[0][0] = 1/(fAspectRatio * fFovRad);
        matrixPerspective[1][1] = 1/(fFovRad);
        matrixPerspective[2][2] = (-fFar * fNear) / (fFar - fNear);
        matrixPerspective[2][3] = -(2*fFar * fNear) / (fFar - fNear);
        matrixPerspective[3][2] = -1.0;
        matrixPerspective[3][3] = 0;        
    }


    public Triangle Projects(Triangle tri)
    {
        //Apply the projection
        return new Triangle(MatrixMultiplication(tri.getTriangle()[0], matrixPerspective), MatrixMultiplication(tri.getTriangle()[1], matrixPerspective), MatrixMultiplication(tri.getTriangle()[2], matrixPerspective));
    }

    //To calculate matrix multiplications
    public Point3D MatrixMultiplication(Point3D i, double[][] m)
    {
        Point3D o = new Point3D();
        o.setX(((i.getX())*(m[0][0]) + (i.getY())*(m[0][1]) + (i.getZ())*(m[0][2]) + m[0][3]));
        o.setY(((i.getX())*(m[1][0]) + (i.getY())*(m[1][1]) + (i.getZ())*(m[1][2]) + m[1][3]));
        o.setZ(((i.getX())*(m[2][0]) + (i.getY())*(m[2][1]) + (i.getZ())*(m[2][2]) + m[2][3]));
        
        double w = (((i.getX())*(m[3][0]) + (i.getY())*(m[3][1]) + (i.getZ())*(m[3][2]) + m[3][3]));
        if(w != 1.0)
        {
            o.setX(o.getX()/w);
            o.setY(o.getY()/w);
            o.setZ(o.getZ()/w);
        }

        return o;
    }
}
