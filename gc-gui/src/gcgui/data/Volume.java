/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.data;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class Volume {

    private double[][][] vol;
    private int w;
    private int h;
    private int zth;


    public Volume(double[][][] vol) {
        this.vol = vol;
        getDimensions();
    }

    public Volume() {

    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public int getZHeight() {
        return zth;
    }

    public void setVolume(double[][][] vol) {
        this.vol = vol;
        getDimensions();
    }

    public double[][][] getVolume() {
        return vol;
    }

    private void getDimensions() {
        zth = vol.length;
        h = vol[0].length;
        w = vol[0][0].length;
    }

    public void setSubVolume(int ox, int oy, int oz, int dx, int dy, int dz) {
        double[][][] subvol = getSubVolume(ox, oy, oz, dx, dy, dz);
        setVolume(subvol);
    }

    /**
     * <p>Returns a sub-volume from within the given volume.</p>
     * <p>The sub-volume is located from the original position in
     * the given volume by its origin (ox, oy, oz) and extending in the
     * direction dx, dy, and dz.  Direction parameters (dx, dy, dz) may be positive
     * or negative; adjustments are made accordingly.</p>
     * <p>Note:  The structure of the given volume is assumed to be in accordance
     * with the NiftiData orientation of vol[z][y][x]. </p>
     * @param vol Original 3-D volume
     * @param ox x-coordinate of the origin for the new volume
     * @param oy y-coordinate of the origin for the new volume
     * @param oz z-coordinate of the origin for the new volume.
     * @param dx direction in x of the new volume from the origin
     * @param dy direction in y of the new volume from the origin
     * @param dz direction in z of the new volume from the origin
     * @return An array containing the values defined by the given coordinates.
     * @throws If the combination of origin and directions extend beyond the bounds
     * of the original volume.
     */
    public double[][][] getSubVolume(int ox, int oy, int oz, int dx, int dy, int dz) throws IllegalArgumentException {
        //How far in each direction
        int deltaX = Math.abs(dx);
        int deltaY = Math.abs(dy);
        int deltaZ = Math.abs(dz);

        System.out.println("ox = "+ox+", oy = "+oy+", oz = "+oz);
        System.out.println("dx = "+dx+", dy = "+dy+", dz = "+dz);
        System.out.println("deltaX = "+deltaX);
        System.out.println("deltaY = "+deltaY);
        System.out.println("deltaZ = "+deltaZ);
        //Adjust the origin based on direction of travel (dx, dy, dz)
        int nox = min(ox, ox+dx);
        int noy = min(oy, oy+dy);
        int noz = min(oz, oz+dz);

        System.out.println("nox = "+nox+", noy = "+noy+", noz = "+noz);

        if (nox < 0 || noy < 0 || noz < 0) {
            throw new IllegalArgumentException("The given parameters extend outside the "
                    + "lower bounds of the given volume.");
        } else if ((nox+deltaX) >= w || (noy+deltaY) > h || (noz+deltaZ) > zth) {
            throw new IllegalArgumentException("The given parameters extends beyond the "
                    + "upper bounds of the given volume.");
        }

        //New array to hold sub volume
        double[][][] subVol = new double[deltaZ][deltaY][deltaX];


        for (int i=0;i<deltaX;i++) {
            for (int j=0;j<deltaY;j++) {
                for (int k=0;k<deltaZ;k++) {
                    subVol[k][j][i] = vol[noz+k][noy+j][nox+i];
                }
            }
        }

        return subVol;
    }

    private int min(int a, int b) {
        return (a<=b ? a:b);
    }


    public double[][] readXYSlice(int z) {

        double [][] slice = new double[w][h];

        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                slice[i][j] = vol[z][j][i];
            }
        }

        return slice;
    }

    public double[][] readXZSlice(int y) {

        double [][] slice = new double[w][zth];

        for (int i=0;i<w;i++) {
            for (int j=0;j<zth;j++) {
                slice[i][j] = vol[j][y][i];
            }
        }

        return slice;
    }

    public double[][] readYZSlice(int x) {

        double [][] slice = new double[h][zth];

        for (int i=0;i<h;i++) {
            for (int j=0;j<zth;j++) {
                slice[i][j] = vol[j][i][x];
            }
        }

        return slice;
    }


}
