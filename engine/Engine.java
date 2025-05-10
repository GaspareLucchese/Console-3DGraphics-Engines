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
    double[][] buffer = new double[Display.getDIMY()][Display.getDIMX()];

    //Calculate the position of new points in the mesh after the projection
    public void Rendering(Mesh mesh, Display Monitor, Trasformation trasf)
    {
        Projection Proj = new Projection();
        ZbufferInizializer(); //[TO-DO] ZBUFFER TO REWIEW

        Mesh meshGeometry = new Mesh();
        Mesh meshBackface = new Mesh(); 
        Mesh finalMesh = new Mesh(); 
        
        //Starting with space transformations (Rotation, Scaling, Translation)
        meshGeometry = trasf.meshTransformation(mesh);
        //Calculating the bounding box of the mesh
        meshGeometry.setBoundingBox();

        //We apply the Frustum Culling to exclude a whole mesh if isn't visible in the camera frustum
        if(FrustumCulling.isInFrustum(meshGeometry.getBoundingBox()))
        {
            //We apply the Backface Culling to exclude all the triangles whose face is not visible 
            meshBackface = BackfaceCulling.backface_culling(meshGeometry);

            //Calculating the frustum planes to apply clipping
            double [][] planes = FrustumCulling.computeFrustumPlanes();
            for(Triangle tri : meshBackface.getMesh())
            {
                //We list all clipped triangles
                List<Triangle> clipped = Clipping.clipTriangleAgainstFrustum(tri, planes);
                                
                for (Triangle cTri : clipped)
                {
                    //We calculate the brightness value of the triangle
                    double brightness_value = FlatShader.computeBrightness(cTri);

                    //Now we can compute the triangle's projection
                    Triangle triProjected = Proj.Projects(cTri);

                    //[TO-DO] 
                    //ZBUFFER INIZIALIZATION TO REWIEW     
                    triProjected.getTriangle()[0].setZ(cTri.getTriangle()[0].getZ());
                    triProjected.getTriangle()[1].setZ(cTri.getTriangle()[1].getZ());
                    triProjected.getTriangle()[2].setZ(cTri.getTriangle()[2].getZ());
                    
                    //Add the projected triangle in the mesh and set its previously calculated brightness
                    Triangle tri_temp = ScreenMapping.mapping_the_screen(triProjected);
                    tri_temp.setBrightness_value(brightness_value);
                    finalMesh.addTriangle(tri_temp);
                }                   
            }

            //[TO-DO]
            //////SO, WE PROBABILY WE CAN PLACE THIS INTO THE CLIPPING LOOP,
            //////BUT BEFORE WE SHOULD SEE IF WE SHOULD SORT THE TRIANGLES/MESH TO APPLY Z-BUFFERING
            
            //Visualizing points on monitor
            for(Triangle triTranslated : finalMesh.getMesh())
            {
                new Rasterization(triTranslated, buffer, Monitor);
            }
        }

        //ONLY FOR DEBUGGING
        /*
        System.out.println("Starting Triangles: " + mesh.getMesh().size());
        System.out.println("Triangles after trasformation: " + meshGeometry.getMesh().size());
        System.out.println("Triangles after Backface Culling: " + meshBackface.getMesh().size());
        System.out.println("Triangles at the end (after clipping): " + finalMesh.getMesh().size());
        */
    }

    //[TO-DO] ZBUFFER TO REWIEW
    public void ZbufferInizializer()
    {
        //[TO-DO] MAYBE WE CAN MOVE IN ANOTHER CLASS, TO REODER TRIANGLES
        for(int i = 0; i < Display.getDIMY(); i++)
        {
            for(int j = 0; j < Display.getDIMX(); j++)
            {
                //[TO-DO] INIZIALIZE WITH A BIGGER NUMBER
                this.buffer[i][j] = 999999;
            }
        }
    }
}

