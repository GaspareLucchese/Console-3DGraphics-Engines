import java.lang.String;

public class Display 
{
    //Dimensione dello schermo da visualizzare (costante)
    private static final int DIMX = 200;
    private static final int DIMY = 150;
    public char[][] Monitor = new char[DIMY][DIMX];

    public Display()
    {}

    public char[][] getMonitor()
    {
        return this.Monitor;
    }

    public void setMonitor(char[][] Monitor)
    {
        this.Monitor = Monitor;
    }

    public static int getDIMX()
    {
        return DIMX;
    }

    public static int getDIMY()
    {
        return DIMY;
    }


    //Svuotiamo la matrice contenente il display
    public void azzera()
    {
        for(int i = 0; i < DIMY; i++)
        {
            for(int j = 0; j < DIMX; j++)
            {
                Monitor[i][j] = ' ';
            }
        }
    }

    //Ogni singolo pixel viene aggiunto sullo schermo alla posizione corretta
    public void aggiungiPunto(int x, int y, char c)
    {
        this.Monitor[y][x] = c;
    }

    //Stampa in output tutti i pixel presenti in sullo schermo
    public void stampa()
    {
        //STAMPA PER CARATTERE???
        /*
        for(int i = 0; i < DIMY; i++)
        {
            boolean voidline = true;

            for(int j = 0; j < DIMX; j++)
            {
                if(Monitor[i][j] != ' ')
                {
                    voidline = false;
                }
            }
            if(voidline == true)
            {
                System.out.println();
            }
            else
            {
                for(int j = 0; j < DIMX; j++)
                {
                    System.out.print(Monitor[i][j]);
                    System.out.print(Monitor[i][j]);
                }
                System.out.println();
            }

        }
        */
        
        //STAMPA PER FRAME???
        String out = "";
        for(int i = 0; i < DIMY; i++)
        {
            for(int j = 0; j < DIMX; j++)
            {
               out = out.concat("" + Monitor[i][j] + Monitor[i][j]);
            }
            out += '\n';
        }
        System.out.println(out);
        
    }


    public void Rasterizza(Triangolo T, double [][] buf)
    {
        int ymin, ymax, xmin, xmax;

        //Gestiamo i punti come già dati ordinati in senso antiorario
        Punto3D p1 = T.getTriangolo()[0];
        Punto3D p2 = T.getTriangolo()[1];
        Punto3D p3 = T.getTriangolo()[2];

        ymin = (int) Math.min(Math.min(p1.getY(), p2.getY()), p3.getY());
        ymax = (int) Math.max(Math.max(p1.getY(), p2.getY()), p3.getY());

        xmin = (int) Math.min(Math.min(p1.getX(), p2.getX()), p3.getX());
        xmax = (int) Math.max(Math.max(p1.getX(), p2.getX()), p3.getX());
        
        //Iteriamo solo nel triangolo che contiene il triangolo
        for(int y = ymax; y >= ymin; y--)
        {
            for(int x = xmin; x <= xmax; x++)
            {
                Punto2D p = new Punto2D(x, y);
                
                //Il prodotto vettoriale ci permette di sapete se un determinato punto p è contenuto tra due vettori
                double prod1 = prodotto_vettoriale(p1, p2, p);
                double prod2 = prodotto_vettoriale(p2, p3, p);
                double prod3 = prodotto_vettoriale(p3, p1, p);

                //Non dovevano essere tutti e tre positivi????????
                //Se è contenuto in tutti e tre i vettori e non usciamo fuori dallo schermo (???) aggiungiamo il punto
                if(((prod1 <= 0) && (prod2 <= 0) && (prod3 <= 0)) && (((int)Math.ceil(x) >= 0) && ((int)Math.ceil(y) >= 0) && ((int)Math.ceil(x) < DIMX) && ((int)Math.ceil(y) < DIMY)))
                {
                    //Z-buffer da sistemare?????
                    double z = calcolaZ(p1, p2, p3, p);
                    if(z <= buf[y][x])
                    {
                        buf[y][x] = z;
                        aggiungiPunto(x, y, T.getLum());
                    }
                }
            }
        }
    }
    
    public double prodotto_vettoriale(Punto3D p1, Punto3D p2, Punto2D prova)
    {
        double ax = (p2.getX() - p1.getX());
        double ay = (p2.getY() - p1.getY());

        double bx = (prova.getX() - p1.getX());
        double by = (prova.getY() - p1.getY());

        return (ax*by - bx*ay);
    }

    //Ricava tutti i valori di z dall'equazione del piano
    public double calcolaZ(Punto3D vert1, Punto3D vert2, Punto3D vert3, Punto2D p)
    {
        //Equazione del piano passante per 3 punti
        double a = (vert2.getY() - vert1.getY())*(vert3.getZ() - vert1.getZ()) - (vert3.getY() - vert1.getY())*(vert2.getZ() - vert1.getZ());
        double b = (vert2.getZ() - vert1.getZ())*(vert3.getX() - vert1.getX()) - (vert3.getZ() - vert1.getZ())*(vert2.getX() - vert1.getX());
        double c = (vert2.getX() - vert1.getX())*(vert3.getY() - vert1.getY()) - (vert3.getX() - vert1.getX())*(vert2.getY() - vert1.getY());
        double d = -1*(a * vert1.getX() + b * vert2.getY() + c * vert3.getZ());

        return (-a * p.getX() -b * p.getY() -d) / c;
    }
}
