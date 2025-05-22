package geometryprocessing;

import java.util.ArrayList;
import java.util.List;

import geometry.Point3D;
import geometry.Triangle;
import applicationstage.BackfaceCulling;

//This class is used to clip triangles against a frustum defined by its planes
public class Clipping 
{
    public static List<Triangle> clipTriangleAgainstFrustum(Triangle tri, double[][] planes) 
    {
        List<Triangle> clippedTriangles = new ArrayList<>();
        clippedTriangles.add(tri); //We start with the original triangle

        //We cli77wp against all frustum planes
        for (int i = 0; i < planes.length; i++) {
            List<Triangle> newClippedTriangles = new ArrayList<>();

            //For every triangle, we clip it against the current plane
            for (Triangle t : clippedTriangles) {
                newClippedTriangles.addAll(clipTriangleAgainstPlane(t, planes[i]));
            }

            //Update the list
            clippedTriangles = newClippedTriangles;
            //If the list is empty, it means that every triangle was deleted
            if (clippedTriangles.isEmpty()) 
            {
                break;
            }
        }
        return clippedTriangles;
    }

    //Clip a triangle against a single plane
    private static List<Triangle> clipTriangleAgainstPlane(Triangle tri, double[] plane)
    {
        List<Point3D[]> insideVertices = new ArrayList<>();
        List<Point3D[]> outsideVertices = new ArrayList<>();

        Point3D[] points = tri.getTriangle();
        Point3D[] normals = tri.getNormals();

        //We divide the vertices into inside points and outside points, and store them in two separate lists
        for (int i = 0; i < 3; i++) 
        {
            Point3D p = points[i];
            Point3D n = normals[i];

            //We check if the point is inside or outside the plane
            if (isInsidePlane(p, plane)) 
            {
                insideVertices.add(new Point3D[]{p, n});
            } 
            else 
            {
                outsideVertices.add(new Point3D[]{p, n});
            }
        }

        //If all the points are inside, we keep the triangle as it is
        if (insideVertices.size() == 3) 
        {
            return List.of(tri);
        }
        //If we have a point inside and two outside, we create a new triangle
        else if (insideVertices.size() == 1 && outsideVertices.size() == 2) 
        {
            return List.of(clipOneInsideTwoOutside(insideVertices.get(0)[0], outsideVertices.get(0)[0], outsideVertices.get(1)[0], 
                                                   insideVertices.get(0)[1], outsideVertices.get(0)[1], outsideVertices.get(1)[1], plane));
        }
        //If we have two points inside and one outside, we create two new triangles
        else if (insideVertices.size() == 2 && outsideVertices.size() == 1) 
        {
            return clipTwoInsideOneOutside(insideVertices.get(0)[0], insideVertices.get(1)[0], outsideVertices.get(0)[0],
                                           insideVertices.get(0)[1], insideVertices.get(1)[1], outsideVertices.get(0)[1], plane);
        }
        //If all the points are outside, we delete the whole triangle
        else 
        {
            return new ArrayList<>();
        }
    }

    //Verify if a point is inside the frustum plane (on the positive side)
    private static boolean isInsidePlane(Point3D p, double[] plane) 
    {
        //We use the plane equation ax + by + cz + d = 0
        double result = plane[0] * p.getX() + plane[1] * p.getY() + plane[2] * p.getZ() + plane[3];
        return result >= 0;
    }

    //We create a new triangle if we have a point inside and two outside
    private static Triangle clipOneInsideTwoOutside(Point3D inside, Point3D outside1, Point3D outside2, 
                                                    Point3D normInside, Point3D normOutside1, Point3D normOutside2, double[] plane) 
    {
        //Check intersections
        Point3D[] intersection1 = getIntersection(inside, outside1, normInside, normOutside1, plane);
        Point3D[] intersection2 = getIntersection(inside, outside2, normInside, normOutside2, plane);

        Triangle t = new Triangle(inside, intersection1[0], intersection2[0], normInside, intersection1[1], intersection2[1]);
        //Ensure the points are ordered correctly
        if(!BackfaceCulling.isFrontFaced(t))
        {
            return new Triangle(inside, intersection2[0], intersection1[0], normInside, intersection2[1], intersection1[1]);
        }
        return t;
    }

    //We create two new triangles when two points are inside and one is outside 
    private static List<Triangle> clipTwoInsideOneOutside(Point3D inside1, Point3D inside2, Point3D outside, 
                                                          Point3D normInside1, Point3D normInside2, Point3D normOutside, double[] plane) 
    {
        // Check intersection
        Point3D[] intersection1 = getIntersection(inside1, outside, normInside1, normOutside, plane);
        Point3D[] intersection2 = getIntersection(inside2, outside, normInside2, normOutside, plane);

        Triangle t1 = new Triangle(inside1, intersection1[0], inside2, normInside1, intersection1[1], normInside2);
        Triangle t2 = new Triangle(inside2, intersection1[0], intersection2[0], normInside2, intersection1[1], intersection2[1]);

        //Ensure the points are ordered correctly
        if(!BackfaceCulling.isFrontFaced(t1))
        {
            t1 = new Triangle(inside1, inside2, intersection1[0], normInside1, normInside2, intersection1[1]);
        }
        if(!BackfaceCulling.isFrontFaced(t2))
        {
            t2 = new Triangle(inside2, intersection2[0], intersection1[0], normInside2, intersection2[1], intersection1[1]);
        }
        return List.of(t1, t2);
    }

    //Calculate the intersection point between a plane and two points (segment)
    private static Point3D[] getIntersection(Point3D p1, Point3D p2, Point3D n1, Point3D n2, double[] plane) 
    {
        double denominator = plane[0] * (p2.getX() - p1.getX()) + plane[1] * (p2.getY() - p1.getY()) + plane[2] * (p2.getZ() - p1.getZ());

        //Avoid division by zero if the denominator is too small (indicating parallelism)
        if (Math.abs(denominator) < 1e-6) 
        {
            return null; //No intersection, or the points are on the plane
        }

        double t = -(plane[0] * p1.getX() + plane[1] * p1.getY() + plane[2] * p1.getZ() + plane[3]) / denominator;

        //We return the new vertex and its normal
        Point3D newVertex = p1.add(p2.subtract(p1).multiply(t));
        Point3D newNormal = interpolatedNormalNewVertex(n1, n2, t);

        return new Point3D[]{newVertex, newNormal};
    }

    private static Point3D interpolatedNormalNewVertex(Point3D n1, Point3D n2, double t) 
    {
        double x = n1.getX() + (n2.getX() - n1.getX()) * t;
        double y = n1.getY() + (n2.getY() - n1.getY()) * t; 
        double z = n1.getZ() + (n2.getZ() - n1.getZ()) * t;

        double length = Math.sqrt(x * x + y * y + z * z);
        //Avoid division by zero if the denominator is too small (indicating parallelism)
        if (Math.abs(length) < 1e-6) 
        {
            return null; //No intersection, or the points are on the plane
        }
        //Normalize the new Vertex Normal
        return new Point3D(x / length, y / length, z / length);
    }   
}