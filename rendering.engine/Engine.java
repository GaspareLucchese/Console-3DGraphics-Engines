package engine;

import display.Display;
import geometry.Triangle;
import scene.Mesh;
import application.BackfaceCulling;
import application.FrustumCulling;
import geometryprocessing.Trasformation;
import geometryprocessing.FlatShader;
import geometryprocessing.Projection;

public class Engine 
{

    //Calculate the position of new points in the mesh after the projection
    public void Rendering(Mesh mesh, Display Monitor, char monitor[][], Trasformation trasf)
    {
        Projection Proj = new Projection();

        //[?]
        //We can work on a copy of the mesh from input, so we can do not modify the mesh itself
        Mesh meshGeometry = new Mesh();
        Mesh meshBackface = new Mesh(); 
        Mesh finalMesh = new Mesh(); 
        
        meshGeometry = trasf.meshTransformation(mesh);
        meshGeometry.setBoundingBox();
        if(FrustumCulling.isInFrustum(meshGeometry.getBoundingBox()))
        {
            meshBackface = BackfaceCulling.backface_culling(meshGeometry);
            for(Triangle tri : meshBackface.getMesh())
            {
                //We calculate the brightness value of the triangle
                double brightness_value = FlatShader.computeBrightness(tri);

                //Now we can compute the triangle's projection
                Triangle triProjected = Proj.Projects(tri);

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

                //Add the projected triangle in the mesh and set its previously calculated brightness
                Triangle tri_temp = new Triangle(triProjected.getTriangle()[0], triProjected.getTriangle()[1], triProjected.getTriangle()[2]);
                tri_temp.setBrightness_char(brightness_value);
                finalMesh.addTriangle(tri_temp);
                    
            }
        }

        
        System.out.println("Starting Triangles: " + mesh.getMesh().size());
        System.out.println("Triangles after trasformation: " + meshGeometry.getMesh().size());
        System.out.println("Triangles after Backface Culling: " + meshBackface.getMesh().size());
        System.out.println("Triangles at the end: " + finalMesh.getMesh().size());
        

        //Visualizing the point on monitor
        Draw(Monitor, monitor, finalMesh);
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

