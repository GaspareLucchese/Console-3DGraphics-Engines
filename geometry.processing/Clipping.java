package geometry.processing;

import java.util.ArrayList;
import java.util.List;

import geometry.Point3D;
import geometry.Triangle;

public class Clipping {

    // Funzione principale di clipping per il frustum
    public static List<Triangle> clipTriangleAgainstFrustum(Triangle tri, double[][] planes) {
        List<Triangle> clippedTriangles = new ArrayList<>();
        clippedTriangles.add(tri); // Inizia con il triangolo originale

        // Clippiamo contro ogni piano del frustum
        for (int i = 0; i < planes.length; i++) {
            List<Triangle> newClippedTriangles = new ArrayList<>();

            // Per ogni triangolo che abbiamo, lo clippiamo contro il piano
            for (Triangle t : clippedTriangles) {
                newClippedTriangles.addAll(clipTriangleAgainstPlane(t, planes[i]));
            }

            clippedTriangles = newClippedTriangles; // Aggiorniamo la lista
            if (clippedTriangles.isEmpty()) {
                break; // Se la lista è vuota, significa che tutto è stato eliminato
            }
        }

        return clippedTriangles;
    }

    // Clippa un triangolo contro un piano specifico
    private static List<Triangle> clipTriangleAgainstPlane(Triangle tri, double[] plane) {
        List<Point3D> insidePoints = new ArrayList<>();
        List<Point3D> outsidePoints = new ArrayList<>();

        // Dividi i vertici in inside e outside rispetto al piano
        for (Point3D p : tri.getTriangle()) {
            if (isInsidePlane(p, plane)) {
                insidePoints.add(p);
            } else {
                outsidePoints.add(p);
            }
        }

        // Se tutti i punti sono fuori, eliminiamo il triangolo
        if (insidePoints.size() == 0) {
            return new ArrayList<>();
        }

        // Se tutti i punti sono dentro, manteniamo il triangolo
        if (insidePoints.size() == 3) {
            return List.of(tri);
        }

        // Caso con 1 punto dentro e 2 fuori, creiamo un nuovo triangolo
        if (insidePoints.size() == 1 && outsidePoints.size() == 2) {
            return List.of(clipOneInsideTwoOutside(insidePoints.get(0), outsidePoints.get(0), outsidePoints.get(1), plane));
        }

        // Caso con 2 punti dentro e 1 fuori, creiamo 2 nuovi triangoli
        if (insidePoints.size() == 2 && outsidePoints.size() == 1) {
            return clipTwoInsideOneOutside(insidePoints.get(0), insidePoints.get(1), outsidePoints.get(0), plane);
        }

        throw new IllegalStateException("This should never happen");
    }

    // Funzione che verifica se un punto è dentro il piano (sul lato positivo)
    private static boolean isInsidePlane(Point3D p, double[] plane) {
        // Verifica se il punto è dentro il piano usando la formula del piano ax + by + cz + d = 0
        double result = plane[0] * p.getX() + plane[1] * p.getY() + plane[2] * p.getZ() + plane[3];
        return result >= 0; // Punto dentro il piano (lato positivo)
    }

    // Crea un nuovo triangolo quando un punto è dentro e due sono fuori
    private static Triangle clipOneInsideTwoOutside(Point3D inside, Point3D outside1, Point3D outside2, double[] plane) {
        Point3D intersection1 = getIntersection(inside, outside1, plane);
        Point3D intersection2 = getIntersection(inside, outside2, plane);
        return new Triangle(inside, intersection1, intersection2);
    }

    // Crea due nuovi triangoli quando due punti sono dentro e uno è fuori
    private static List<Triangle> clipTwoInsideOneOutside(Point3D inside1, Point3D inside2, Point3D outside, double[] plane) {
        Point3D intersection1 = getIntersection(inside1, outside, plane);
        Point3D intersection2 = getIntersection(inside2, outside, plane);
        Triangle t1 = new Triangle(inside1, intersection1, intersection2);
        Triangle t2 = new Triangle(inside2, intersection2, intersection1);
        return List.of(t1, t2);
    }

    // Funzione che calcola l'intersezione tra due punti e un piano
    private static Point3D getIntersection(Point3D p1, Point3D p2, double[] plane) {
        // Calcola il punto di intersezione tra il segmento [p1, p2] e il piano
        double t = -(plane[0] * p1.getX() + plane[1] * p1.getY() + plane[2] * p1.getZ() + plane[3]) /
                    (plane[0] * (p2.getX() - p1.getX()) + plane[1] * (p2.getY() - p1.getY()) + plane[2] * (p2.getZ() - p1.getZ()));
        return p1.add(p2.subtract(p1).multiply(t));
    }
}
