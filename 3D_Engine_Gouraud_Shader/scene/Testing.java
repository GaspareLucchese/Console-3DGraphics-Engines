//       _o_                                   
//  ,-.'-----`.__ ;   -Developer: Gaspare Lucchese                    
// ((j`=======',-'    -Data Ultima Modifica: 22/11/2025     
//  `-\       /       -Descrizione: Used to test the engine and the display
//     `-===-'          

package scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.Engine;
import geometry.Point3D;
import display.Display;
import geometry.Triangle;
import geometryprocessing.Trasformation;

///This class is used to test the engine and the display
public class Testing extends Thread
{
    public static void main(String[] args) 
    {
        //Creation of two ArrayLists and a Mesh (Triangles' ArrayList) to memorize file's rows
        List<Point3D> vertices  = new ArrayList<>();
        //List<Point3D> vertexTextures = new ArrayList<>();
        List<Point3D> vertexNomals = new ArrayList<>();
        Mesh faces = new Mesh();
        //Flag for normals and textures check
        boolean hasNormals = false;
        boolean hasTexture = false;

        //Try-Catch to open and read the file with .obj notation
        try (BufferedReader buffer = new BufferedReader(new FileReader("scene/Teapot.obj")))
        {
            String line;
            //We read every line of file and add them to the Arraylist...
            while ((line = buffer.readLine()) != null) 
            {
                if (line.startsWith("v "))
                {
                    //...separating it in words
                    String[] word = line.split("\\s+");
                    //Add vertices (as Points)
                    vertices.add(new Point3D(Double.parseDouble(word[1]),Double.parseDouble(word[2]), Double.parseDouble(word[3])));
                }
                else if  (line.startsWith("vt "))
                {
                    //[TO-DO] Textures(?)
                    hasTexture = true;
                    // String[] word = line.split("\\s+");
                    // //Add vertex textures (as Points)
                    // vertexTextures.add(new Point3D(Double.parseDouble(word[1]),Double.parseDouble(word[2]), Double.parseDouble(word[3])));
                }
                if (line.startsWith("vn "))
                {
                    hasNormals = true;
                    String[] word = line.split("\\s+");
                    //Add vertex normals (as Points)
                    vertexNomals.add(new Point3D(Double.parseDouble(word[1]),Double.parseDouble(word[2]), Double.parseDouble(word[3])));
                }
                else if (line.startsWith("f")) 
                {
                    String[] word = line.split("\\s+");
                    
                    int[] v = new int[3];
                    //int[] vt = new int[3];
                    int[] vn = new int[3];

                    for (int i = 0; i < 3; i++)
                    {
                        //We can parse mesh faces on file in different ways, based on the flags
                        if (!hasNormals && !hasTexture)
                        {
                            // f v1 v2 v3
                            v[i] = Integer.parseInt(word[i + 1]) - 1;
                        }
                        else if (!hasNormals && hasTexture)
                        {
                            // f v1/vt1 v2/vt2 v3/vt3
                            String[] element = word[i + 1].split("/");
                            v[i] = Integer.parseInt(element[0]) - 1;
                            //vt[i] = Integer.parseInt(element[1]) - 1;
                        }
                        else if (hasNormals && !hasTexture)
                        {
                            // f v1//vn1 v2//vn2 v3//vn3
                            String[] element = word[i + 1].split("//");
                            v[i] = Integer.parseInt(element[0]) - 1;
                            vn[i] = Integer.parseInt(element[1]) - 1;
                        }
                        else if (hasNormals && hasTexture)
                        {
                            // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
                            String[] element = word[i + 1].split("/");
                            v[i] = Integer.parseInt(element[0]) - 1;
                            //vt[i] = Integer.parseInt(element[1]) - 1;
                            vn[i] = Integer.parseInt(element[2]) - 1;
                        }
                    }

                    if (hasNormals)
                    {
                        //If we find the vertex normals we can use them...
                        faces.addTriangle(new Triangle(
                        vertices.get(v[0]),vertices.get(v[1]),vertices.get(v[2]), 
                        vertexNomals.get(vn[0]), vertexNomals.get(vn[1]), vertexNomals.get(vn[2])
                        ));
                    }
                    else
                    {
                        //...else we save only the triangle vertices...
                        faces.addTriangle(new Triangle(
                        vertices.get(v[0]),vertices.get(v[1]),vertices.get(v[2])
                        ));                        
                    }
                }                
            }

            //... and we have to calculate the vertex normals
            if (!hasNormals)
            {
                //We group vertices by their 3D position (to avoid the creation of seams on the mesh)
                ////////////// This is necessary because multiple Point3D instances can represent the same position in space,
                //////////// but they are considered different objects. We want to compute a single averaged normal per position.
                Map<Point3D, List<Point3D>> grouped = new HashMap<>();
                for (Triangle t : faces.getMesh()) {
                    for (Point3D v : t.getTriangle()) {
                        //If the position is not yet in the map, add it with a new list
                        grouped.computeIfAbsent(v, key -> new ArrayList<>()).add(v);
                    }
                }

                ///////We sum all the normals values and count how many faces that share each vertex position.
                //We need to store the normals's sum of each triangle and the number of triangles that share a vertex position
                //We can use a HashMap to store the normals' sum and a HashMap to count the number of triangles
                Map<Point3D, Point3D> normalSum = new HashMap<>(); 
                Map<Point3D, Integer> normalCount = new HashMap<>();

                for (Triangle t : faces.getMesh()) 
                {
                    //Get the face normal for the current triangle.
                    Point3D faceNormal = t.getFaceNormal(); 

                    for (Point3D vertex : t.getTriangle()) 
                    {
                        //Find the key position in the group that matches this vertex's position.
                        for (Point3D key : grouped.keySet()) 
                        {
                            //If the current vertex matches the grouped key (so they share the same position in space)
                            if (key.equals(vertex)) 
                            {
                                //We add the normal to the sum.
                                normalSum.put(key, normalSum.getOrDefault(key, new Point3D(0, 0, 0)).add(faceNormal));
                                //Incrementing the count of faces linked to the vertex.
                                normalCount.put(key, normalCount.getOrDefault(key, 0) + 1);
                                break;
                            }
                        }
                    }
                }

                //Computing the averaged normal vector for each key position.
                Map<Point3D, Point3D> averageNormal = new HashMap<>();
                for (Point3D key : normalSum.keySet()) 
                {
                    //Divide the normals' sum by the number of triangles, then normalize the result.
                    averageNormal.put(key, normalSum.get(key).multiply(1.0 / normalCount.get(key)).normalized());
                }

                for (Triangle t : faces.getMesh()) 
                {
                    Point3D[] v = t.getTriangle();
                    Point3D[] n = new Point3D[3];

                    //Repeat for each of the 3 vertices of the triangle
                    for (int i = 0; i < 3; i++) 
                    {
                        //Iterate over all unique vertex positions 
                        for (Point3D key : grouped.keySet()) 
                        {
                            //If the current vertex matches the grouped key
                            if (key.equals(v[i])) 
                            {
                                //We assign the averaged normals to each vertex in each triangle.
                                n[i] = averageNormal.get(key);
                                break;
                            }
                        }
                    }

                    //We set the computed vertex normals to the triangle
                    t.setVertexNormals(n[0], n[1], n[2]);
                }
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        //ROTATING TEAPOT TEST
        Display display = new Display();
        Engine newEngine = new Engine();
        Trasformation trasformation = new Trasformation();

        trasformation.setMovement(0, -1, 4.3);
        for(int i = 0; i < 10000; i++)
        {
            display.reset();
            trasformation.setThetaX(-i);
            trasformation.setThetaY(i);
            trasformation.setThetaZ(i);
            newEngine.Rendering(faces, display, trasformation);
            
            System.out.print("\033[H");  
            System.out.flush();
            display.print();
            //Slowing-down the print
            try
            {
                Thread.sleep(16); 
            } 
            catch(Exception e)
            {
                e.printStackTrace();
            } 
        }  
    }
}