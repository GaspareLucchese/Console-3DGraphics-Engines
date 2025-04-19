package scene;

import java.util.ArrayList;
import java.util.List;

import geometry.Point3D;
import geometry.Triangle;

public class Mesh
{
	//We can see the 3D mesh as a set of polygons (Triangles)
    private List<Triangle> mesh = new ArrayList<>();
	private Point3D[] bounding_box = new Point3D[9];

    public void addTriangle(Triangle triangle)
    {
        this.mesh.add(triangle);
    }

	public void modifyTriangle(int i, Triangle triangle)
    {
        this.mesh.set(i, triangle);
    }

	//Setter method to modify the mesh
    public void setMesh(List<Triangle> Mesh)
    {
        this.mesh = Mesh;
    }
	public void setBoundingBox()
	{
		this.bounding_box = this.createBoundinBox();
	}

    public List<Triangle> getMesh()
    {
        return this.mesh;
    }
	public Point3D[] getBoundingBox()
    {
        return this.bounding_box;
    }

	//With this method we can copy all the triangles in this mesh to another destination
    public void copyMesh(Mesh new_mesh)
    {
        for(int i = 0; i < this.mesh.size(); i++)
		{
			new_mesh.mesh.add(i, (this.getMesh()).get(i));
		}
    }

	//Calculate the bounding box of the mesh
	public Point3D[] createBoundinBox()
	{
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;

		for(Triangle tri : this.getMesh())
		{
			for(Point3D p : tri.getTriangle())
			{
				minX = Math.min(minX, p.getX());
				minY = Math.min(minY, p.getY());
				minZ = Math.min(minZ, p.getZ());
				maxX = Math.max(maxX, p.getX());
				maxY = Math.max(maxY, p.getY());
				maxZ = Math.max(maxZ, p.getZ());
			}
		}

		return new Point3D[] {
            new Point3D(minX, minY, minZ),
            new Point3D(minX, minY, maxZ),
            new Point3D(minX, maxY, minZ),
            new Point3D(minX, maxY, maxZ),
            new Point3D(maxX, minY, minZ),
            new Point3D(maxX, minY, maxZ),
            new Point3D(maxX, maxY, minZ),
            new Point3D(maxX, maxY, maxZ)
        };
	}


	//Only for debugging
    public void debug_print_mesh()
    {
        for(int i = 0; i < (this.getMesh()).size(); i++)
        {
            System.out.println((this.getMesh()).get(i));
        }
    }
	public void print_bounding_box()
    {
        for(int i = 0; i < 8; i++)
        {
            System.out.println(bounding_box[i]);
        }
		System.out.println();
    }

	//This function create the maze's walls and set them in the mesh
    public Mesh createSolid(char[][] maze, Position pos)
	{
		Mesh solid = new Mesh();

		for(int i = (maze.length - 3); i > 1 ; i--)
		{
			for(int j = ((maze[0].length) - 3); j > 1; j--)
			{
				if(maze[i][j] == '*')
				{
					int xplus = 2*((- j + pos.x));
                    int zplus = 2*((- i + pos.y));

					//Default values ​​for creating a parallelepiped
					double[][][] values = {

						{{-1.0 , -2.0 , -1.0},  {-1.0 ,  1.0 , -1.0},  { 1.0 ,  1.0 , -1.0}},
            			{{-1.0 , -2.0 , -1.0},  { 1.0 ,  1.0 , -1.0},  { 1.0 , -2.0 , -1.0}},

            			{{ 1.0 , -2.0 , -1.0},  { 1.0 ,  1.0 , -1.0},  { 1.0 ,  1.0 ,  1.0}},
            			{{ 1.0 , -2.0 , -1.0},  { 1.0 ,  1.0 ,  1.0},  { 1.0 , -2.0 ,  1.0}},

            			{{ 1.0 , -2.0 ,  1.0},  { 1.0 ,  1.0 ,  1.0},  {-1.0 ,  1.0 ,  1.0}},
            			{{ 1.0 , -2.0 ,  1.0},  {-1.0 ,  1.0 ,  1.0},  {-1.0 , -2.0 ,  1.0}},

            			{{-1.0 , -2.0 ,  1.0},  {-1.0 ,  1.0 ,  1.0},  {-1.0 ,  1.0 , -1.0}},
            			{{-1.0 , -2.0 ,  1.0},  {-1.0 ,  1.0 , -1.0},  {-1.0 , -2.0 , -1.0}},

            			{{-1.0 ,  1.0 , -1.0},  {-1.0 ,  1.0 ,  1.0},  { 1.0 ,  1.0 ,  1.0}},
            			{{-1.0 ,  1.0 , -1.0},  { 1.0 ,  1.0 ,  1.0},  { 1.0 ,  1.0 , -1.0}},

            			{{ 1.0 , -2.0 ,  1.0},  {-1.0 , -2.0 ,  1.0},  {-1.0 , -2.0 , -1.0}},
            			{{ 1.0 , -2.0 ,  1.0},  {-1.0 , -2.0 , -1.0},  { 1.0 , -2.0 , -1.0}}
					};

					//Creation of the points based on the values...
					Point3D points[][] = new Point3D[12][3];
        			for(int l = 0; l < 12; l++)
        			{
            			for (int m = 0; m < 3; m++)
            			{
                			Point3D temp = new Point3D(values[l][m][0] + xplus, values[l][m][1], values[l][m][2] + zplus);
                			points[l][m] = temp;
            			}
        			}

					//...and creation of triangles based on the points, tha we can add in the mesh
					for(int k = 0; k < 12; k++)
        			{
						//if(Math.min(Math.min(Math.floor(punti[k][0].getZ()+Engine.distance), Math.floor(punti[k][1].getZ()+Engine.distance)), Math.floor(punti[k][2].getZ()+Engine.distance)) < 0)
            			Triangle temporary = new Triangle(points[k][0], points[k][1], points[k][2]);
                		solid.addTriangle(temporary);
        			}
				}
			}
		}

		return solid;
	}

}

