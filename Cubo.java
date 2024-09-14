public class Cubo
{
    public static void main(String[] args) 
    {
        Space s = new Space();
        double[][][] valori = {
    
            
            //HGBE
            
            {{-1.0 , -1.0 , -1.0},  {-1.0 ,  1.0 , -1.0},  { 1.0 ,  1.0 , -1.0}},
            {{-1.0 , -1.0 , -1.0},  { 1.0 ,  1.0 , -1.0},  { 1.0 , -1.0 , -1.0}},

            //EBAC
            {{ 1.0 , -1.0 , -1.0},  { 1.0 ,  1.0 , -1.0},  { 1.0 ,  1.0 ,  1.0}},
            {{ 1.0 , -1.0 , -1.0},  { 1.0 ,  1.0 ,  1.0},  { 1.0 , -1.0 ,  1.0}},
            
            //CADF
            {{ 1.0 , -1.0 ,  1.0},  { 1.0 ,  1.0 ,  1.0},  {-1.0 ,  1.0 ,  1.0}},
            {{ 1.0 , -1.0 ,  1.0},  {-1.0 ,  1.0 ,  1.0},  {-1.0 , -1.0 ,  1.0}},

            
            //FDGH
            {{-1.0 , -1.0 ,  1.0},  {-1.0 ,  1.0 ,  1.0},  {-1.0 ,  1.0 , -1.0}},
            {{-1.0 , -1.0 ,  1.0},  {-1.0 ,  1.0 , -1.0},  {-1.0 , -1.0 , -1.0}},
            
            //GDAB
            {{-1.0 ,  1.0 , -1.0},  { -1.0 ,  1.0 , 1.0},  { 1.0 ,  1.0 ,  1.0}},
            {{-1.0 ,  1.0 , -1.0},  { 1.0 ,  1.0 ,  1.0},  { 1.0 ,  1.0 , -1.0}},

            //CFHE
            {{ 1.0 , -1.0 ,  1.0},  {-1.0 , -1.0 ,  1.0},  {-1.0 , -1.0 , -1.0}},
            {{ 1.0 , -1.0 ,  1.0},  {-1.0 , -1.0 , -1.0},  { 1.0 , -1.0 , -1.0}}
            
        };
    
        //Creazione del punto partendo dai valori...
        Punto3D punti[][] = new Punto3D[12][3];
        for(int l = 0; l < 12; ++l)
        {
            for (int m = 0; m < 3; ++m)
            {
                Punto3D temp = new Punto3D(valori[l][m][0], valori[l][m][1], valori[l][m][2]);
                punti[l][m] = temp;
            }
        }
    
        //...E creazione dei triangoli a partire dei punti, che verranno aggiunti allo spazio
        for(int k = 0; k < 12; ++k)
        {
            //if(Math.min(Math.min(Math.floor(punti[k][0].getZ()+Engine.distance), Math.floor(punti[k][1].getZ()+Engine.distance)), Math.floor(punti[k][2].getZ()+Engine.distance)) < 0)
            
            Triangolo t = new Triangolo(punti[k][0], punti[k][1], punti[k][2]);
            s.aggiungiTriangolo(t);
            
        }
        Engine e = new Engine();
        Display d = new Display();

        e.setMovimento(0, 0, 10);

        for(int i = 0; i < 10000; i++)
        {
            d.azzera();
            //e.setThetaX(5*i);
            e.setThetaY(5*i);
            //e.setThetaZ(5*i);
            e.Proietta(s, d, d.getMonitor());
            
            //System.out.print("\033[H\033[2J");  
            System.out.flush();
            d.stampa();
            try
            {
                Thread.sleep(1); 
            } 
            catch(Exception exc)
            {}
        }
    }
}
