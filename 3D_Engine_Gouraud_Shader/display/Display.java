package display;

//This class is used to represent the display screen
public class Display 
{
    //Screen size to render
    private static final int DIMX = 470;
    private static final int DIMY = 210;

    public char[][] Monitor = new char[DIMY][DIMX];
    public Display()
    {
        this.setMonitor(Monitor);
    }

    //Setters and Getters Methods
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

    //[TO-DO]: CHECK IF IT THIS IS THE BEST WAY TO PRINT A FRAME
    //Print all the the pixels on the frame (Monitor matrix)
    public void print() 
    {
        //We print the frame update a single frame line (not the whole frame)
        for(int i = 0; i < DIMY; i++)
        {
            //[TO-DO] Choose the better method
            StringBuilder frame = new StringBuilder();
            for(int j = 0; j < DIMX; j++)
            {
                frame.append(Monitor[i][j]).append(Monitor[i][j]);
            }
            System.out.println(frame);
        }

        // StringBuilder out = new StringBuilder();

        // for (int i = 0; i < DIMY; i++) 
        // {
        //     for (int j = 0; j < DIMX; j++) 
        //     {
        //         out.append(Monitor[i][j]).append(Monitor[i][j]);
        //     }
        //     out.append('\n');
        // }
        // System.out.print(out);
    }
}