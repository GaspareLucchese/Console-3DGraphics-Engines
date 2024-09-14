public class Punto2D 
{
    private double x;
    private double y;

    public Punto2D()
    {
        this.x = 0;
        this.y = 0;
    }

    public double getX()
    {
        return x;
    }
    public double getY()
    {
        return y;
    }

    public void setX(double x)
    {
        this.x = x;
    }
    public void setY(double y)
    {
        this.y = y;
    }

    public Punto2D(double x, double y)
    {
        this.setX(x);
        this.setY(y);
    }

    public String toString()
	{
		return ("( " + this.getX() + " , " + this.getY() + " )");
	}
}
