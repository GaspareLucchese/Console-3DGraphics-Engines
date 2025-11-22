//       _o_                                   
//  ,-.'-----`.__ ;   -Developer: Gaspare Lucchese                    
// ((j`=======',-'    -Data Ultima Modifica: 22/11/2025     
//  `-\       /       -Descrizione: Used to apply transformations to a mesh 
//     `-===-'  

package geometryprocessing;

import geometry.Point3D;
import geometry.Triangle;
import scene.Mesh;

//This class is used to apply transformations to a mesh
public class Trasformation {

    //Angles to rotate the mesh
    private double fThetaX = Math.toRadians(0);
    private double fThetaY = Math.toRadians(0);
    private double fThetaZ = Math.toRadians(0);
    private double[][] matScaling = new double[4][4];
    private double[][] matrixRotX = new double[4][4];
    private double[][] matrixRotY = new double[4][4];
    private double[][] matrixRotZ = new double[4][4];
    private double[][] matTranslation = new double[4][4];

    //Values for mesh translation
    //negative = move to the left, positive = move to right
    double moveX = 0;
    //negative = move down, positive = move up
    double moveY = 0;
    //negative = behind the viewer, positive = in front of the viewer
    double distance = 0;

    double scaleX = 1;
    double scaleY = 1;
    double scaleZ = 1;

    public Mesh meshTransformation(Mesh mesh)
    {
        Mesh copy = new Mesh();
        //Resetting all the matrices
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                matScaling[i][j] = 0;
                matrixRotX[i][j] = 0;
                matrixRotY[i][j] = 0;
                matrixRotZ[i][j] = 0;
                matTranslation[i][j] = 0;
            }
        }
        
        //[TO-DO] Change the input for the setting methods, like the rotation one
        this.setMatrixRot();
        this.setMatScaling();
        this.setMovement(moveX, moveY, distance);

        for(int l = 0; l < (mesh.getMesh()).size(); l++)
        {
            //We extract all the triangles in the mesh
            Triangle tri = (mesh.getMesh()).get(l);
            Triangle triScaled = new Triangle();
            Triangle triRotated = new Triangle();
            //Triangle triTranslated = new Triangle();

            //We apply the scaling to the mesh
            triScaled.setTriangle(MatrixMultiplication2(tri.getTriangle()[0], matScaling), MatrixMultiplication2(tri.getTriangle()[1], matScaling), MatrixMultiplication2(tri.getTriangle()[2], matScaling));

            //We can now apply the rotation to individual points
            triRotated.setTriangle(MatrixMultiplication2(triScaled.getTriangle()[0], matrixRotX), MatrixMultiplication2(triScaled.getTriangle()[1], matrixRotX), MatrixMultiplication2(triScaled.getTriangle()[2], matrixRotX));
            triRotated.setTriangle(MatrixMultiplication2(triRotated.getTriangle()[0], matrixRotY), MatrixMultiplication2(triRotated.getTriangle()[1], matrixRotY), MatrixMultiplication2(triRotated.getTriangle()[2], matrixRotY));
            triRotated.setTriangle(MatrixMultiplication2(triRotated.getTriangle()[0], matrixRotZ), MatrixMultiplication2(triRotated.getTriangle()[1], matrixRotZ), MatrixMultiplication2(triRotated.getTriangle()[2], matrixRotZ));

            Triangle triTranslated = new Triangle(triRotated.getTriangle()[0], triRotated.getTriangle()[1], triRotated.getTriangle()[2]);

            //Applying the traslation
            triTranslated.setTriangle(MatrixMultiplication2(triRotated.getTriangle()[0], matTranslation), MatrixMultiplication2(triRotated.getTriangle()[1], matTranslation), MatrixMultiplication2(triRotated.getTriangle()[2], matTranslation));
    
            Point3D[] originalNormals = tri.getNormals();
            //We need to apply the rotation also to the vertex normals (but not the scaling and the translation!)

            Point3D n1 = MatrixMultiplication2(originalNormals[0], matrixRotX);
            Point3D n2 = MatrixMultiplication2(originalNormals[1], matrixRotX);
            Point3D n3 = MatrixMultiplication2(originalNormals[2], matrixRotX);

            n1 = MatrixMultiplication2(n1, matrixRotY);
            n2 = MatrixMultiplication2(n2, matrixRotY);
            n3 = MatrixMultiplication2(n3, matrixRotY);

            n1 = MatrixMultiplication2(n1, matrixRotZ);
            n2 = MatrixMultiplication2(n2, matrixRotZ);
            n3 = MatrixMultiplication2(n3, matrixRotZ);

            n1 = n1.normalized();
            n2 = n2.normalized();
            n3 = n3.normalized();

            //We can set the new vertex normals to the new triangle
            triTranslated.setVertexNormals(n1, n2, n3);

            //[TO-DO] Check if necessary
            //We calculate again the face normal, if we need it
            triTranslated.setFaceNormal();

            copy.addTriangle(triTranslated);
        }
        return copy;
    }

    public Point3D MatrixMultiplication2(Point3D i, double[][] m)
    {
        Point3D o = new Point3D();
        o.setX(((i.getX())*(m[0][0]) + (i.getY())*(m[0][1]) + (i.getZ())*(m[0][2]) + 1*m[0][3]));
        o.setY(((i.getX())*(m[1][0]) + (i.getY())*(m[1][1]) + (i.getZ())*(m[1][2]) + 1*m[1][3]));
        o.setZ(((i.getX())*(m[2][0]) + (i.getY())*(m[2][1]) + (i.getZ())*(m[2][2]) + 1*m[2][3]));
        
        return o;
    }

    //To set the trasformation matrices
    public void setMatScaling()
    {
        this.matScaling[0][0] = this.scaleX;
        this.matScaling[1][1] = this.scaleY;
        this.matScaling[2][2] = this.scaleZ;
        this.matScaling[3][3] = 1;
    }
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
    public void setMatTranslation()
    {
        this.matTranslation[0][0] = 1;
        this.matTranslation[1][1] = 1;
        this.matTranslation[2][2] = 1;
        this.matTranslation[3][3] = 1;

        //[TO-DO] CHECK
        this.matTranslation[0][3] = this.moveX;
        this.matTranslation[1][3] = this.moveY;
        this.matTranslation[2][3] = this.distance;
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

    //[TO-DO] Change with the Translation Matrix
    public void setMovement(double moveX, double moveY, double distance)
    {
        this.moveX = moveX;
        this.moveY = moveY;
        this.distance = distance;
        this.setMatTranslation();
    }
}
