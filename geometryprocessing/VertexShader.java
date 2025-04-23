package geometryprocessing;

import geometry.Point3D;
import geometry.Triangle;

public class VertexShader 
{
    public static double computeBrightness(Triangle tri)
    {
        Point3D light, normal;

        //[?]
        //Gestiamo la direzione della luce (Sistemare?)
        light = new Point3D(0, 0, -1);

        //Normalize the lighting vector
        double lum = Math.sqrt(light.getX()*light.getX() + light.getY()*light.getY() + light.getZ()*light.getZ());
        light.setX(light.getX()/lum);
        light.setY(light.getY()/lum);
        light.setZ(light.getZ()/lum);

        normal = tri.normal();
        //To obtain the brightness value we should apply the dot product between the light vector and the triangle's normal vector 
        double brightness_value = normal.getX()*light.getX() + normal.getY()*light.getY() + normal.getZ()*light.getZ(); 
        
        // We limit the brightness value between 0 and 1
        return Math.max(0, Math.min(brightness_value, 1));
    }
    
}
