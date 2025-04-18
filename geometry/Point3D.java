package geometry;
public class Point3D extends Point2D
{
    //We extend the two dimensional point
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

    public double dotProduct(Point3D p)
    {
        return this.getX() * p.getX() + this.getY() * p.getY() + this.getZ() * p.getZ();
    }

    public Point3D normalized() {
        double length = Math.sqrt(this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ());
        return new Point3D( this.getX()/ length, this.getY() / length, this.getZ() / length);
    }
    
}
