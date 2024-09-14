import java.util.ArrayList;
import java.util.List;

public class Space
{
	//Lo spazio è un insieme di poligoni (triangoli)
    private List<Triangolo> Spazio = new ArrayList<>();

    public void aggiungiTriangolo(Triangolo triangolo)
    {
        this.Spazio.add(triangolo);
    }

	public void modificaTriangolo(int i, Triangolo triangolo)
    {
        this.Spazio.set(i, triangolo);
    }

	//Metodo setter per la sua modifica
    public void setSpace(List<Triangolo> Spazio)
    {
        this.Spazio = Spazio;
    }

    public List<Triangolo> getSpace()
    {
        return this.Spazio;
    }

	//Copiamo tutti i triangoli presenti nello spazio in un'altra destinazione
    public void copiaSpazio(Space nuovo)
    {
        for(int i = 0; i < this.Spazio.size(); i++)
		{
			nuovo.Spazio.add(i, (this.getSpace()).get(i));
		}
    }

	//Per il debug
    public void Stampa()
    {
        for(int i = 0; i < (this.getSpace()).size(); i++)
        {
            System.out.println((this.getSpace()).get(i));
        }
    }

	//Utile per creare le mura del labirinto nello spazio
    public Space creaSolido(char[][] maze, Posizione pp)
	{
		Space solido = new Space();

		for(int i = (maze.length - 3); i > 1 ; i--)
		{
			for(int j = ((maze[0].length) - 3); j > 1; j--)
			{
				if(maze[i][j] == '*')
				{
					int xplus = 2*((- j + pp.x));
                    int zplus = 2*((- i + pp.y));

					//Valori predefiniti per un parallelepipedo
					double[][][] valori = {

						{{-1.0 , -2.0 , -1.0},  {-1.0 ,  1.0 , -1.0},  { 1.0 ,  1.0 , -1.0}},
            			{{-1.0 , -2.0 , -1.0},  { 1.0 ,  1.0 , -1.0},  { 1.0 , -2.0 , -1.0}},

            			{{ 1.0 , -2.0 , -1.0},  { 1.0 ,  1.0 , -1.0},  { 1.0 ,  1.0 ,  1.0}},
            			{{ 1.0 , -2.0 , -1.0},  { 1.0 ,  1.0 ,  1.0},  { 1.0 , -2.0 ,  1.0}},

            			{{ 1.0 , -2.0 ,  1.0},  { 1.0 ,  1.0 ,  1.0},  {-1.0 ,  1.0 ,  1.0}},
            			{{ 1.0 , -2.0 ,  1.0},  {-1.0 ,  1.0 ,  1.0},  {-1.0 , -2.0 ,  1.0}},

            			{{-1.0 , -2.0 ,  1.0},  {-1.0 ,  1.0 ,  1.0},  {-1.0 ,  1.0 , -1.0}},
            			{{-1.0 , -2.0 ,  1.0},  {-1.0 ,  1.0 , -1.0},  {-1.0 , -2.0 , -1.0}},

            			{{-1.0 ,  1.0 , -1.0},  {-1.0 ,  1.0 ,  1.0},  { 1.0 ,  1.0 ,  1.0}},
            			{{-1.0 ,  1.0 , -1.0},  { 1.0 ,  1.0 ,  1.0},  { 1.0 ,  1.0 , -1.0}},

            			{{ 1.0 , -2.0 ,  1.0},  {-1.0 , -2.0 ,  1.0},  {-1.0 , -2.0 , -1.0}},
            			{{ 1.0 , -2.0 ,  1.0},  {-1.0 , -2.0 , -1.0},  { 1.0 , -2.0 , -1.0}}
					};

					//Creazione del punto partendo dai valori...
					Punto3D punti[][] = new Punto3D[12][3];
        			for(int l = 0; l < 12; l++)
        			{
            			for (int m = 0; m < 3; m++)
            			{
                			Punto3D temp = new Punto3D(valori[l][m][0] + xplus, valori[l][m][1], valori[l][m][2] + zplus);
                			punti[l][m] = temp;
            			}
        			}

					//...E creazione dei triangoli a partire dei punti, che verranno aggiunti allo spazio
					for(int k = 0; k < 12; k++)
        			{
						//if(Math.min(Math.min(Math.floor(punti[k][0].getZ()+Engine.distance), Math.floor(punti[k][1].getZ()+Engine.distance)), Math.floor(punti[k][2].getZ()+Engine.distance)) < 0)
            			Triangolo tempo = new Triangolo(punti[k][0], punti[k][1], punti[k][2]);
                		solido.aggiungiTriangolo(tempo);
        			}
				}
			}
		}

		return solido;
	}

}

