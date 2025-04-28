package application.stage;

import display.Display;
import geometry.Point3D;

public class FrustumCulling {

    static double fNear = 0.01;
    static double fFar = 1000.0;
    static double fFov = Math.toRadians(90.0);
    static double fAspectRatio = (double)(Display.getDIMX()) / (double)(Display.getDIMY());
    static double fFovRad = Math.tan(fFov / 2.0);

    public static boolean isInFrustum(Point3D[] box) {
        double[][] frustumPlanes = computeFrustumPlanes();

        for (double[] plane : frustumPlanes) {
            int out = 0;
            for (Point3D p : box) {
                //dot product between plane and the vertex point
                double distance = plane[0]*p.getX() + plane[1]*p.getY() + plane[2]*p.getZ() +  plane[3];
                if (distance < 0) {
                    out++;
                }
            }
            //if all the vertices are outside the frustum, we d
            if (out == 8) {
                return false;
            }
        }
        return true;
    }

    public static double[][] computeFrustumPlanes() {
        double[][] planes = new double[6][4];

        double nearHeight = fNear * fFovRad;
        double nearWidth = nearHeight * fAspectRatio;

        double[][] frustumCorners = new double[][] {
            {-nearWidth, -nearHeight, fNear}, //bottom-left corner
            { nearWidth, -nearHeight, fNear}, //bottom-right corner
            { nearWidth,  nearHeight, fNear}, //top-right corner
            {-nearWidth,  nearHeight, fNear}  //top-left corner
        };

        // Near plane
        planes[0] = createPlaneFromPoints(
            frustumCorners[0][0], frustumCorners[0][1], frustumCorners[0][2],
            frustumCorners[1][0], frustumCorners[1][1], frustumCorners[1][2],
            frustumCorners[2][0], frustumCorners[2][1], frustumCorners[2][2]
        );

        // Far plane: normale fissa (-Z) e distanza fFar
        planes[1] = new double[]{0, 0, -1, fFar};

        // Left plane: camera, top-left, bottom-left
        planes[2] = createPlaneFromPoints(
            0, 0, 0,
            frustumCorners[3][0], frustumCorners[3][1], frustumCorners[3][2],
            frustumCorners[0][0], frustumCorners[0][1], frustumCorners[0][2]
        );

        // Right plane: camera, bottom-right, top-right
        planes[3] = createPlaneFromPoints
        (
            0, 0, 0,
            frustumCorners[1][0], frustumCorners[1][1], frustumCorners[1][2],
            frustumCorners[2][0], frustumCorners[2][1], frustumCorners[2][2]
        );

        // Top plane: camera, top-right, top-left
        planes[4] = createPlaneFromPoints(
            0, 0, 0,
            frustumCorners[2][0], frustumCorners[2][1], frustumCorners[2][2],
            frustumCorners[3][0], frustumCorners[3][1], frustumCorners[3][2]
        );

        // Bottom plane: camera, bottom-left, bottom-right
        planes[5] = createPlaneFromPoints(
            0, 0, 0,
            frustumCorners[0][0], frustumCorners[0][1], frustumCorners[0][2],
            frustumCorners[1][0], frustumCorners[1][1], frustumCorners[1][2]
        );

        return planes;
    }

    private static double[] createPlaneFromPoints(
        double x1, double y1, double z1,
        double x2, double y2, double z2,
        double x3, double y3, double z3
    ) {
        //vectors on plane...
        double[] line1 = {x2 - x1, y2 - y1, z2 - z1};
        double[] line2 = {x3 - x1, y3 - y1, z3 - z1};

        //...to calculate (with cross product) the plane normal
        double[] normal = {
            line1[1]*line2[2] - line1[2]*line2[1],
            line1[2]*line2[0] - line1[0]*line2[2],
            line1[0]*line2[1] - line1[1]*line2[0]
        };

        //Normalization
        double length = Math.sqrt(normal[0]*normal[0] + normal[1]*normal[1] + normal[2]*normal[2]);
        if (length == 0)
        {
            length = 1e-6; //We can't divide by zero
        }
        for (int i = 0; i < 3; i++)
        {
            normal[i] /= length;
        }

        //[?]
        // Distance point-plane => dot product between the -(plane normal) and the plane
        double d = -(normal[0]*x1 + normal[1]*y1 + normal[2]*z1);

        return new double[]{normal[0], normal[1], normal[2], d};
    }
}
