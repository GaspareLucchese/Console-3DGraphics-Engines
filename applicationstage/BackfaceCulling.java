package applicationstage;

import geometry.Point3D;
import geometry.Triangle;
import scene.Mesh;

//Is used to remove the triangles that that are facing away from the camera.
public class BackfaceCulling 
{
    public static Mesh backface_culling (Mesh triangles)
    {
        Mesh result = new Mesh();

        //Iterate through all the triangles in the mesh
        for(int i = 0; i < triangles.getMesh().size(); i++)
        {
            Triangle tri = triangles.getMesh().get(i);
            if(isFrontFaced(tri))
            {
                //If the triangle is front faced, we add it to the result mesh
                result.addTriangle(tri);
            }
        }
        return result;
    }

    public static boolean isFrontFaced(Triangle tri)
    {
        Point3D normal = tri.normal().normalized();
        double result = normal.dotProduct(tri.getCentroid());
        //It uses the dot product between the triangle's normal and its centroid to determine if the triangle is front faced or not.
        if(result < 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
