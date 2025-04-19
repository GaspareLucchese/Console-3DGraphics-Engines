package engine;

import display.Display;
import geometry.Point3D;
import geometry.Triangle;
import scene.Mesh;
import application.BackfaceCulling;
import application.FrustumCulling;
import geometryprocessing.Trasformation;

public class Engine 
{

    private double[][] matrixPerspective = new double[4][4];

    public Engine()
    {
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                matrixPerspective[i][j] = 0;
            }
        }

        //Setting the perspective matrix
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

        //[?] non lo facciamo pure dopo?
        /////////////////this.setMatrixRot();
        
    }

    

    //Calculate the position of new points in the mesh after the projection
    public void Projects(Mesh mesh, Display Monitor, char monitor[][], Trasformation trasf)
    {
        Point3D normal, light;
        
        //[?]
        //Gestiamo la direzione della luce (Sistemare?)
        light = new Point3D(0, 0, -1);
        //Normalize the lighting vector
        double lum = Math.sqrt(light.getX()*light.getX() + light.getY()*light.getY() + light.getZ()*light.getZ());
        light.setX(light.getX()/lum);
        light.setY(light.getY()/lum);
        light.setZ(light.getZ()/lum);

        //[?]
        //We can work on a copy of the mesh from input, so we can do not modify the mesh itself
        Mesh meshGeometry = new Mesh();
        Mesh meshBackface = new Mesh(); 
        Mesh finalMesh = new Mesh(); 
        
        meshGeometry = trasf.meshTransformation(mesh);
        meshBackface = BackfaceCulling.backface_culling(meshGeometry);
        meshBackface.setBoundingBox();
        if(!(FrustumCulling.isInFrustum(meshBackface.getBoundingBox())))
        {
            System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK?");
        }

        for(Triangle tri : meshBackface.getMesh())
        {
            if(true)
            {
                //Apply the projection
                Triangle triProjected = new Triangle(MatrixMultiplication(tri.getTriangle()[0], matrixPerspective), MatrixMultiplication(tri.getTriangle()[1], matrixPerspective), MatrixMultiplication(tri.getTriangle()[2], matrixPerspective));

                //[?]
                /*Rivedere??? (Normalizziamo?)*/
                if(true)
                {
                    //Center in screen and scale, based on the screen size
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

                //[?]
                //Inizializziamo per lo z-buffer(???)         
                triProjected.getTriangle()[0].setZ(tri.getTriangle()[0].getZ());
                triProjected.getTriangle()[1].setZ(tri.getTriangle()[1].getZ());
                triProjected.getTriangle()[2].setZ(tri.getTriangle()[2].getZ());

                //Add the new triangle in the mesh
                Triangle tri_temp = new Triangle(triProjected.getTriangle()[0], triProjected.getTriangle()[1], triProjected.getTriangle()[2]);
                
                normal = tri.normal();
                //To obtain the brightness value we should apply the dot product between the light vector and the triangle's normal vector 
                double brightness_value = normal.getX()*light.getX() + normal.getY()*light.getY() + normal.getZ()*light.getZ(); 
                tri_temp.setBrightness_char(brightness_value);
                finalMesh.addTriangle(tri_temp);
                
            }
        }

        /*
        System.out.println("Starting Triangles: " + mesh.getMesh().size());
        System.out.println("Triangles after trasformation: " + meshGeometry.getMesh().size());
        System.out.println("Triangles after Backface Culling: " + meshBackface.getMesh().size());
        System.out.println("Triangles after Frustum Culling: " + meshFrustum.getMesh().size())
        System.out.println("Triangles at the end: " + finalMesh.getMesh().size());
        */

        //Visualizing the point on monitor
        Draw(Monitor, monitor, finalMesh);
    }

    //To calculate mtrix multiplications
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
    

    public char[][] Draw(Display Monitor, char[][] schermo, Mesh mesh)
    {
        //[?]
        //Z-buffer (da Sistemare???)
        double[][] Buffer = new double[Display.getDIMY()][Display.getDIMX()];
        for(int i = 0; i < Display.getDIMY(); i++)
        {
            for(int j = 0; j < Display.getDIMX(); j++)
            {
                //[?]
                //Inizializziamo il più lontano possibile (???)
                Buffer[i][j] = 999999;
            }
        }

        for(int l = 0; l < (mesh.getMesh()).size(); l++)
        {
            Triangle triTranslated = (mesh.getMesh()).get(l);
            //[?]
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
        
        return Monitor.Monitor;
    }
}

