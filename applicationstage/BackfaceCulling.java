package applicationstage;

import geometry.Point3D;
import scene.Mesh;
import geometry.Triangle;

public class BackfaceCulling 
{
    public static Mesh backface_culling (Mesh triangles)
    {
        Mesh result = new Mesh();

        for(int i = 0; i < triangles.getMesh().size(); i++)
        {
            Triangle tri = triangles.getMesh().get(i);
            if(isFrontFaced(tri))
            {
                result.addTriangle(tri);
            }
        }
        return result;
    }

    public static boolean isFrontFaced(Triangle tri)
    {
        Point3D normal = tri.normal().normalized();
        double result = normal.dotProduct(tri.getCentroid());
        //Backface Culling = dot product between the triangle's vector and its normal
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
