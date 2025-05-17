package geometryprocessing;

import geometry.Point3D;
import geometry.Triangle;

//This class is used to compute the brightness of a triangle based on the Lambertian reflection model.
public class FlatShader 
{
    //[TO-DO]
    //Managing the light direction, and if static we can't change it!
    private static final Point3D light = new Point3D(0, 0, -1);
    static
    {
        //Normalizing the lighting vector
        double lum = Math.sqrt(light.getX()*light.getX() + light.getY()*light.getY() + light.getZ()*light.getZ());
        light.setX(light.getX()/lum);
        light.setY(light.getY()/lum);
        light.setZ(light.getZ()/lum);
    }

    public static double computeBrightness(Triangle tri)
    {
        Point3D normal = tri.normal();
        //To obtain the brightness value we should apply the dot product between the light vector and the triangle's normal vector 
        double brightness_value = normal.getX()*light.getX() + normal.getY()*light.getY() + normal.getZ()*light.getZ(); 
        
        //LAMBERT REFLECTION MODEL
        double kd = 0.80;
        return Math.min(kd * Math.max(0, brightness_value), 1);
    }
}