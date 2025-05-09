package display;

public class Display 
{
    //Screen size to render
    private static final int DIMX = 375;
    private static final int DIMY = 211;

    public char[][] Monitor = new char[DIMY][DIMX];
    public Display()
    {
        this.setMonitor(Monitor);
    }

    public void setMonitor(char[][] Monitor)
    {
        this.Monitor = Monitor;
    }

    public char[][] getMonitor()
    {
        return this.Monitor;
    }

    public static int getDIMX()
    {
        return DIMX;
    }

    public static int getDIMY()
    {
        return DIMY;
    }

    //Empty the display matrix
    public void reset()
    {
        for(int i = 0; i < DIMY; i++)
        {
            for(int j = 0; j < DIMX; j++)
            {
                Monitor[i][j] = ' ';
            }
        }
    }

    //Every pixel is added on the display matrix to the right position
    public void addPoint(int x, int y, char c)
    {
        this.Monitor[y][x] = c;
    }

    
    //[?]
    //Print all the the pixels on the monitor matrix
    public void print()
    {
        //STAMPA PER FRAME???
        
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < DIMY; i++)
        {
            for(int j = 0; j < DIMX; j++)
            {
                out.append(Monitor[i][j]).append(Monitor[i][j]);
            }
        out.append('\n');
        }
        System.out.println(out);
        
    }
}
