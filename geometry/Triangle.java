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

    public Point3D normal()
    {
        Point3D normal = new Point3D();
        Point3D line1 = new Point3D();
        Point3D line2 = new Point3D();

        //Calculate the triangle's cross product...
        line1.setX(this.getTriangle()[1].getX() - this.getTriangle()[0].getX());
        line1.setY(this.getTriangle()[1].getY() - this.getTriangle()[0].getY());
        line1.setZ(this.getTriangle()[1].getZ() - this.getTriangle()[0].getZ());

        line2.setX(this.getTriangle()[2].getX() - this.getTriangle()[0].getX());
        line2.setY(this.getTriangle()[2].getY() - this.getTriangle()[0].getY());
        line2.setZ(this.getTriangle()[2].getZ() - this.getTriangle()[0].getZ());

        //... to obtain the normal (that we should normalize)
        normal.setX(line1.getY()*line2.getZ() - line1.getZ()*line2.getY());
        normal.setY(line1.getZ()*line2.getX() - line1.getX()*line2.getZ());
        normal.setZ(line1.getX()*line2.getY() - line1.getY()*line2.getX());

        double lung = Math.sqrt(normal.getX()*normal.getX() + normal.getY()*normal.getY() + normal.getZ()*normal.getZ());
        normal.setX(normal.getX()/lung);
        normal.setY(normal.getY()/lung);
        normal.setZ(normal.getZ()/lung);

        return normal;
    }

    public Point3D getCentroid()
    {
        Point3D centroid = new Point3D(
        (this.getTriangle()[0].getX() + this.getTriangle()[1].getX() + this.getTriangle()[2].getX()) / 3.0,
        (this.getTriangle()[0].getY() + this.getTriangle()[1].getY() + this.getTriangle()[2].getY()) / 3.0,
        (this.getTriangle()[0].getZ() + this.getTriangle()[1].getZ() + this.getTriangle()[2].getZ()) / 3.0);

        return centroid;
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