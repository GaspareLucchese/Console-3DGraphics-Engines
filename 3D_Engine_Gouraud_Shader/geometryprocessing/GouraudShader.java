package geometryprocessing;

import geometry.Point3D;
import geometry.Triangle;

//This class is used to compute the brightness of a triangle based on the Lambertian reflection model.
public class GouraudShader 
{
    //[TO-DO]
    //Managing the light direction, and if static we can't change it!

    private static Point3D light = new Point3D(0, 0, -1);
    static
    {
        //Normalizing the lighting vector
        light = light.normalized();
    }

    public static double[] computeBrightness(Triangle tri)
    {
        Point3D[] normal = tri.getNormals();
        //[TO-DO] Check if we need this base value
        double [] brightness_value = {0, 0, 0};

        //[TO-DO] Check if we need this condition
        if (normal == null) {
            //System.err.println("The Triangle has no Vertex Normals setted!");
            //return brightness_value;
        }

        for (int i = 0; i < 3; i++) 
        {
            if (normal[i] == null) 
            {
                //[TO-DO] DEBUGGING
                //System.err.println("Vertex Normal at index " + i);
                continue;
            }

            //LAMBERT REFLECTION MODEL
            double kd = 0.80;
            //To obtain the brightness value we should apply the dot product between the light vector and the the three triangle's vertex normal vectors 
            brightness_value[i] = kd * Math.min(Math.max(0,normal[i].getX() * light.getX() + normal[i].getY() * light.getY() + normal[i].getZ() * light.getZ()), 1);
        }
        
        return brightness_value;

    }
    
}
