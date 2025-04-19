package application;

import display.Display;
import geometry.Point3D;

public class FrustumCulling {

    static double fNear = 0.01;
    static double fFar = 1000.0;
    static double fFov = Math.toRadians(90.0);
    static double fAspectRatio = (double)(Display.getDIMX()/Display.getDIMY());
    static double fFovRad = 1.0/ Math.tan(fFov / 2);

    public static boolean isInFrustum(Point3D[] box)
    {
    
        double[][] frustumPlanes = computeFrustumPlanes();

        for(double[] plane : frustumPlanes)
        {
            int out = 0;
            for (Point3D p : box)
            {
                //[??]
                double distance = plane[0]*p.getX() + plane[1]*p.getY() + plane[2]*p.getZ() +  plane[3];
                if (distance < 0)
                {
                    out++;
                }
            }
            if(out == 8)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        return true;
    }

    public static double[][] computeFrustumPlanes()
    {
        //every plane is written as ax + by +cz + d = 0
        double[][] planes = new double[6][4]; 

        double nearHeight = fNear * fFovRad;
        double nearWidth = nearHeight * fAspectRatio;

        //[??]
        // Near plane: normale (0, 0, 1), punto (0, 0, zNear)
        planes[0] = new double[]{0, 0, 1, -fNear};
        // Far plane: normale (0, 0, -1), punto (0, 0, zFar)
        planes[1] = new double[]{0, 0, -1, fNear};
        // Left plane: normale approx. (nw, 0, zNear) ⟶ cross((0,1,0), (zNear,0,-nw))
        planes[2] = normalizePlane(new double[]{fNear, 0, -nearWidth});
        // Right plane: normale approx. (-nw, 0, zNear)
        planes[3] = normalizePlane(new double[]{-fNear, 0, -nearWidth});
        // Top plane: normale approx. (0, -nh, zNear)
        planes[4] = normalizePlane(new double[]{0, -fNear, -nearHeight});
        // Bottom plane: normale approx. (0, nh, zNear)
        planes[5] = normalizePlane(new double[]{0, fNear, -nearHeight});

        return planes;
    }

    private static double[] normalizePlane(double[] normal) 
    {
        double length = (double) Math.sqrt(normal[0]*normal[0] + normal[1]*normal[1] + normal[2]*normal[2]);
        return new double[]{
            normal[0] / length,
            normal[1] / length,
            normal[2] / length,
            0 // passa per l'origine
        };
    }
    
}
