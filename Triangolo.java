public class Triangolo 
{
    private double lumin = 0;
    private char osita = ' ';
    public Punto3D vuoto = new Punto3D(0, 0, 0);

    public Triangolo()
    {
         this.setTriangolo(vuoto, vuoto, vuoto);
    }

    private Punto3D p1;
    private Punto3D p2;
    private Punto3D p3;

    //Il Triangolo è un insieme di tre punti (vertici)
    public Triangolo(Punto3D p1, Punto3D p2, Punto3D p3)
    {
        this.setTriangolo(p1, p2, p3);
    }

    public void setTriangolo(Punto3D p1, Punto3D p2, Punto3D p3)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Punto3D[] getTriangolo()
    {
        Punto3D[] tris = new Punto3D[]{p1, p2, p3};
        return tris;
    }
   
    public String toString()
    {
        return ("(" + p1 + p2 + p3 + ")");
    }

    //Metodi setter e getter per gestire la luminosità e la sua visualizzazione
    public void setLum(double lum)
    {
        this.osita = getOmbra(lum);
    }
    public double getL()
    {
        return this.lumin;
    }
    public char getLum()
    {
        return this.osita;
    }
    
    //Convertiamo la luminosità in caratteri ascii per la stampa
    public char getOmbra(double lumi)
    {
        int pixel = (int) (11*lumi);

        switch(pixel)
        {
            case 0: 
                return '.';
            case 1: 
                return ',';
            case 2: 
                return '-';
            case 3: 
                return '~';
            case 4: 
                return ':';
            case 5: 
                return ';';
            case 6: 
                return '=';
            case 7: 
                return '!';
            case 8: 
                return '*';
            case 9: 
                return '#';
            case 10: 
                return '$';
            case 11: 
                return '@';
            default:
                return '.';
        }
    }
}
