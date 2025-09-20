//       _o_                                   
//  ,-.'-----`.__ ;   -Developer: Gaspare Lucchese                    
// ((j`=======',-'    -Data Ultima Modifica: 22/05/2025     
//  `-\       /       -Descrizione: Used to represent a 3D mesh, which 
//     `-===-'         is a collection of triangles.  

package scene;

import java.util.ArrayList;
import java.util.List;

import geometry.Point3D;
import geometry.Triangle;

//This class is used to represent a 3D mesh, which is a collection of triangles.
public class Mesh
{
	//The mesh is a list of triangles
    private List<Triangle> mesh = new ArrayList<>();
	private Point3D[] bounding_box = new Point3D[9];

    public void addTriangle(Triangle triangle)
    {
        this.mesh.add(triangle);
    }

	//Setters and Getters Methods
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


	//Only for Debugging
    // public void debug_print_mesh()
    // {
    //     for(int i = 0; i < (this.getMesh()).size(); i++)
    //     {
    //         System.out.println((this.getMesh()).get(i));
    //     }
    // }
	// public void print_bounding_box()
    // {
    //     for(int i = 0; i < 8; i++)
    //     {
    //         System.out.println(bounding_box[i]);
    //     }
	// 	System.out.println();
    // }
}

