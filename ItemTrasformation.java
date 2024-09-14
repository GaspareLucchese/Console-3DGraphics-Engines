import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemTrasformation extends Thread
{
    public static void main(String[] args) 
    {
        //Object's Path (WSL)
        String path = "/mnt/c/users/lucch/Desktop/3D_Engine/Teapot.txt";
        //Object's Path (cmd / Visual Studio Code Terminal)
        //String path = "C:\\Users\\lucch\\Desktop\\3D_Engine\\Teapot.txt";

        //Creation of an ArrayList and a Space (ArrayList with Triangles) to memorize file's rows
        List<Point3D> vectors  = new ArrayList<>();
        Space faces = new Space();

        //Try-Catch to open and read the file
        try (BufferedReader buffer = new BufferedReader(new FileReader(new File(path)))) 
        {
            String line;
            //We read every line of file and add them to the Arraylist...
            while ((line = buffer.readLine()) != null) 
            {
                //...separating it in words
                String[] word = line.split(" ");
                //Add vectors
                if(word[0].equals("v"))
                {
                    vectors.add(new Point3D(Double.parseDouble(word[1]),Double.parseDouble(word[2]), Double.parseDouble(word[3])));
                }
                else if (word[0].equals("f")) //Add the triangles to the space to project
                {
                    faces.addTriangle(new Triangle(vectors.get(Integer.parseInt(word[1]) - 1),vectors.get(Integer.parseInt(word[2]) - 1),vectors.get(Integer.parseInt(word[3]) - 1)));
                }                
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        
        //AGGIORNAMENTO FRAMES TEIERA
        Display display = new Display();
        Engine newEngine = new Engine();
        newEngine.setMovement(0, -1, 5);
        for(int i = 0; i < 10000; i++)
        {
            display.reset();
            newEngine.setThetaX(-3*i);
            newEngine.setThetaY(3*i);
            newEngine.setThetaZ(3*i);
            newEngine.Projects(faces, display, display.getMonitor());
            
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            display.print();
            try
            {
                Thread.sleep(16); 
            } 
            catch(Exception e)
            {} 
        }
    
        
        //AGGIORNAMENTO FRAMES SCONCIO
        /* 
        Display display = new Display();
        Engine newEngine = new Engine();
        newEngine.distance = 155;
        newEngine.moveY = -25;
        newEngine.moveX = -40;
        for(int i = 0; i < 100; i++)
        {
            /*
            display.reset();
            newEngine.setThetaX(-30);
            newEngine.setThetaY(225);
            newEngine.setThetaZ(0);
            newEngine.Projects(faces, display, display.getMonitor());
            
            
            display.reset();
            newEngine.setThetaX(-15);
            newEngine.setThetaY(-5*i);
            newEngine.setThetaZ(0);
            newEngine.Projects(faces, display, display.getMonitor());
            
            
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            display.print();
            try
            {
                Thread.sleep(1); 
            } 
            catch(Exception e)
            {

            }
        }
            */
        
    }
}