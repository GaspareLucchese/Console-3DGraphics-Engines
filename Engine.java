import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Engine 
{
    /*Per le Rotazioni, da Sistemare*/
    private double fThetaX = Math.toRadians(0);
    private double fThetaY = Math.toRadians(0);
    private double fThetaZ = Math.toRadians(0);

    //negativo = verso sinistra, positivo = verso destra
    private double muoviX = 0;
    //negativo = verso il basso, positivo = verso l'alto
    private double muoviY = 0;
    //negativo = dietro l'osservatore, positivo = di fronte l'osservatore
    private double distance = 0;

    private double[][] matriceProspettiva = new double[4][4];
    private double[][] matriceRotX = new double[4][4];
    private double[][] matriceRotY = new double[4][4];
    private double[][] matriceRotZ = new double[4][4];

    public Engine()
    {
        //Azzeriamo le matrici
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                matriceProspettiva[i][j] = 0;
                matriceRotX[i][j] = 0;
                matriceRotY[i][j] = 0;
                matriceRotZ[i][j] = 0;
            }
        }

        //Impostiamo la matrice della prospettiva
        double fNear = 0.01;
        double fFar = 1000.0;
        double fFov = Math.toRadians(90.0);
        double fAspectRatio = (double)(Display.getDIMX()/Display.getDIMY());
        double fFovRad = 1.0/ Math.tan(fFov / 2);
        matriceProspettiva[0][0] = 1/(fAspectRatio * fFovRad);
        matriceProspettiva[1][1] = 1/(fFovRad);
        matriceProspettiva[2][2] = (-fFar * fNear) / (fFar - fNear);
        matriceProspettiva[2][3] = -(2*fFar * fNear) / (fFar - fNear);
        matriceProspettiva[3][2] = -1.0;
        matriceProspettiva[3][3] = 0;

        this.setMatRot();
        
    }

    public void setMovimento(double muoviX, double muoviY, double distance)
    {
        this.muoviX = muoviX;
        this.muoviY = muoviY;
        this.distance = distance;
    }

    //Calcoliamo la posizione dei nuovi punti nello spazio dopo la proiezione
    public void Proietta(Space spazio, Display Monitor, char monitor[][])
    {
        Punto3D normale, linea1, linea2, luce;
        normale = new Punto3D();
        linea1 = new Punto3D();
        linea2 = new Punto3D();
        //Gestiamo la direzione della luce (Sistemare?)
        luce = new Punto3D(0, 0, -1);
        //Normalizziamo il vettore luce
        double lum = Math.sqrt(luce.getX()*luce.getX() + luce.getY()*luce.getY() + luce.getZ()*luce.getZ());
        luce.setX(luce.getX()/lum);
        luce.setY(luce.getY()/lum);
        luce.setZ(luce.getZ()/lum);

        //Lavoriamo su una copia dello spazio in input, non sullo spazio stesso (sistemare?)
        Space copia = new Space();
        spazio.copiaSpazio(copia);
        //Impostiamo tutte le matrice di rotazione
        this.setMatRot();

        
        for(int l = 0; l < (spazio.getSpace()).size(); l++)
        {
            //Estraiamo ogni singolo contenuto nello spazio
            Triangolo tri = (spazio.getSpace()).get(l);
            Triangolo triRotated = new Triangolo();

            //Un'eventuale scale va applicato prima della rotazione! (rivedere)

            //Applichiamo le rotazioni ai singoli punti
            triRotated.setTriangolo(MoltiplicaMatrici2(tri.getTriangolo()[0], matriceRotX), MoltiplicaMatrici2(tri.getTriangolo()[1], matriceRotX), MoltiplicaMatrici2(tri.getTriangolo()[2], matriceRotX));
            triRotated.setTriangolo(MoltiplicaMatrici2(triRotated.getTriangolo()[0], matriceRotY), MoltiplicaMatrici2(triRotated.getTriangolo()[1], matriceRotY), MoltiplicaMatrici2(triRotated.getTriangolo()[2], matriceRotY));
            triRotated.setTriangolo(MoltiplicaMatrici2(triRotated.getTriangolo()[0], matriceRotZ), MoltiplicaMatrici2(triRotated.getTriangolo()[1], matriceRotZ), MoltiplicaMatrici2(triRotated.getTriangolo()[2], matriceRotZ));

            
            Triangolo triTranslated = new Triangolo(triRotated.getTriangolo()[0], triRotated.getTriangolo()[1], triRotated.getTriangolo()[2]);
            //Trasliamo il Triangolo (riv de necessaria la copia)
            //??controlliamo se la condizione nell'if è necessaria/da cambiare
            if(triTranslated.getTriangolo()[0].getZ()+distance > 0)
            {  
                triTranslated.getTriangolo()[0].setZ(triTranslated.getTriangolo()[0].getZ() + distance);
                triTranslated.getTriangolo()[0].setX(triTranslated.getTriangolo()[0].getX() - muoviX);
                triTranslated.getTriangolo()[0].setY(triTranslated.getTriangolo()[0].getY() + muoviY);
            }
            if(triTranslated.getTriangolo()[1].getZ()+distance > 0)
            {
                triTranslated.getTriangolo()[1].setZ(triTranslated.getTriangolo()[1].getZ() + distance);
                triTranslated.getTriangolo()[1].setX(triTranslated.getTriangolo()[1].getX() - muoviX);
                triTranslated.getTriangolo()[1].setY(triTranslated.getTriangolo()[1].getY() + muoviY);
            }
            if(triTranslated.getTriangolo()[2].getZ()+distance > 0)
            {
                triTranslated.getTriangolo()[2].setZ(triTranslated.getTriangolo()[2].getZ() + distance);
                triTranslated.getTriangolo()[2].setX(triTranslated.getTriangolo()[2].getX() - muoviX);
                triTranslated.getTriangolo()[2].setY(triTranslated.getTriangolo()[2].getY() + muoviY);
            }

            //Calcoliamo il prodotto vettoriale del triangolo...
            linea1.setX(triTranslated.getTriangolo()[1].getX() - triTranslated.getTriangolo()[0].getX());
            linea1.setY(triTranslated.getTriangolo()[1].getY() - triTranslated.getTriangolo()[0].getY());
            linea1.setZ(triTranslated.getTriangolo()[1].getZ() - triTranslated.getTriangolo()[0].getZ());

            linea2.setX(triTranslated.getTriangolo()[2].getX() - triTranslated.getTriangolo()[0].getX());
            linea2.setY(triTranslated.getTriangolo()[2].getY() - triTranslated.getTriangolo()[0].getY());
            linea2.setZ(triTranslated.getTriangolo()[2].getZ() - triTranslated.getTriangolo()[0].getZ());

            //... per ricavarci la normale (che normalizzeremo)
            normale.setX(linea1.getY()*linea2.getZ() - linea1.getZ()*linea2.getY());
            normale.setY(linea1.getZ()*linea2.getX() - linea1.getX()*linea2.getZ());
            normale.setZ(linea1.getX()*linea2.getY() - linea1.getY()*linea2.getX());
            double lung = Math.sqrt(normale.getX()*normale.getX() + normale.getY()*normale.getY() + normale.getZ()*normale.getZ());
            normale.setX(normale.getX()/lung);
            normale.setY(normale.getY()/lung);
            normale.setZ(normale.getZ()/lung);
    
            //Per ricavare il valore della luminosità applichiamo il prodotto scalare tra il vettore luce e il vettore normale del triangolo
            double luminosita = normale.getX()*luce.getX() + normale.getY()*luce.getY() + normale.getZ()*luce.getZ(); 

            //Applichiamo il backface-culling (prodotto scalare tra il vettore triangolo e la sua normale)
            if(normale.getX()*(triTranslated.getTriangolo()[0].getX()) + normale.getY()*(triTranslated.getTriangolo()[0].getY()) + normale.getZ()*(triTranslated.getTriangolo()[0].getZ()) < 0)
            {
                int nTriangoliClipping = 0;
                Triangolo[] Clipping = new Triangolo[2];

                //(pp Da togliere)
                Punto3D pp = new Punto3D(0, 0 ,0);
                Triangolo t1 = new Triangolo(pp, pp, pp), t2 = new Triangolo(pp, pp, pp);
                nTriangoliClipping = Triangolo_Clippato_Piano(new Punto3D(0,0,0.1), new Punto3D(0, 0, 1), triTranslated, t1, t2);
                Clipping[0] = t1;
                Clipping[1] = t2;

                //Ciclo per il Clipping
                for(int n = 0; n < nTriangoliClipping; n++)
                {
                    //Applichiamo la proiezione
                    Triangolo triProjected = new Triangolo(MoltiplicaMatrici(Clipping[n].getTriangolo()[0], matriceProspettiva), MoltiplicaMatrici(Clipping[n].getTriangolo()[1], matriceProspettiva), MoltiplicaMatrici(Clipping[n].getTriangolo()[2], matriceProspettiva));

                    /*Rivedere??? (Normalizziamo?)*/
                    if(true)
                    {
                        //Centriamo nello schermo e proporzioniamo in base alle dimensioni dello schermo
                        double centraX = 1;
                        double centraY = 1;
                        triProjected.getTriangolo()[0].setX(triProjected.getTriangolo()[0].getX() + centraX);
                        triProjected.getTriangolo()[0].setY(triProjected.getTriangolo()[0].getY() + centraY);
                        triProjected.getTriangolo()[1].setX(triProjected.getTriangolo()[1].getX() + centraX);
                        triProjected.getTriangolo()[1].setY(triProjected.getTriangolo()[1].getY() + centraY);
                        triProjected.getTriangolo()[2].setX(triProjected.getTriangolo()[2].getX() + centraX);
                        triProjected.getTriangolo()[2].setY(triProjected.getTriangolo()[2].getY() + centraY);
                        int w = Display.getDIMX();
                        int h = Display.getDIMY();
                        double b = 0.5;
               
                        triProjected.getTriangolo()[0].setX(triProjected.getTriangolo()[0].getX() * b * w);
                        triProjected.getTriangolo()[0].setY(triProjected.getTriangolo()[0].getY() * b * h);
                        triProjected.getTriangolo()[1].setX(triProjected.getTriangolo()[1].getX() * b * w);
                        triProjected.getTriangolo()[1].setY(triProjected.getTriangolo()[1].getY() * b * h);
                        triProjected.getTriangolo()[2].setX(triProjected.getTriangolo()[2].getX() * b * w);
                        triProjected.getTriangolo()[2].setY(triProjected.getTriangolo()[2].getY() * b * h);
                    }
                    //Inizializziamo per lo z-buffer(???)         
                    triProjected.getTriangolo()[0].setZ(Clipping[n].getTriangolo()[0].getZ());
                    triProjected.getTriangolo()[1].setZ(Clipping[n].getTriangolo()[0].getZ());
                    triProjected.getTriangolo()[2].setZ(Clipping[n].getTriangolo()[0].getZ());
    
                    //Aggiungiamo allo Spazio il nuovo Triangolo
                    Triangolo trilli = new Triangolo(triProjected.getTriangolo()[0], triProjected.getTriangolo()[1], triProjected.getTriangolo()[2]);
                    trilli.setLum(luminosita);
                    copia.aggiungiTriangolo(trilli);
                }
                
            }
        }

        
        
        List <Triangolo> listaTriangoli = copia.getSpace();
        //Riv non fa nulla
        Collections.sort(listaTriangoli, new Comparator<Triangolo>() 
        {
            @Override
            public int compare(Triangolo t1, Triangolo t2)
            {
                double z1 = (t1.getTriangolo()[0].getZ() + t1.getTriangolo()[1].getZ() + t1.getTriangolo()[2].getZ()) / 3.0f;
                double z2 = (t2.getTriangolo()[0].getZ() + t2.getTriangolo()[1].getZ() + t2.getTriangolo()[2].getZ()) / 3.0f;
                return Double.compare(z2, z1);
            }
        });
        
        //CLIPPING PER IL FRUSTUM (CONCLUDERE)
        /*
        ArrayList<Triangolo> Prova = new ArrayList<>();
        for(Triangolo Z : copia.getSpace())
        {
            Prova.add(Z);
        }  
        
        for(Triangolo T : Prova)
        {
        //Proiettiamo lo Spazio nel Monitor
        //Clippiamo anche verso i 4 bordi dello schermo
        Triangolo Clipped[] = new Triangolo[2];

        listaTriangoli.add(T);
        int nNuoviTriang = 1;

        for(int p = 0; p < 4; p++)
        {
            int nTriangDaAggiun = 0;
            while(nNuoviTriang > 0)
            {
                Triangolo test = listaTriangoli.get(0);
                listaTriangoli.remove(0);
                nNuoviTriang--;

                Triangolo t1 = new Triangolo(), t2 = new Triangolo();
                switch(p)
                {
                    case 0:
                        nTriangDaAggiun = Triangolo_Clippato_Piano(new Punto3D(0 , 0, 0), new Punto3D(0, 1, 0), test, t1, t2);
                        break;
                    case 1:
                        nTriangDaAggiun = Triangolo_Clippato_Piano(new Punto3D(0 , (double) Display.getDIMY()- 1, 0), new Punto3D(0, -1, 0), test, t1, t2);
                        break;
                    case 2:
                        nTriangDaAggiun = Triangolo_Clippato_Piano(new Punto3D(0 , 0, 0), new Punto3D(1, 0, 0), test, t1, t2);
                        break;
                    case 3:
                        nTriangDaAggiun = Triangolo_Clippato_Piano(new Punto3D((double) Display.getDIMX()- 1, 0, 0), new Punto3D(-1, 0, 0), test, t1, t2);
                        break;
                }
                Clipped[0] = t1;
                Clipped[1] = t2;

                for(int w = 0; w < nTriangDaAggiun; ++w)
                {
                    listaTriangoli.add(Clipped[w]);
                }
            }
            nNuoviTriang = listaTriangoli.size();
        }
    }
    */
    
        //Cambiamo lo spazio con i nuovi triangoli
        copia.setSpace(listaTriangoli);
        //Visualizziamo i punti sul display
        Disegna(Monitor, monitor, copia);
    }

    //Per le moltiplicazioni matriciali
    public Punto3D MoltiplicaMatrici(Punto3D i, double[][] m)
    {
        Punto3D o = new Punto3D();
        o.setX(((i.getX())*(m[0][0]) + (i.getY())*(m[0][1]) + (i.getZ())*(m[0][2]) + m[0][3]));
        o.setY(((i.getX())*(m[1][0]) + (i.getY())*(m[1][1]) + (i.getZ())*(m[1][2]) + m[1][3]));
        o.setZ(((i.getX())*(m[2][0]) + (i.getY())*(m[2][1]) + (i.getZ())*(m[2][2]) + m[2][3]));
        
        double w = (((i.getX())*(m[3][0]) + (i.getY())*(m[3][1]) + (i.getZ())*(m[3][2]) + m[3][3]));
        if(w != 1.0)
        {
            o.setX(o.getX()/w);
            o.setY(o.getY()/w);
            o.setZ(o.getZ()/w);
        }

        return o;
    }
    public Punto3D MoltiplicaMatrici2(Punto3D i, double[][] m)
    {
        Punto3D o = new Punto3D();
        o.setX(((i.getX())*(m[0][0]) + (i.getY())*(m[0][1]) + (i.getZ())*(m[0][2])));
        o.setY(((i.getX())*(m[1][0]) + (i.getY())*(m[1][1]) + (i.getZ())*(m[1][2])));
        o.setZ(((i.getX())*(m[2][0]) + (i.getY())*(m[2][1]) + (i.getZ())*(m[2][2])));
        
        return o;
    }

    public char[][] Disegna(Display Monitor, char[][] schermo, Space spazio)
    {
        //Z-buffer (da Sistemare???)
        double[][] Buffer = new double[Display.getDIMY()][Display.getDIMX()];
        for(int i = 0; i < Display.getDIMY(); i++)
        {
            for(int j = 0; j < Display.getDIMX(); j++)
            {
                //Inizializziamo il più lontano possibile
                Buffer[i][j] = 999999;
            }
        }

        for(int l = 0; l < (spazio.getSpace()).size(); l++)
        {
            Triangolo triTranslated = (spazio.getSpace()).get(l);
            //Verifichiamo che i vertici del triangolo siano all'interno del display, e siano i valori più piccoli del z-buffer (Riv?)
            if((int)Math.ceil(triTranslated.getTriangolo()[0].getX()) >= 0 && (int)Math.ceil(triTranslated.getTriangolo()[0].getX()) < Display.getDIMX() && (int)Math.ceil(triTranslated.getTriangolo()[0].getY()) >= 0 && (int)Math.ceil(triTranslated.getTriangolo()[0].getY()) < Display.getDIMY() && triTranslated.getTriangolo()[0].getZ() < Buffer[(int)Math.ceil(triTranslated.getTriangolo()[0].getY())][(int)Math.ceil(triTranslated.getTriangolo()[0].getX())])
            {
                schermo[(int)Math.ceil(triTranslated.getTriangolo()[0].getY())][(int)Math.ceil(triTranslated.getTriangolo()[0].getX())] = triTranslated.getLum();
            }
            
            if((int)Math.ceil(triTranslated.getTriangolo()[1].getX()) >= 0 && (int)Math.ceil(triTranslated.getTriangolo()[1].getX()) < Display.getDIMX() && (int)Math.ceil(triTranslated.getTriangolo()[1].getY()) >= 0 && (int)Math.ceil(triTranslated.getTriangolo()[1].getY()) < Display.getDIMY() && triTranslated.getTriangolo()[1].getZ() < Buffer[(int)Math.ceil(triTranslated.getTriangolo()[1].getY())][(int)Math.ceil(triTranslated.getTriangolo()[1].getX())])
            {
                schermo[(int)Math.ceil(triTranslated.getTriangolo()[1].getY())][(int)Math.ceil(triTranslated.getTriangolo()[1].getX())] = triTranslated.getLum();
            }

            if((int)Math.ceil(triTranslated.getTriangolo()[2].getX()) >= 0 && (int)Math.ceil(triTranslated.getTriangolo()[2].getX()) < Display.getDIMX() && (int)Math.ceil(triTranslated.getTriangolo()[2].getY()) >= 0 && (int)Math.ceil(triTranslated.getTriangolo()[2].getY()) < Display.getDIMY() && triTranslated.getTriangolo()[2].getZ() < Buffer[(int)Math.ceil(triTranslated.getTriangolo()[2].getY())][(int)Math.ceil(triTranslated.getTriangolo()[2].getX())])
            {
                schermo[(int)Math.ceil(triTranslated.getTriangolo()[2].getY())][(int)Math.ceil(triTranslated.getTriangolo()[2].getX())] = triTranslated.getLum();
            }

            //Aggiorniamo il monitor con i punti del vertice
            Monitor.setMonitor(schermo);
            //Rasterizziamo i triangoli
            Monitor.Rasterizza(triTranslated, Buffer);
        }
        
        /*
        for(int i = 0; i < Monitor.DIMY; i++)
        {
            for(int j = 0; j < Monitor.DIMX; j++)
            {
                System.out.print((int) Buffer[i][j]);
            }
            System.out.println();
        }
        */
        
        return Monitor.Monitor;
    }

    //Metodi setter e getter per le rotazioni
    public void setThetaX(double fThetaX)
    {
        this.fThetaX = Math.toRadians(fThetaX);
        this.setMatRot();
    }
    public void setThetaY(double fThetaY)
    {
        this.fThetaY = Math.toRadians(fThetaY);
        this.setMatRot();
    }
    public void setThetaZ(double fThetaZ)
    {
        this.fThetaZ = Math.toRadians(fThetaZ);
        this.setMatRot();
    }

    public double getThetaX()
    {
        return this.fThetaX;
    }
    public double getThetaY()
    {
        return this.fThetaY;
    }
    public double getThetaZ()
    {
        return this.fThetaZ;
    }

    //Per impostare le matrici di rotazioni
    public void setMatRot()
    {
        this.matriceRotX[0][0] =  1;
        this.matriceRotX[1][1] =  Math.cos(this.fThetaX);
        this.matriceRotX[1][2] = -Math.sin(this.fThetaX);
        this.matriceRotX[2][1] =  Math.sin(this.fThetaX); 
        this.matriceRotX[2][2] =  Math.cos(this.fThetaX);
        this.matriceRotX[3][3] =  1;

        this.matriceRotY[0][0] =  Math.cos(this.fThetaY);
        this.matriceRotY[0][2] =  Math.sin(this.fThetaY);
        this.matriceRotY[1][1] =  1;
        this.matriceRotY[2][0] = -Math.sin(this.fThetaY);
        this.matriceRotY[2][2] =  Math.cos(this.fThetaY);
        this.matriceRotY[3][3] =  1;

        this.matriceRotZ[0][0] =  Math.cos(this.fThetaZ);
        this.matriceRotZ[0][1] = -Math.sin(this.fThetaZ);
        this.matriceRotZ[1][0] =  Math.sin(this.fThetaZ);
        this.matriceRotZ[1][1] =  Math.cos(this.fThetaZ);
        this.matriceRotZ[2][2] =  1;
        this.matriceRotZ[3][3] =  1;
    }

    //???
    public Punto3D Vettore_IntersecaPiano(Punto3D Piano_p, Punto3D Piano_n, Punto3D LineaPart, Punto3D LineaFine)
    {
        double Norma_n = Math.sqrt(Piano_n.getX()*Piano_n.getX() + Piano_n.getY()*Piano_n.getY() + Piano_n.getZ()*Piano_n.getZ());
        Piano_n.setX(Piano_n.getX()/Norma_n);
        Piano_n.setY(Piano_n.getY()/Norma_n);
        Piano_n.setZ(Piano_n.getZ()/Norma_n);
        double Piano_d = -(Piano_n.getX()*Piano_p.getX() + Piano_n.getY()*Piano_p.getY() + Piano_n.getZ()*Piano_p.getZ());
        double ad = LineaPart.getX()*Piano_n.getX() + LineaPart.getY()*Piano_n.getY() + LineaPart.getZ()*Piano_n.getZ();
        double bd = LineaFine.getX()*Piano_n.getX() + LineaFine.getY()*Piano_n.getY() + LineaFine.getZ()*Piano_n.getZ();
        double t = (-Piano_d - ad) / (bd-ad);
        Punto3D LineaInizioFine = new Punto3D(LineaFine.getX() - LineaPart.getX(), LineaFine.getY() - LineaPart.getY(), LineaFine.getZ() - LineaPart.getZ());
        Punto3D LineaDaIntersecare = new Punto3D(t*LineaInizioFine.getX(), t*LineaInizioFine.getY(), t*LineaInizioFine.getZ());

        return new Punto3D(LineaPart.getX() + LineaDaIntersecare.getX(), LineaPart.getY() + LineaDaIntersecare.getY(), LineaPart.getZ() + LineaDaIntersecare.getZ());
    }

    //???
    public int Triangolo_Clippato_Piano(Punto3D Piano_p, Punto3D Piano_n, Triangolo in_tri, Triangolo out_tri1, Triangolo out_tri2)
    {
        //normalizziamo la normale del piano
        double Norma_n = Math.sqrt(Piano_n.getX()*Piano_n.getX() + Piano_n.getY()*Piano_n.getY() + Piano_n.getZ()*Piano_n.getZ());
        Piano_n.setX(Piano_n.getX()/Norma_n);
        Piano_n.setY(Piano_n.getY()/Norma_n);
        Piano_n.setZ(Piano_n.getZ()/Norma_n);

        Punto3D[] Punti_Interni = new Punto3D[3];
        Punto3D[] Punti_Esterni = new Punto3D[3];
        int nPuntiInt = 0, nPuntiExt = 0;

        //Calcoliamo le distanze punti-piano
        double dist0 = distanza(in_tri.getTriangolo()[0], Piano_n, Piano_p);
        double dist1 = distanza(in_tri.getTriangolo()[1], Piano_n, Piano_p);
        double dist2 = distanza(in_tri.getTriangolo()[2], Piano_n, Piano_p);

        if(dist0 >= 0)
        {
            Punti_Interni[nPuntiInt] = in_tri.getTriangolo()[0];
            nPuntiInt++;
        }
        else
        {
            Punti_Esterni[nPuntiExt] = in_tri.getTriangolo()[0];
            nPuntiExt++;
        }
        if(dist1 >= 0)
        {
            Punti_Interni[nPuntiInt] = in_tri.getTriangolo()[1];
            nPuntiInt++;
        }
        else
        {
            Punti_Esterni[nPuntiExt] = in_tri.getTriangolo()[1];
            nPuntiExt++;
        }
        if(dist2 >= 0)
        {
            Punti_Interni[nPuntiInt] = in_tri.getTriangolo()[2];
            nPuntiInt++;
        }
        else
        {
            Punti_Esterni[nPuntiExt] = in_tri.getTriangolo()[2];
            nPuntiExt++;
        }


        if(nPuntiInt == 0)
        {
            return 0;
        }
        if(nPuntiInt == 3)
        {
            out_tri1.setTriangolo(in_tri.getTriangolo()[0], in_tri.getTriangolo()[1], in_tri.getTriangolo()[2]);          
            return 1;
        }
        if (nPuntiInt == 1 && nPuntiExt == 2) 
        {
            Punto3D p0 = new Punto3D(Punti_Interni[0].getX(), Punti_Interni[0].getY(), Punti_Interni[0].getZ());
            Punto3D p1 = Vettore_IntersecaPiano(Piano_p, Piano_n, Punti_Interni[0], Punti_Esterni[0]); 
            Punto3D p2 = Vettore_IntersecaPiano(Piano_p, Piano_n, Punti_Interni[0], Punti_Esterni[1]);  
            out_tri1.setTriangolo(p0, p1, p2);
            return 1;
        }
        if(nPuntiInt == 2 && nPuntiExt == 1)
        {
            Punto3D p0 = new Punto3D(Punti_Interni[0].getX(), Punti_Interni[0].getY(), Punti_Interni[0].getZ());
            Punto3D p1 = new Punto3D(Punti_Interni[1].getX(), Punti_Interni[1].getY(), Punti_Interni[1].getZ());
            Punto3D p2 = Vettore_IntersecaPiano(Piano_p, Piano_n, Punti_Interni[0], Punti_Esterni[0]);         
            out_tri1.setTriangolo(p0, p1, p2);

            out_tri1.getTriangolo()[1].setX(p1.getX());
            out_tri1.getTriangolo()[1].setY(p1.getY());
            out_tri1.getTriangolo()[1].setZ(p1.getZ());

            out_tri2.getTriangolo()[0].setX(Punti_Interni[1].getX());
            out_tri2.getTriangolo()[0].setY(Punti_Interni[1].getY());
            out_tri2.getTriangolo()[0].setZ(Punti_Interni[1].getZ());
            
            out_tri2.getTriangolo()[0].setX(out_tri1.getTriangolo()[2].getX());
            out_tri2.getTriangolo()[0].setY(out_tri1.getTriangolo()[2].getY());
            out_tri2.getTriangolo()[0].setZ(out_tri1.getTriangolo()[2].getZ());
            p0 = p1;
            p1 = out_tri1.getTriangolo()[2];
            p2 = Vettore_IntersecaPiano(Piano_p, Piano_n, Punti_Interni[1], Punti_Esterni[0]);  
            out_tri2.setTriangolo(p0, p1, p2);
            return 2;
        }

        return 0;
    }

    //???
    public double distanza(Punto3D p, Punto3D Piano_n, Punto3D Piano_p)
    {
    ///////////////////////////?????normalizza
    return (Piano_n.getX()*p.getX() + Piano_n.getY()*p.getY() + Piano_n.getZ()*p.getZ() - (Piano_n.getX()*Piano_p.getX() + Piano_n.getY()*Piano_p.getY() + Piano_n.getZ()*Piano_p.getZ()));
    }
}

