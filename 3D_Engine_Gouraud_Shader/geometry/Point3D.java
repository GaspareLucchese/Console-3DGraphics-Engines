package geometry;

//This class is used to represent a point in 3D space
public class Point3D extends Point2D
{
    private double z;

    //We extend the two dimensional point
    public Point3D()
    {
        super();
        this.z = 0;
    }

    public Point3D(double x, double y, double z)
    {
        super(x, y);
        this.setZ(z);
    } 

    //Setters and Getters Methods
    public double getZ()
    {
        return this.z;
    }
    public void setZ(double z)
    {
        this.z = z;
    }

    //[TO-DO] Vedere dove possiamo riutilizzarle
    public double dotProduct(Point3D p)
    {
        return this.getX() * p.getX() + this.getY() * p.getY() + this.getZ() * p.getZ();
    }

    public Point3D normalized() {
        double length = Math.sqrt(this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ());
        return new Point3D( this.getX()/ length, this.getY() / length, this.getZ() / length);
    }

    public Point3D add(Point3D other) {
        return new Point3D(this.getX() + other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ());
    }

    public Point3D subtract(Point3D other) {
        return new Point3D(this.getX() - other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ());
    }

    public Point3D multiply(double scalar) {
        return new Point3D(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    public String toString()
	{
		return ("( " + this.getX() + " , " + this.getY() + " , " + this.getZ() + " )");
	}
}