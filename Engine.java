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
    private double moveX = 0;
    //negativo = verso il basso, positivo = verso l'alto
    private double moveY = 0;
    //negativo = dietro l'osservatore, positivo = di fronte l'osservatore
    private double distance = 0;

    private double[][] matrixPerspective = new double[4][4];
    private double[][] matrixRotX = new double[4][4];
    private double[][] matrixRotY = new double[4][4];
    private double[][] matrixRotZ = new double[4][4];

    public Engine()
    {
        //Reset all the matrices
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                matrixPerspective[i][j] = 0;
                matrixRotX[i][j] = 0;
                matrixRotY[i][j] = 0;
                matrixRotZ[i][j] = 0;
            }
        }

        //Set the perspective matrix
        double fNear = 0.01;
        double fFar = 1000.0;
        double fFov = Math.toRadians(90.0);
        double fAspectRatio = (double)(Display.getDIMX()/Display.getDIMY());
        double fFovRad = 1.0/ Math.tan(fFov / 2);
        matrixPerspective[0][0] = 1/(fAspectRatio * fFovRad);
        matrixPerspective[1][1] = 1/(fFovRad);
        matrixPerspective[2][2] = (-fFar * fNear) / (fFar - fNear);
        matrixPerspective[2][3] = -(2*fFar * fNear) / (fFar - fNear);
        matrixPerspective[3][2] = -1.0;
        matrixPerspective[3][3] = 0;

        this.setMatrixRot();
        
    }

    public void setMovement(double moveX, double moveY, double distance)
    {
        this.moveX = moveX;
        this.moveY = moveY;
        this.distance = distance;
    }

    //Calculate the position of new points in the space after the projection
    public void Projects(Space space, Display Monitor, char monitor[][])
    {
        Point3D normal, line1, line2, light;
        normal = new Point3D();
        line1 = new Point3D();
        line2 = new Point3D();
        //Gestiamo la direzione della luce (Sistemare?)
        light = new Point3D(0, 0, -1);
        //Normalize the lighting vector
        double lum = Math.sqrt(light.getX()*light.getX() + light.getY()*light.getY() + light.getZ()*light.getZ());
        light.setX(light.getX()/lum);
        light.setY(light.getY()/lum);
        light.setZ(light.getZ()/lum);

        //We can work on a copy of the space from input, so we can do not modify the space itself (SISTEMARE ?)
        Space copy = new Space();
        space.copySpace(copy);
        //Set all the rotation matrices
        this.setMatrixRot();

        
        for(int l = 0; l < (space.getSpace()).size(); l++)
        {
            //We extract all the tringles in the space
            Triangle tri = (space.getSpace()).get(l);
            Triangle triRotated = new Triangle();

            //Un'eventuale scale va applicato prima della rotazione! (rivedere)

            //We can apply the rotation to individual points
            triRotated.setTriangle(MatrixMultiplication2(tri.getTriangle()[0], matrixRotX), MatrixMultiplication2(tri.getTriangle()[1], matrixRotX), MatrixMultiplication2(tri.getTriangle()[2], matrixRotX));
            triRotated.setTriangle(MatrixMultiplication2(triRotated.getTriangle()[0], matrixRotY), MatrixMultiplication2(triRotated.getTriangle()[1], matrixRotY), MatrixMultiplication2(triRotated.getTriangle()[2], matrixRotY));
            triRotated.setTriangle(MatrixMultiplication2(triRotated.getTriangle()[0], matrixRotZ), MatrixMultiplication2(triRotated.getTriangle()[1], matrixRotZ), MatrixMultiplication2(triRotated.getTriangle()[2], matrixRotZ));

            
            Triangle triTranslated = new Triangle(triRotated.getTriangle()[0], triRotated.getTriangle()[1], triRotated.getTriangle()[2]);
            //Trasliamo il Triangolo (riv se necessaria la copia)
            //??controlliamo se la condizione nell'if è necessaria/da cambiare
            if(triTranslated.getTriangle()[0].getZ()+distance > 0)
            {  
                triTranslated.getTriangle()[0].setZ(triTranslated.getTriangle()[0].getZ() + distance);
                triTranslated.getTriangle()[0].setX(triTranslated.getTriangle()[0].getX() - moveX);
                triTranslated.getTriangle()[0].setY(triTranslated.getTriangle()[0].getY() + moveY);
            }
            if(triTranslated.getTriangle()[1].getZ()+distance > 0)
            {
                triTranslated.getTriangle()[1].setZ(triTranslated.getTriangle()[1].getZ() + distance);
                triTranslated.getTriangle()[1].setX(triTranslated.getTriangle()[1].getX() - moveX);
                triTranslated.getTriangle()[1].setY(triTranslated.getTriangle()[1].getY() + moveY);
            }
            if(triTranslated.getTriangle()[2].getZ()+distance > 0)
            {
                triTranslated.getTriangle()[2].setZ(triTranslated.getTriangle()[2].getZ() + distance);
                triTranslated.getTriangle()[2].setX(triTranslated.getTriangle()[2].getX() - moveX);
                triTranslated.getTriangle()[2].setY(triTranslated.getTriangle()[2].getY() + moveY);
            }

            //Calcolutate the triangle's cross product...
            line1.setX(triTranslated.getTriangle()[1].getX() - triTranslated.getTriangle()[0].getX());
            line1.setY(triTranslated.getTriangle()[1].getY() - triTranslated.getTriangle()[0].getY());
            line1.setZ(triTranslated.getTriangle()[1].getZ() - triTranslated.getTriangle()[0].getZ());

            line2.setX(triTranslated.getTriangle()[2].getX() - triTranslated.getTriangle()[0].getX());
            line2.setY(triTranslated.getTriangle()[2].getY() - triTranslated.getTriangle()[0].getY());
            line2.setZ(triTranslated.getTriangle()[2].getZ() - triTranslated.getTriangle()[0].getZ());

            //... to obtain the normal (that we should normalize)
            normal.setX(line1.getY()*line2.getZ() - line1.getZ()*line2.getY());
            normal.setY(line1.getZ()*line2.getX() - line1.getX()*line2.getZ());
            normal.setZ(line1.getX()*line2.getY() - line1.getY()*line2.getX());

            double lung = Math.sqrt(normal.getX()*normal.getX() + normal.getY()*normal.getY() + normal.getZ()*normal.getZ());
            normal.setX(normal.getX()/lung);
            normal.setY(normal.getY()/lung);
            normal.setZ(normal.getZ()/lung);
    
            //To obtain the brightness value we should apply the dot product between the light vector and the triangle's normal vector 
            double brightness_value = normal.getX()*light.getX() + normal.getY()*light.getY() + normal.getZ()*light.getZ(); 

            //Apply the Backface-culling (dot product between the triangle's vector and its normal)
            //Applichiamo il backface-culling (prodotto scalare tra il vettore triangolo e la sua normal)
            if(normal.getX()*(triTranslated.getTriangle()[0].getX()) + normal.getY()*(triTranslated.getTriangle()[0].getY()) + normal.getZ()*(triTranslated.getTriangle()[0].getZ()) < 0)
            {
                int nTriangoliClipping = 0;
                Triangle[] Clipping = new Triangle[2];

                //(p Da togliere)
                Point3D p = new Point3D(0, 0 ,0);
                Triangle t1 = new Triangle(p, p, p), t2 = new Triangle(p, p, p);
                nTriangoliClipping = Triangle_Clipped_Plane(new Point3D(0,0,0.1), new Point3D(0, 0, 1), triTranslated, t1, t2);
                Clipping[0] = t1;
                Clipping[1] = t2;

                //Cicle for Clipping
                for(int n = 0; n < nTriangoliClipping; n++)
                {
                    //Apply the projection
                    Triangle triProjected = new Triangle(MatrixMultiplication(Clipping[n].getTriangle()[0], matrixPerspective), MatrixMultiplication(Clipping[n].getTriangle()[1], matrixPerspective), MatrixMultiplication(Clipping[n].getTriangle()[2], matrixPerspective));

                    /*Rivedere??? (Normalizziamo?)*/
                    if(true)
                    {
                        //Center in the screen and scale based on the screen size
                        double centerX = 1;
                        double centerY = 1;
                        triProjected.getTriangle()[0].setX(triProjected.getTriangle()[0].getX() + centerX);
                        triProjected.getTriangle()[0].setY(triProjected.getTriangle()[0].getY() + centerY);
                        triProjected.getTriangle()[1].setX(triProjected.getTriangle()[1].getX() + centerX);
                        triProjected.getTriangle()[1].setY(triProjected.getTriangle()[1].getY() + centerY);
                        triProjected.getTriangle()[2].setX(triProjected.getTriangle()[2].getX() + centerX);
                        triProjected.getTriangle()[2].setY(triProjected.getTriangle()[2].getY() + centerY);
                        int w = Display.getDIMX();
                        int h = Display.getDIMY();
                        double b = 0.5;
               
                        triProjected.getTriangle()[0].setX(triProjected.getTriangle()[0].getX() * b * w);
                        triProjected.getTriangle()[0].setY(triProjected.getTriangle()[0].getY() * b * h);
                        triProjected.getTriangle()[1].setX(triProjected.getTriangle()[1].getX() * b * w);
                        triProjected.getTriangle()[1].setY(triProjected.getTriangle()[1].getY() * b * h);
                        triProjected.getTriangle()[2].setX(triProjected.getTriangle()[2].getX() * b * w);
                        triProjected.getTriangle()[2].setY(triProjected.getTriangle()[2].getY() * b * h);
                    }

                    //Inizializziamo per lo z-buffer(???)         
                    triProjected.getTriangle()[0].setZ(Clipping[n].getTriangle()[0].getZ());
                    triProjected.getTriangle()[1].setZ(Clipping[n].getTriangle()[0].getZ());
                    triProjected.getTriangle()[2].setZ(Clipping[n].getTriangle()[0].getZ());
    
                    //Add the new triangle in the space
                    Triangle tri_temp = new Triangle(triProjected.getTriangle()[0], triProjected.getTriangle()[1], triProjected.getTriangle()[2]);
                    tri_temp.setBrightness_char(brightness_value);
                    copy.addTriangle(tri_temp);
                }
                
            }
        }

        
        
        List <Triangle> triangleList = copy.getSpace();
        //Riv non fa nulla???
        Collections.sort(triangleList, new Comparator<Triangle>() 
        {
            @Override
            public int compare(Triangle t1, Triangle t2)
            {
                double z1 = (t1.getTriangle()[0].getZ() + t1.getTriangle()[1].getZ() + t1.getTriangle()[2].getZ()) / 3.0f;
                double z2 = (t2.getTriangle()[0].getZ() + t2.getTriangle()[1].getZ() + t2.getTriangle()[2].getZ()) / 3.0f;
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
        //Proiettiamo lo space nel Monitor
        //Clippiamo anche verso i 4 bordi dello schermo
        Triangolo Clipped[] = new Triangolo[2];

        triangleList.add(T);
        int nNuoviTriang = 1;

        for(int p = 0; p < 4; p++)
        {
            int nTriangDaAggiun = 0;
            while(nNuoviTriang > 0)
            {
                Triangolo test = triangleList.get(0);
                triangleList.remove(0);
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
                    triangleList.add(Clipped[w]);
                }
            }
            nNuoviTriang = triangleList.size();
        }
    }
    */
    
        //We change the space with new triangles
        copy.setSpace(triangleList);
        //Visualize the point on the monitor
        Draw(Monitor, monitor, copy);
    }

    //For Matrix Multiplications
    public Point3D MatrixMultiplication(Point3D i, double[][] m)
    {
        Point3D o = new Point3D();
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
    public Point3D MatrixMultiplication2(Point3D i, double[][] m)
    {
        Point3D o = new Point3D();
        o.setX(((i.getX())*(m[0][0]) + (i.getY())*(m[0][1]) + (i.getZ())*(m[0][2])));
        o.setY(((i.getX())*(m[1][0]) + (i.getY())*(m[1][1]) + (i.getZ())*(m[1][2])));
        o.setZ(((i.getX())*(m[2][0]) + (i.getY())*(m[2][1]) + (i.getZ())*(m[2][2])));
        
        return o;
    }

    public char[][] Draw(Display Monitor, char[][] schermo, Space space)
    {
        //Z-buffer (da Sistemare???)
        double[][] Buffer = new double[Display.getDIMY()][Display.getDIMX()];
        for(int i = 0; i < Display.getDIMY(); i++)
        {
            for(int j = 0; j < Display.getDIMX(); j++)
            {
                //Inizializziamo il più lontano possibile (???)
                Buffer[i][j] = 999999;
            }
        }

        for(int l = 0; l < (space.getSpace()).size(); l++)
        {
            Triangle triTranslated = (space.getSpace()).get(l);
            //We have to verify that triangle's vertices are inside the display, and their value should be the smallest in the Z-buffer (Riv?)
            if((int)Math.ceil(triTranslated.getTriangle()[0].getX()) >= 0 && (int)Math.ceil(triTranslated.getTriangle()[0].getX()) < Display.getDIMX() && (int)Math.ceil(triTranslated.getTriangle()[0].getY()) >= 0 && (int)Math.ceil(triTranslated.getTriangle()[0].getY()) < Display.getDIMY() && triTranslated.getTriangle()[0].getZ() < Buffer[(int)Math.ceil(triTranslated.getTriangle()[0].getY())][(int)Math.ceil(triTranslated.getTriangle()[0].getX())])
            {
                schermo[(int)Math.ceil(triTranslated.getTriangle()[0].getY())][(int)Math.ceil(triTranslated.getTriangle()[0].getX())] = triTranslated.getBrightness_char();
            }
            
            if((int)Math.ceil(triTranslated.getTriangle()[1].getX()) >= 0 && (int)Math.ceil(triTranslated.getTriangle()[1].getX()) < Display.getDIMX() && (int)Math.ceil(triTranslated.getTriangle()[1].getY()) >= 0 && (int)Math.ceil(triTranslated.getTriangle()[1].getY()) < Display.getDIMY() && triTranslated.getTriangle()[1].getZ() < Buffer[(int)Math.ceil(triTranslated.getTriangle()[1].getY())][(int)Math.ceil(triTranslated.getTriangle()[1].getX())])
            {
                schermo[(int)Math.ceil(triTranslated.getTriangle()[1].getY())][(int)Math.ceil(triTranslated.getTriangle()[1].getX())] = triTranslated.getBrightness_char();
            }

            if((int)Math.ceil(triTranslated.getTriangle()[2].getX()) >= 0 && (int)Math.ceil(triTranslated.getTriangle()[2].getX()) < Display.getDIMX() && (int)Math.ceil(triTranslated.getTriangle()[2].getY()) >= 0 && (int)Math.ceil(triTranslated.getTriangle()[2].getY()) < Display.getDIMY() && triTranslated.getTriangle()[2].getZ() < Buffer[(int)Math.ceil(triTranslated.getTriangle()[2].getY())][(int)Math.ceil(triTranslated.getTriangle()[2].getX())])
            {
                schermo[(int)Math.ceil(triTranslated.getTriangle()[2].getY())][(int)Math.ceil(triTranslated.getTriangle()[2].getX())] = triTranslated.getBrightness_char();
            }

            //We can update the monitor with vertices points
            Monitor.setMonitor(schermo);
            //Rasterization of triangles
            Monitor.Rasterization(triTranslated, Buffer);
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

    //Setter and getter methods for rotations
    public void setThetaX(double fThetaX)
    {
        this.fThetaX = Math.toRadians(fThetaX);
        this.setMatrixRot();
    }
    public void setThetaY(double fThetaY)
    {
        this.fThetaY = Math.toRadians(fThetaY);
        this.setMatrixRot();
    }
    public void setThetaZ(double fThetaZ)
    {
        this.fThetaZ = Math.toRadians(fThetaZ);
        this.setMatrixRot();
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

    //To setting the rotation matrices
    public void setMatrixRot()
    {
        this.matrixRotX[0][0] =  1;
        this.matrixRotX[1][1] =  Math.cos(this.fThetaX);
        this.matrixRotX[1][2] = -Math.sin(this.fThetaX);
        this.matrixRotX[2][1] =  Math.sin(this.fThetaX); 
        this.matrixRotX[2][2] =  Math.cos(this.fThetaX);
        this.matrixRotX[3][3] =  1;

        this.matrixRotY[0][0] =  Math.cos(this.fThetaY);
        this.matrixRotY[0][2] =  Math.sin(this.fThetaY);
        this.matrixRotY[1][1] =  1;
        this.matrixRotY[2][0] = -Math.sin(this.fThetaY);
        this.matrixRotY[2][2] =  Math.cos(this.fThetaY);
        this.matrixRotY[3][3] =  1;

        this.matrixRotZ[0][0] =  Math.cos(this.fThetaZ);
        this.matrixRotZ[0][1] = -Math.sin(this.fThetaZ);
        this.matrixRotZ[1][0] =  Math.sin(this.fThetaZ);
        this.matrixRotZ[1][1] =  Math.cos(this.fThetaZ);
        this.matrixRotZ[2][2] =  1;
        this.matrixRotZ[3][3] =  1;
    }

    //???
    public Point3D Vector_IntersectsPlane(Point3D Plane_p, Point3D Plane_n, Point3D lineStart, Point3D lineEnd)
    {
        double Norm_n = Math.sqrt(Plane_n.getX()*Plane_n.getX() + Plane_n.getY()*Plane_n.getY() + Plane_n.getZ()*Plane_n.getZ());
        Plane_n.setX(Plane_n.getX()/Norm_n);
        Plane_n.setY(Plane_n.getY()/Norm_n);
        Plane_n.setZ(Plane_n.getZ()/Norm_n);
        double Plane_d = -(Plane_n.getX()*Plane_p.getX() + Plane_n.getY()*Plane_p.getY() + Plane_n.getZ()*Plane_p.getZ());
        double ad = lineStart.getX()*Plane_n.getX() + lineStart.getY()*Plane_n.getY() + lineStart.getZ()*Plane_n.getZ();
        double bd = lineEnd.getX()*Plane_n.getX() + lineEnd.getY()*Plane_n.getY() + lineEnd.getZ()*Plane_n.getZ();
        double t = (-Plane_d - ad) / (bd-ad);
        Point3D lineStartEnd = new Point3D(lineEnd.getX() - lineStart.getX(), lineEnd.getY() - lineStart.getY(), lineEnd.getZ() - lineStart.getZ());
        Point3D lineToIntersect = new Point3D(t*lineStartEnd.getX(), t*lineStartEnd.getY(), t*lineStartEnd.getZ());

        return new Point3D(lineStart.getX() + lineToIntersect.getX(), lineStart.getY() + lineToIntersect.getY(), lineStart.getZ() + lineToIntersect.getZ());
    }

    //???
    public int Triangle_Clipped_Plane(Point3D Plane_p, Point3D Plane_n, Triangle in_tri, Triangle out_tri1, Triangle out_tri2)
    {
        //Normalize the plane's normal
        double Norm_n = Math.sqrt(Plane_n.getX()*Plane_n.getX() + Plane_n.getY()*Plane_n.getY() + Plane_n.getZ()*Plane_n.getZ());
        Plane_n.setX(Plane_n.getX()/Norm_n);
        Plane_n.setY(Plane_n.getY()/Norm_n);
        Plane_n.setZ(Plane_n.getZ()/Norm_n);

        Point3D[] Internal_Points = new Point3D[3];
        Point3D[] External_Points = new Point3D[3];
        int nPointInt = 0, nPointExt = 0;

        //Calcolulate the points-plane distances
        double dist0 = distance(in_tri.getTriangle()[0], Plane_n, Plane_p);
        double dist1 = distance(in_tri.getTriangle()[1], Plane_n, Plane_p);
        double dist2 = distance(in_tri.getTriangle()[2], Plane_n, Plane_p);

        if(dist0 >= 0)
        {
            Internal_Points[nPointInt] = in_tri.getTriangle()[0];
            nPointInt++;
        }
        else
        {
            External_Points[nPointExt] = in_tri.getTriangle()[0];
            nPointExt++;
        }
        if(dist1 >= 0)
        {
            Internal_Points[nPointInt] = in_tri.getTriangle()[1];
            nPointInt++;
        }
        else
        {
            External_Points[nPointExt] = in_tri.getTriangle()[1];
            nPointExt++;
        }
        if(dist2 >= 0)
        {
            Internal_Points[nPointInt] = in_tri.getTriangle()[2];
            nPointInt++;
        }
        else
        {
            External_Points[nPointExt] = in_tri.getTriangle()[2];
            nPointExt++;
        }


        if(nPointInt == 0)
        {
            return 0;
        }
        if(nPointInt == 3)
        {
            out_tri1.setTriangle(in_tri.getTriangle()[0], in_tri.getTriangle()[1], in_tri.getTriangle()[2]);          
            return 1;
        }
        if (nPointInt == 1 && nPointExt == 2) 
        {
            Point3D p0 = new Point3D(Internal_Points[0].getX(), Internal_Points[0].getY(), Internal_Points[0].getZ());
            Point3D p1 = Vector_IntersectsPlane(Plane_p, Plane_n, Internal_Points[0], External_Points[0]); 
            Point3D p2 = Vector_IntersectsPlane(Plane_p, Plane_n, Internal_Points[0], External_Points[1]);  
            out_tri1.setTriangle(p0, p1, p2);
            return 1;
        }
        if(nPointInt == 2 && nPointExt == 1)
        {
            Point3D p0 = new Point3D(Internal_Points[0].getX(), Internal_Points[0].getY(), Internal_Points[0].getZ());
            Point3D p1 = new Point3D(Internal_Points[1].getX(), Internal_Points[1].getY(), Internal_Points[1].getZ());
            Point3D p2 = Vector_IntersectsPlane(Plane_p, Plane_n, Internal_Points[0], External_Points[0]);         
            out_tri1.setTriangle(p0, p1, p2);

            out_tri1.getTriangle()[1].setX(p1.getX());
            out_tri1.getTriangle()[1].setY(p1.getY());
            out_tri1.getTriangle()[1].setZ(p1.getZ());

            out_tri2.getTriangle()[0].setX(Internal_Points[1].getX());
            out_tri2.getTriangle()[0].setY(Internal_Points[1].getY());
            out_tri2.getTriangle()[0].setZ(Internal_Points[1].getZ());
            
            out_tri2.getTriangle()[0].setX(out_tri1.getTriangle()[2].getX());
            out_tri2.getTriangle()[0].setY(out_tri1.getTriangle()[2].getY());
            out_tri2.getTriangle()[0].setZ(out_tri1.getTriangle()[2].getZ());
            p0 = p1;
            p1 = out_tri1.getTriangle()[2];
            p2 = Vector_IntersectsPlane(Plane_p, Plane_n, Internal_Points[1], External_Points[0]);  
            out_tri2.setTriangle(p0, p1, p2);
            return 2;
        }

        return 0;
    }

    //???
    public double distance(Point3D p, Point3D Plane_n, Point3D Plane_p)
    {
    ///////////////////////////?????normalizza
    return (Plane_n.getX()*p.getX() + Plane_n.getY()*p.getY() + Plane_n.getZ()*p.getZ() - (Plane_n.getX()*Plane_p.getX() + Plane_n.getY()*Plane_p.getY() + Plane_n.getZ()*Plane_p.getZ()));
    }
}

