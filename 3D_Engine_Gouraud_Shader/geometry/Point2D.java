//       _o_                                   
//  ,-.'-----`.__ ;   -Developer: Gaspare Lucchese                    
// ((j`=======',-'    -Data Ultima Modifica: 22/05/2025     
//  `-\       /       -Descrizione: Used to represent a point in 2D space
//     `-===-'             

package geometry;

//This class is used to represent a point in 2D space
public class Point2D 
{
    private double x;
    private double y;

    public Point2D()
    {
        this.x = 0;
        this.y = 0;
    }
    public Point2D(double x, double y)
    {
        this.setX(x);
        this.setY(y);
    }

    //Setters and Getters Methods
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

    public String toString()
	{
		return ("( " + this.getX() + " , " + this.getY() + " )");
	}
}
