package scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
        //Load vertices and faces from a .txt file with .obj notation 
        InputStream path = Testing.class.getClassLoader().getResourceAsStream("scene/Teapot.txt");

        if (path == null) {
            System.err.println("File not found in classpath!");
            return;
        }

        //Creation of two ArrayLists and a Mesh (Triangles' ArrayList) to memorize file's rows
        List<Point3D> vertices  = new ArrayList<>();
        //List<Point3D> vertexTextures = new ArrayList<>();
        List<Point3D> vertexNomals = new ArrayList<>();
        Mesh faces = new Mesh();
        //Flag for normals and textures check
        boolean hasNormals = false;
        boolean hasTexture = false;

        //Try-Catch to open and read the file
        try (BufferedReader buffer = new BufferedReader(new FileReader("scene/Teapot.txt")))
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
            if(!hasNormals)
            {
                //We need to store the normals of each triangle and the number of triangles that share a vertex
                //We can use a HashMap to store the normals and a HashMap to count the number of triangles
                Map<Point3D, Point3D> normalMap = new HashMap<>();
                Map<Point3D, Integer> normalCount = new HashMap<>();

                for (Triangle t : faces.getMesh())
                {
                    Point3D faceNormal = t.getFaceNormal();

                    for (Point3D vertex : t.getTriangle())
                    {
                        //The normal of the vertex is the average of the normals of the triangles that share it
                        normalMap.put(vertex, normalMap.getOrDefault(vertex, new Point3D(0, 0, 0)).add(faceNormal));
                        normalCount.put(vertex, normalCount.getOrDefault(vertex, 0) + 1);
                    }
                }

                for (Triangle t : faces.getMesh())
                {
                    //[TO-DO] See if normalized() is needed
                    Point3D n1 = (normalMap.get(t.getTriangle()[0]).multiply(1.0 / normalCount.get(t.getTriangle()[0]))).normalized();
                    Point3D n2 = (normalMap.get(t.getTriangle()[1]).multiply(1.0 / normalCount.get(t.getTriangle()[1]))).normalized();
                    Point3D n3 = (normalMap.get(t.getTriangle()[2]).multiply(1.0 / normalCount.get(t.getTriangle()[2]))).normalized();
                    t.setVertexNormals(n1, n2, n3);
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

        trasformation.setMovement(0, -1, 4.4);
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