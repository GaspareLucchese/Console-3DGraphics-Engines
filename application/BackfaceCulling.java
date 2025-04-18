package application;

import geometry.Point3D;
import scene.Space;
import geometry.Triangle;

public class BackfaceCulling 
{
    public static Space backface_culling (Space triangles)
    {
        System.out.println("triangles: " + triangles.getSpace().size());
        Space result = new Space();

        for(int i = 0; i < triangles.getSpace().size(); i++)
        {
            Triangle tri = triangles.getSpace().get(i);
            if(isFrontFaced(tri))
            {
                result.addTriangle(tri);
            }
        }
        System.out.println("result " + result.getSpace().size());
        return result;
    }

    public static boolean isFrontFaced(Triangle tri)
    {
        Point3D normal = tri.normal().normalized();
        double result = normal.dotProduct(tri.getCentroid());
        //backface-culling = dot product between the triangle's vector and its normal
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
