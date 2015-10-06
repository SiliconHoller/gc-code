/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util.nii;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class NiftiMemoryData extends NiftiData {

    protected double[][][] vol;
    protected byte[] rawvol;
    protected int loadedT;
    protected boolean volLoaded = false;

    protected NiftiMemoryData() {
        super();
    }

    @Override
    protected void loadData() throws IOException {
        //Default to T=0 data
        loadVol((short)0);
    }

    @Override
    public void addExtension(int code, String filename) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void loadVol(short ttt) throws IOException {
        if (volLoaded && ttt == (short)loadedT) {
            return;
        } else {
            rawvol = reader.readVolBlob(ttt);
            vol = readDoubleVol(ttt);
            loadedT = ttt;
            volLoaded = true;
        }
    }

    /**
     * Read one 3D volume from rawdat and return it as 3D double array
     * @param ttt T dimension of vol to read (0 based index)
     * @return 3D double array, scale and offset have been applied.
     * Array indices are [Z][Y][X], assuming an xyzt ordering of dimensions.
     * ie  indices are data[dim[3]][dim[2]][dim[1]]
     * @exception IOException
     *
     */
    @Override
    public double[][][] readDoubleVol(short ttt) throws IOException {
        double data[][][];
        EndianCorrectInputStream ecs;
        short ZZZ;
        int i, j, k;


        short XDIM = header.getXDIM();
        short YDIM = header.getYDIM();
        short ZDIM = header.getZDIM();
        short[] dim = header.getDim();
        short datatype = header.getDatatype();
        boolean big_endian = header.isBig_endian();
        float scl_slope = header.getScl_slope();
        float scl_inter = header.getScl_inter();

        // for 2D volumes, zdim may be 0
        ZZZ = ZDIM;
        if (dim[0] == 2) {
            ZZZ = 1;
        }


        // allocate 3D array
        data = new double[ZZZ][YDIM][XDIM];

        // read the correct datatypes from the byte array
        // undo signs if necessary, add scaling
        ecs = new EndianCorrectInputStream(new ByteArrayInputStream(rawvol), big_endian);
        switch (datatype) {

            case Nifti1.NIFTI_TYPE_INT8:
            case Nifti1.NIFTI_TYPE_UINT8:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            data[k][j][i] = (double) (ecs.readByte());
                            if ((datatype == Nifti1.NIFTI_TYPE_UINT8) && (data[k][j][i] < 0)) {
                                data[k][j][i] = Math.abs(data[k][j][i]) + (double) (1 << 7);
                            }
                            if (scl_slope != 0) {
                                data[k][j][i] = data[k][j][i] * scl_slope + scl_inter;
                            }
                        }
                    }
                }
                break;

            case Nifti1.NIFTI_TYPE_INT16:
            case Nifti1.NIFTI_TYPE_UINT16:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            data[k][j][i] = (double) (ecs.readShortCorrect());
                            if ((datatype == Nifti1.NIFTI_TYPE_UINT16) && (data[k][j][i] < 0)) {
                                data[k][j][i] = Math.abs(data[k][j][i]) + (double) (1 << 15);
                            }
                            if (scl_slope != 0) {
                                data[k][j][i] = data[k][j][i] * scl_slope + scl_inter;
                            }
                        }
                    }
                }
                break;

            case Nifti1.NIFTI_TYPE_INT32:
            case Nifti1.NIFTI_TYPE_UINT32:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            data[k][j][i] = (double) (ecs.readIntCorrect());
                            if ((datatype == Nifti1.NIFTI_TYPE_UINT32) && (data[k][j][i] < 0)) {
                                data[k][j][i] = Math.abs(data[k][j][i]) + (double) (1 << 31);
                            }
                            if (scl_slope != 0) {
                                data[k][j][i] = data[k][j][i] * scl_slope + scl_inter;
                            }
                        }
                    }
                }
                break;


            case Nifti1.NIFTI_TYPE_INT64:
            case Nifti1.NIFTI_TYPE_UINT64:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            data[k][j][i] = (double) (ecs.readLongCorrect());
                            if ((datatype == Nifti1.NIFTI_TYPE_UINT64) && (data[k][j][i] < 0)) {
                                data[k][j][i] = Math.abs(data[k][j][i]) + (double) (1 << 63);
                            }
                            if (scl_slope != 0) {
                                data[k][j][i] = data[k][j][i] * scl_slope + scl_inter;
                            }
                        }
                    }
                }
                break;


            case Nifti1.NIFTI_TYPE_FLOAT32:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            data[k][j][i] = (double) (ecs.readFloatCorrect());
                            if (scl_slope != 0) {
                                data[k][j][i] = data[k][j][i] * scl_slope + scl_inter;
                            }
                        }
                    }
                }
                break;


            case Nifti1.NIFTI_TYPE_FLOAT64:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            data[k][j][i] = (double) (ecs.readDoubleCorrect());
                            if (scl_slope != 0) {
                                data[k][j][i] = data[k][j][i] * scl_slope + scl_inter;
                            }
                        }
                    }
                }
                break;


            case Nifti1.DT_NONE:
            case Nifti1.DT_BINARY:
            case Nifti1.NIFTI_TYPE_COMPLEX64:
            case Nifti1.NIFTI_TYPE_FLOAT128:
            case Nifti1.NIFTI_TYPE_RGB24:
            case Nifti1.NIFTI_TYPE_COMPLEX128:
            case Nifti1.NIFTI_TYPE_COMPLEX256:
            case Nifti1.DT_ALL:
            default:
                throw new IOException("Sorry, cannot yet read nifti-1 datatype " + Nifti1.decodeDatatype(datatype));
        }

        ecs.close();


        return (data);
    }


    @Override
    public void writeVol(double[][][] data, short ttt) throws IOException {
        short i, j, k;
        short ZZZ;
        int blob_size;
        EndianCorrectOutputStream ecs;
        ByteArrayOutputStream baos;

        short XDIM = header.getXDIM();
        short YDIM = header.getYDIM();
        short ZDIM = header.getZDIM();
        short[] dim = header.getDim();
        short datatype = header.getDatatype();
        boolean big_endian = header.isBig_endian();
        float scl_slope = header.getScl_slope();
        float scl_inter = header.getScl_inter();

        // for 2D volumes, zdim may be 0
        ZZZ = ZDIM;
        if (dim[0] == 2) {
            ZZZ = 1;
        }

        blob_size = XDIM * YDIM * ZZZ * Nifti1.bytesPerVoxel(datatype);
        baos = new ByteArrayOutputStream(blob_size);
        ecs = new EndianCorrectOutputStream(baos, big_endian);


        switch (datatype) {

            case Nifti1.NIFTI_TYPE_INT8:
            case Nifti1.NIFTI_TYPE_UINT8:
            case Nifti1.NIFTI_TYPE_INT16:
            case Nifti1.NIFTI_TYPE_UINT16:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            if (scl_slope == 0) {
                                ecs.writeShortCorrect((short) (data[k][j][i]));
                            } else {
                                ecs.writeShortCorrect((short) ((data[k][j][i] - scl_inter) / scl_slope));
                            }
                        }
                    }
                }
                break;


            case Nifti1.NIFTI_TYPE_INT32:
            case Nifti1.NIFTI_TYPE_UINT32:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            if (scl_slope == 0) {
                                ecs.writeIntCorrect((int) (data[k][j][i]));
                            } else {
                                ecs.writeIntCorrect((int) ((data[k][j][i] - scl_inter) / scl_slope));
                            }
                        }
                    }
                }
                break;

            case Nifti1.NIFTI_TYPE_INT64:
            case Nifti1.NIFTI_TYPE_UINT64:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            if (scl_slope == 0) {
                                ecs.writeLongCorrect((long) Math.rint(data[k][j][i]));
                            } else {
                                ecs.writeLongCorrect((long) Math.rint((data[k][j][i] - scl_inter) / scl_slope));
                            }
                        }
                    }
                }
                break;
            case Nifti1.NIFTI_TYPE_FLOAT32:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            if (scl_slope == 0) {
                                ecs.writeFloatCorrect((float) (data[k][j][i]));
                            } else {
                                ecs.writeFloatCorrect((float) ((data[k][j][i] - scl_inter) / scl_slope));
                            }
                        }
                    }
                }
                break;
            case Nifti1.NIFTI_TYPE_FLOAT64:
                for (k = 0; k < ZZZ; k++) {
                    for (j = 0; j < YDIM; j++) {
                        for (i = 0; i < XDIM; i++) {
                            if (scl_slope == 0) {
                                ecs.writeDoubleCorrect(data[k][j][i]);
                            } else {
                                ecs.writeDoubleCorrect((data[k][j][i] - scl_inter) / scl_slope);
                            }
                        }
                    }
                }
                break;




            case Nifti1.DT_NONE:
            case Nifti1.DT_BINARY:
            case Nifti1.NIFTI_TYPE_COMPLEX64:
            case Nifti1.NIFTI_TYPE_FLOAT128:
            case Nifti1.NIFTI_TYPE_RGB24:
            case Nifti1.NIFTI_TYPE_COMPLEX128:
            case Nifti1.NIFTI_TYPE_COMPLEX256:
            case Nifti1.DT_ALL:
            default:
                throw new IOException("Sorry, cannot yet write nifti-1 datatype " + Nifti1.decodeDatatype(datatype));

        }


        writer.writeVolBlob(baos, ttt);
        rawvol = baos.toByteArray();
        loadedT = ttt;
        vol = this.readDoubleVol(ttt);
        ecs.close();

        return;
    }



}
