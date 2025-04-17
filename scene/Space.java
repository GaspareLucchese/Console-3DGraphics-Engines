package scene;


import java.util.ArrayList;
import java.util.List;

import geometry.Point3D;
import geometry.Triangle;

public class Space
{
	//We can see the 3D space as a set of polygons (Triangles)
    private List<Triangle> space = new ArrayList<>();

    public void addTriangle(Triangle triangle)
    {
        this.space.add(triangle);
    }

	public void modifyTriangle(int i, Triangle triangle)
    {
        this.space.set(i, triangle);
    }

	//Setter method to modify the space
    public void setSpace(List<Triangle> Space)
    {
        this.space = Space;
    }

    public List<Triangle> getSpace()
    {
        return this.space;
    }

	//With this method we can copy all the triangles in this space to another destination
    public void copySpace(Space new_space)
    {
        for(int i = 0; i < this.space.size(); i++)
		{
			new_space.space.add(i, (this.getSpace()).get(i));
		}
    }

	//Only for debugging
    public void debug_print_space()
    {
        for(int i = 0; i < (this.getSpace()).size(); i++)
        {
            System.out.println((this.getSpace()).get(i));
        }
    }

	//This function create the maze's walls and set them in the space
    public Space createSolid(char[][] maze, Position pos)
	{
		Space solid = new Space();

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

					//...and creation of triangles based on the points, tha we can add in the space
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

