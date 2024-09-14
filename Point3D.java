public class Point3D extends Point2D
{
    //We can extend the two dimensional point
    public Point3D()
    {
        super();
        this.z = 0;
    }

    private double z;

    public double getZ()
    {
        return this.z;
    }
    public void setZ(double z)
    {
        this.z = z;
    }

    public Point3D(double x, double y, double z)
    {
        super(x, y);
        this.setZ(z);
    } 
    
    public String toString()
	{
		return ("( " + this.getX() + " , " + this.getY() + " , " + this.getZ() + " )");
	}
}
