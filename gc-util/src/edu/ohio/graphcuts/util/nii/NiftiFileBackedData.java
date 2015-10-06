/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util.nii;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * <p>Implementation of NiftiData that reads and writes directly from
 * a file.</p>
 * <p>This implementation is basically a reorganization of the original Nifti1Dataset class,
 * with a few alterations in precisely how the read/write operations are
 * passed to the NiftiReader and NiftiWriter members, as opposed to being
 * handled internally.</p>
 * 
 * @author David Days <david.c.days@gmail.com>
 */
public class NiftiFileBackedData extends NiftiData {

    protected NiftiFileBackedData() {
        super();
    }

    /**
     * Performs no operations because all methods immediately read/write to disk.
     * @throws IOException
     */
    @Override
    protected void loadData() throws IOException {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Add an extension stored in a file to a header
     * @param code -- code identifying the extension
     * @param filename -- filename containing the extension.  The entire
     * file will be added as an extension
     */
    public void addExtension(int code, String filename) throws IOException {

        File f;
        long l;
        int size_code[] = new int[2];
        DataInputStream dis;
        byte b[];
        int i, il, pad;

        f = new File(filename);
        l = f.length();

        //// check length, compute padding
        //// 8bytes of size+code plus ext data must be mult. of 16
        if (l > Integer.MAX_VALUE) {
            throw new IOException("Error: maximum extension size is " + Integer.MAX_VALUE + "bytes. " + filename + " is " + l + " bytes.");
        }
        il = (int) l;
        pad = (il + Nifti1.EXT_KEY_SIZE) % 16;
        if (pad != 0) {
            pad = 16 - pad;
        }
        ///System.out.println("\next file size is "+l+", padding with "+pad);

        /// read the extension data from the file
        b = new byte[il + pad];
        try {
            dis = new DataInputStream(new FileInputStream(filename));
            dis.readFully(b, 0, il);
            dis.close();
        } catch (IOException ex) {
            throw new IOException("Error reading extension data for " + header.getDs_hdrname() + " from file " + filename + ". :" + ex.getMessage());
        }
        for (i = il; i < il + pad; i++) {
            b[i] = 0;
        }


        Vector extensions_list = header.getExtensions_list();
        Vector extension_blobs = header.getExtension_blobs();
        byte[] extension = header.getExtension();
        /// well, if we got this far, I guess we really have to add it
        size_code[0] = il + pad + Nifti1.EXT_KEY_SIZE;
        size_code[1] = code;
        extensions_list.add(size_code);
        extension_blobs.add(b);
        extension[0] = 1;


        // update vox_offset for nii files
        if (header.isDs_is_nii()) {
            float vox_offset = header.getVox_offset();
            vox_offset += size_code[0];
            header.setVox_offset(vox_offset);
        }



    }

    /**
     * Read one 3D volume from disk and return it as 3D double array
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
        byte b[];
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

        // read bytes from disk
        b = reader.readVolBlob(ttt);

        // read the correct datatypes from the byte array
        // undo signs if necessary, add scaling
        ecs = new EndianCorrectInputStream(new ByteArrayInputStream(b), big_endian);
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
        b = null;


        return (data);
    }


    /**
     * Write one 3D double array to disk. Data is written in datatype
     * of this Nifti1Dataset instance.  Data is "un-scaled" as per
     * scale/offset fields of this Nifti1Dataset instance, before being
     * written to disk.
     * @param data 3D array of array data
     * Array indices are [Z][Y][X], assuming an xyzt ordering of dimensions.
     * ie  indices are data[dim[3]][dim[2]][dim[1]]
     * @param ttt T dimension of vol to write (0 based index)
     * @exception IOException
     *
     */
    public void writeVol(double data[][][], short ttt) throws IOException {

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
        ecs.close();

        return;
    }


}
