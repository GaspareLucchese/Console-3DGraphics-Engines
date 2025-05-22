package pixelprocessing;

//This class is used to process the pixels of the image
public class GouraudPixelShader
{    
    //We can convert the brigthness value with a set of ASCII characters to simulate the shadow
    public static char pixelProcessing(double[] lumi, double alpha, double beta, double gamma)
    {
        /*PIXEL SHADING STAGE*/
        //We need to calcultate the lineare interpolation of the three brightness values
        double interpolatedLumi = alpha * lumi[0] + beta * lumi[1] + gamma * lumi[2];
        
        //We need to apply the gamma correction to the interpolated brightness value
        double gammaCorrected = Math.pow(clamp(interpolatedLumi, 0, 1), 1.0 / 2.2);

        /*[MERGING STAGE]*/
        final char[] asciiScale = {'.', ':', '-', '~', '=', '+', '*', '#', '%', 'B', '@'};

        //We normalize the value between 0 and the (array size - 1)
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

