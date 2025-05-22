package pixelprocessing;

import geometry.Point3D;

//This class is used to process the pixels of the image
public class PhongPixelShader
{    
    private static Point3D light = new Point3D(0, 0, -1);
    private static Point3D cameraDir = new Point3D(0, 0, -1);

    static
    {
        //Normalizing the lighting vector and the camera direction vector
        light = light.normalized();
        cameraDir = cameraDir.normalized();
    }

    //We can convert the brigthness value with a set of ASCII characters to simulate the shadow
    public static char pixelProcessing(Point3D interpolatedNormal)
    {
        /*[PIXEL SHADING STAGE]*/
        //To obtain the specular reflection, we can choose between the Blinn-Phong reflection model or the Phong reflection model
        //BLINN-PHONG REFLECTION MODEL
        //Point3D halfVector = light.add(cameraDir).normalized();
        //PHONG REFLECTION MODEL
        Point3D R = interpolatedNormal.multiply(2 * (interpolatedNormal.getX() * light.getX() + interpolatedNormal.getY() * light.getY() + interpolatedNormal.getZ() * light.getZ())).normalized();

        //We need to set the values of the ambient, diffuse and specular components
        double ka = 0.05;
        double kd = 0.6;
        double ks = 0.35;
        double shininess = 32;
 
        double ambient = ka;
        //To obtain the diffuse reflection value we can use Lambert, appling the dot product between the light vector and the the three triangle's vertex normal vectors
        double diffuse = kd * Math.min(Math.max(0,interpolatedNormal.getX() * light.getX() + interpolatedNormal.getY() * light.getY() + interpolatedNormal.getZ() * light.getZ()), 1);
        //To obtain the specular reflection, we can choose between the Blinn-Phong reflection model or the Phong reflection model
        //BLINN-PHONG REFLECTION MODEL
        // double specular = ks * Math.pow(Math.min(Math.max(0, interpolatedNormal.getX() * halfVector.getX() + interpolatedNormal.getY() * halfVector.getY() + interpolatedNormal.getZ() * halfVector.getZ()), 1), shininess);
        //PHONG REFLECTION MODEL
        double specular = ks * Math.pow(Math.min(Math.max(0, R.getX() * cameraDir.getX() + R.getY() * cameraDir.getY() + R.getZ() * cameraDir.getZ()), 1), shininess);
        
        //The brightness value is the sum of the ambient, diffuse and specular components
        double brightness_value = ambient + diffuse + specular;

        //We need to apply the gamma correction to the interpolated brightness value
        double gammaCorrected = Math.pow(clamp(brightness_value, 0, 1), 1.0 / 2.2);

        /*[MERGING STAGE]*/
        final char[] asciiScale = {'.', ':', '-', '~', '=', '+', '*', '#', '%', 'B', '@'};

        //We interpolatedNormalize the value between 0 and the (array size - 1)
        int index = (int)(gammaCorrected * (asciiScale.length - 1));

        //We set constraints for the brightness value
        index = Math.max(0, Math.min(index, asciiScale.length - 1));

        //Return the corresponding ASCII character
        return asciiScale[index];
    }

    //This method is used to clamp the value between min and max
    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}

