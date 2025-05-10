package pixelprocessing;

public class FlatPixelShader
{    
    //We can convert the brigthness value with a set of ASCII characters to simulate the shadow
    public static char pixelProcessing(double lumi)
    {
        //We don't need to calculate interpolation with FlatShader, 
        //so we can procede to merging

        //11 is used only to normalize 
        int pixel = (int) (11*lumi);

        switch(pixel)
        {
            case 0: 
                return '.';
            case 1: 
                return ',';
            case 2: 
                return '-';
            case 3: 
                return '~';
            case 4: 
                return ':';
            case 5: 
                return ';';
            case 6: 
                return '=';
            case 7: 
                return '!';
            case 8: 
                return '*';
            case 9: 
                return '#';
            case 10: 
                return '$';
            case 11: 
                return '@';
            default:
                return '.';
        }
    }
}
