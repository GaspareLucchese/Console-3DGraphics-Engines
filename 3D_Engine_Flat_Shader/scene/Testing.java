//       _o_                                   
//  ,-.'-----`.__ ;   -Developer: Gaspare Lucchese                    
// ((j`=======',-'    -Data Ultima Modifica: 22/05/2025     
//  `-\       /       -Descrizione: Used to test the engine and the display
//     `-===-'          

package scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

        //Creation of an ArrayList and a Mesh (Triangles' ArrayList) to memorize file's rows
        List<Point3D> vectors  = new ArrayList<>();
        Mesh faces = new Mesh();

        //Try-Catch to open and read the file
        try (BufferedReader buffer = new BufferedReader(new FileReader("scene/Teapot.txt")))
        {
            String line;
            //We read every line of file and add them to the Arraylist...
            while ((line = buffer.readLine()) != null) 
            {
                //...separating it in words
                String[] word = line.split(" ");
                if(word[0].equals("v"))
                {
                    //Add vectors (Points)
                    vectors.add(new Point3D(Double.parseDouble(word[1]),Double.parseDouble(word[2]), Double.parseDouble(word[3])));
                }
                else if (word[0].equals("f")) 
                {
                    //Add triangles to the Mesh to project
                    faces.addTriangle(new Triangle(vectors.get(Integer.parseInt(word[1]) - 1),vectors.get(Integer.parseInt(word[2]) - 1),vectors.get(Integer.parseInt(word[3]) - 1)));
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