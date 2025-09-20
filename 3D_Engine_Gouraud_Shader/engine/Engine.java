//       _o_                                   
//  ,-.'-----`.__ ;   -Developer: Gaspare Lucchese                    
// ((j`=======',-'    -Data Ultima Modifica: 22/05/2025     
//  `-\       /       -Descrizione: Used to manage the rendering of 
//     `-===-'         the mesh on the screen    

package engine;

import java.util.List;

import display.Display;
import geometry.Triangle;
import scene.Mesh;
import applicationstage.BackfaceCulling;
import applicationstage.FrustumCulling;
import geometryprocessing.Trasformation;
import geometryprocessing.Clipping;
import geometryprocessing.GouraudShader;
import geometryprocessing.Projection;
import geometryprocessing.ScreenMapping;
import rasterization.Rasterization;

//This class is used to manage the rendering of the mesh on the screen
public class Engine 
{
    double[][] zBuffer = new double[Display.getDIMY()][Display.getDIMX()];

    public void Rendering(Mesh mesh, Display Monitor, Trasformation trasf)
    {
        Projection Proj = new Projection();
        inizializeZBuffer();

        Mesh meshGeometry = new Mesh();
        Mesh meshBackface = new Mesh(); 
        Mesh finalMesh = new Mesh(); 
        
        //Starting with space transformations (Rotation, Scaling, Translation)
        meshGeometry = trasf.meshTransformation(mesh);
        //Calculating the mesh's bounding box
        meshGeometry.setBoundingBox();

        //APPLICATION STAGE
        //We apply the Frustum Culling to exclude a whole mesh if isn't visible in the camera frustum
        if(FrustumCulling.isInFrustum(meshGeometry.getBoundingBox()))
        {
            //We apply the Backface Culling to exclude all the triangles whose face is not visible to the camera
            meshBackface = BackfaceCulling.backface_culling(meshGeometry);

            //[TO-DO] FIX CLIPPING STAGE TO USE GOURAUD
            //GEOMETRY PROCESSING STAGE
            //Calculating the frustum planes to apply clipping
            double [][] planes = FrustumCulling.computeFrustumPlanes();
            for(Triangle tri : meshBackface.getMesh())
            {
                //We list all the clipped triangles
                List<Triangle> clipped = Clipping.clipTriangleAgainstFrustum(tri, planes);
                                
                for (Triangle cTri : clipped)
                {
                    if (cTri.getNormals() == null)
                    {
                        cTri.setVertexNormals(cTri.getFaceNormal(), cTri.getFaceNormal(), cTri.getFaceNormal());
                    }
                    //We calculate the brightness value of the triangle using the Gouraud Shader
                    double [] brightness_value = GouraudShader.computeBrightness(cTri);

                    //Now we can compute the triangle's projection
                    Triangle triProjected = Proj.Projects(cTri);
                    
                    //Add the projected triangle in the mesh and set its previously calculated brightness
                    Triangle tri_temp = ScreenMapping.mapping_the_screen(triProjected);
                    tri_temp.setBrightness_value(brightness_value);
                    
                    //We add the triangle to the final mesh
                    finalMesh.addTriangle(tri_temp);
                }                   
            }

            //[TO-DO]
            //////SO, WE PROBABILY WE CAN PLACE THIS INTO THE CLIPPING LOOP,
            //////BUT BEFORE WE SHOULD SEE IF WE SHOULD SORT THE TRIANGLES/MESH TO APPLY Z-BUFFERING
            
            //Visualizing points on monitor
            //We apply the Z-Buffering algorithm to remove the triangles that are occluded by other triangles
            for(Triangle triTranslated : finalMesh.getMesh())
            {
                //RASTERIZATION AND PIXEL PROCESSING STAGE
                new Rasterization(triTranslated, zBuffer, Monitor);
            }
        }

        //ONLY FOR DEBUGGING
        // System.out.println("Starting Triangles: " + mesh.getMesh().size());
        // System.out.println("Triangles after trasformation: " + meshGeometry.getMesh().size());
        // System.out.println("Triangles after Backface Culling: " + meshBackface.getMesh().size());
        // System.out.println("Triangles at the end (after clipping): " + finalMesh.getMesh().size());
        
    }

    //This method is used to initialize the Z-Buffer with negative infinity values
    public void inizializeZBuffer()
    {
        for(int i = 0; i < Display.getDIMY(); i++)
        {
            for(int j = 0; j < Display.getDIMX(); j++)
            {
                this.zBuffer[i][j] = Double.NEGATIVE_INFINITY;
            }
        }
    }
}

