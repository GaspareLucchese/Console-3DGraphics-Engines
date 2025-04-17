package geometry;
public class Triangle 
{
    private double brightness_value = 0;
    private char brightness_char = ' ';
    public Point3D zero = new Point3D(0, 0, 0);

    public Triangle()
    {
         this.setTriangle(zero, zero, zero);
    }

    private Point3D p1;
    private Point3D p2;
    private Point3D p3;

    //The triangle is created from three points (vertices)
    public Triangle(Point3D p1, Point3D p2, Point3D p3)
    {
        this.setTriangle(p1, p2, p3);
    }

    public void setTriangle(Point3D p1, Point3D p2, Point3D p3)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Point3D[] getTriangle()
    {
        return new Point3D[]{p1, p2, p3};
    }
   
    public String toString()
    {
        return ("(" + p1.toString() + ", " + p2.toString() + ", " + p3 + ")");
    }

    //Setter and getter methods for manage the brightness and its visualization
    public void setBrightness_char(double lum)
    {
        this.brightness_char = value_to_ASCII(lum);
    }
    public double getBrightness_value()
    {
        return this.brightness_value;
    }
    public char getBrightness_char()
    {
        return this.brightness_char;
    }
    
    //We can convert the brigthness value with a set of ASCII characters to simulate the shadow
    public char value_to_ASCII(double lumi)
    {
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