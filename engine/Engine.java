package engine;

import java.util.List;

import display.Display;
import geometry.Triangle;
import scene.Mesh;
import applicationstage.BackfaceCulling;
import applicationstage.FrustumCulling;
import geometryprocessing.Trasformation;
import geometryprocessing.Clipping;
import geometryprocessing.FlatShader;
import geometryprocessing.Projection;
import geometryprocessing.ScreenMapping;
import rasterization.Rasterization;

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
            double [][] planes = FrustumCulling.computeFrustumPlanes();
            meshBackface = BackfaceCulling.backface_culling(meshGeometry);
            for(Triangle tri : meshBackface.getMesh())
            {
                List<Triangle> clipped = 
                Clipping.clipTriangleAgainstFrustum(tri, planes);
                                
                for (Triangle cTri : clipped)
                {
                     //We calculate the brightness value of the triangle
                    double brightness_value = FlatShader.computeBrightness(cTri);

                    //Now we can compute the triangle's projection
                    Triangle triProjected = Proj.Projects(cTri);

                    //[?]
                    //Inizializziamo per lo z-buffer(???)         
                    triProjected.getTriangle()[0].setZ(cTri.getTriangle()[0].getZ());
                    triProjected.getTriangle()[1].setZ(cTri.getTriangle()[1].getZ());
                    triProjected.getTriangle()[2].setZ(cTri.getTriangle()[2].getZ());
                    

                    //Add the projected triangle in the mesh and set its previously calculated brightness
                    Triangle tri_temp = ScreenMapping.mapping_the_screen(triProjected);
                    tri_temp.setBrightness_char(brightness_value);
                    finalMesh.addTriangle(tri_temp);
                }                   
            }
        }

        /*
        System.out.println("Starting Triangles: " + mesh.getMesh().size());
        System.out.println("Triangles after trasformation: " + meshGeometry.getMesh().size());
        System.out.println("Triangles after Backface Culling: " + meshBackface.getMesh().size());
        System.out.println("Triangles at the end (after clipping): " + finalMesh.getMesh().size());
        */
        

        //Visualizing the point on monitor
        Draw(Monitor, monitor, finalMesh);
    }

    

    public char[][] Draw(Display Monitor, char[][] schermo, Mesh mesh)
    {
        //[?] FORSE NON DA FARE QUI; DA ORDINARE
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

        for(Triangle triTranslated : mesh.getMesh())
        {
            //[??]
            new Rasterization(triTranslated, Buffer, Monitor);
        }
        
        return Monitor.Monitor;
    }
}

