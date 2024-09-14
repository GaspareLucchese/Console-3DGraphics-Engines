import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OggettoTrasforma extends Thread
{
    public static void main(String[] args) 
    {
        //Percorso del file da aprire (WSL)
        String path = "/mnt/c/users/lucch/Desktop/3D_Engine/Teapot.txt";
        //Percorso del file da aprire (cmd)
        //String path = "C:\\Users\\lucch\\Desktop\\Labirinto_Cumulativo\\LabirintoCasuale\\Pene.txt";

        //Creazione di un ArrayList e di uno spazio (ArrayList di triangoli) per memorizzare le righe del file
        List<Punto3D> vettori  = new ArrayList<>();
        Space facce = new Space();

        //Try-Catch per aprire e leggere il file
        try (BufferedReader buffer = new BufferedReader(new FileReader(new File(path)))) 
        {
            String line;
            //Leggiamo ogni riga del file e la aggiungiamo all'ArrayList...
            while ((line = buffer.readLine()) != null) 
            {
                //...dividendola in parole
                String[] parola = line.split(" ");
                //Aggiungiamo i vettori
                if(parola[0].equals("v"))
                {
                    vettori.add(new Punto3D(Double.parseDouble(parola[1]),Double.parseDouble(parola[2]), Double.parseDouble(parola[3])));
                }
                else if (parola[0].equals("f")) //Aggiungiamo i triangoli allo spazio da proiettare
                {
                    facce.aggiungiTriangolo(new Triangolo(vettori.get(Integer.parseInt(parola[1]) - 1),vettori.get(Integer.parseInt(parola[2]) - 1),vettori.get(Integer.parseInt(parola[3]) - 1)));
                }                
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        //AGGIORNAMENTO FRAMES TEIERA

        Display display = new Display();
        Engine fuoco = new Engine();
        fuoco.setMovimento(0, -1, 5);
        for(int i = 0; i < 10000; i++)
        {
            display.azzera();
            fuoco.setThetaX(-3*i);
            fuoco.setThetaY(3*i);
            fuoco.setThetaZ(3*i);
            fuoco.Proietta(facce, display, display.getMonitor());
            
            //System.out.print("\033[H\033[2J");  
            System.out.flush();
            display.stampa();
            try
            {
                Thread.sleep(1); 
            } 
            catch(Exception e)
            {} 
        }
        
        //AGGIORNAMENTO FRAMES PENE
        /*
        Display display = new Display();
        Engine fuoco = new Engine();
        fuoco.distance = 155;
        fuoco.muoviY = -25;
        fuoco.muoviX = -40;
        for(int i = 0; i < 100; i++)
        {
            display.azzera();
            fuoco.setThetaX(-30);
            fuoco.setThetaY(225);
            fuoco.setThetaZ(0);
            fuoco.Proietta(facce, display, display.getMonitor());
            
            
            display.azzera();
            fuoco.setThetaX(-15);
            fuoco.setThetaY(-5*i);
            fuoco.setThetaZ(0);
            fuoco.Proietta(facce, display, display.getMonitor());
            
            
            //System.out.print("\033[H\033[2J");  
            System.out.flush();
            display.stampa();
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