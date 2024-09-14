public class Punto3D extends Punto2D
{
    //Estendiamo il punto 2D
    public Punto3D()
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

    public Punto3D(double x, double y, double z)
    {
        super(x, y);
        this.setZ(z);
    } 
    
    public String toString()
	{
		return ("( " + this.getX() + " , " + this.getY() + " , " + this.getZ() + " )");
	}
}
