//       _o_                                   
//  ,-.'-----`.__ ;   -Developer: Gaspare Lucchese                    
// ((j`=======',-'    -Data Ultima Modifica: 22/05/2025     
//  `-\       /       -Descrizione: Used to represent a triangle in 3D space
//     `-===-'             

package geometry;

//This class is used to represent a triangle in 3D space
public class Triangle 
{
    private double[] brightness_value = {0, 0, 0};

    //[TO-DO] Use this face normal instead of calculating it every time
    private Point3D faceNormal;

    private Point3D p1;
    private Point3D p2;
    private Point3D p3;

    private Point3D normalVertex1;
    private Point3D normalVertex2;
    private Point3D normalVertex3;

    //The triangle is created from three points (vertices)
    public Triangle(Point3D p1, Point3D p2, Point3D p3)
    {
        this.setTriangle(p1, p2, p3);
    }
     public Triangle()
    {
        this.setTriangle(new Point3D(0, 0, 0), new Point3D(0, 0, 0), new Point3D(0, 0, 0));
    }
    public Triangle(Point3D p1, Point3D p2, Point3D p3, Point3D n1, Point3D n2, Point3D n3)
    {
        this.setTriangle(p1, p2, p3);
        this.setFaceNormal();
        this.setVertexNormals(n1, n2, n3);
    }

    //Setters and Getters Methods
    public void setTriangle(Point3D p1, Point3D p2, Point3D p3)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.setFaceNormal();
    }
    public Point3D[] getTriangle()
    {
        return new Point3D[]{p1, p2, p3};
    }
    //[TO-DO]
    public void setVertexNormals(Point3D n1, Point3D n2, Point3D n3) 
    {
        this.normalVertex1 = n1;
        this.normalVertex2 = n2;
        this.normalVertex3 = n3;
    }
    //[TO-DO] Verify this snippet
    public Point3D[] getNormals() 
    {
        if (normalVertex1 == null || normalVertex2 == null || normalVertex3 == null)
        return null;

        Point3D n1 = normalVertex1.normalized();
        Point3D n2 = normalVertex2.normalized();
        Point3D n3 = normalVertex3.normalized();

        if (n1 == null || n2 == null || n3 == null)
            return null;

        return new Point3D[]{n1, n2, n3};
    }
    public Point3D getFaceNormal() 
    {
        return this.faceNormal;
    }
    public void setFaceNormal() 
    {
        this.faceNormal = this.normal();
    }
   
    //Get the triangle's normal, which is a vector perpendicular to the triangle's plane
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

        //Normalize the normal
        double lung = Math.sqrt(normal.getX()*normal.getX() + normal.getY()*normal.getY() + normal.getZ()*normal.getZ());
        normal.setX(normal.getX()/lung);
        normal.setY(normal.getY()/lung);
        normal.setZ(normal.getZ()/lung);

        return normal;
    }

    //Get the triangle's centroid, which is the average of the three vertices
    public Point3D getCentroid()
    {
        Point3D centroid = new Point3D(
        (this.getTriangle()[0].getX() + this.getTriangle()[1].getX() + this.getTriangle()[2].getX()) / 3.0,
        (this.getTriangle()[0].getY() + this.getTriangle()[1].getY() + this.getTriangle()[2].getY()) / 3.0,
        (this.getTriangle()[0].getZ() + this.getTriangle()[1].getZ() + this.getTriangle()[2].getZ()) / 3.0);

        return centroid;
    }

    //Setter and getter methods to manage the brightness value calculated for a face
    public void setBrightness_value(double[] brightness_value)
    {
        this.brightness_value = brightness_value;
    }
    public double[] getBrightness_value()
    {
        return this.brightness_value;
    }

    public String toString()
    {
        return ("(" + p1.toString() + ", " + p2.toString() + ", " + p3 + ")");
    }
}