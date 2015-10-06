/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util.nii;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class NiftiWriter {

    protected NiftiHeader header;

    public NiftiWriter() {
        //empty constructor for convenience
    }

    public NiftiWriter(NiftiHeader header) {
        this.header = header;
    }

    public void setHeader(NiftiHeader header) {
        this.header = header;
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Write header information to disk file
     * @exception IOException
     * @exception FileNotFoundException
     */
    public void writeHeader() throws IOException, FileNotFoundException {

        EndianCorrectOutputStream ecs;
        ByteArrayOutputStream baos;
        FileOutputStream fos;
        short s, ss[];
        byte b, bb[], ext_blob[];
        int hsize;
        int i, n;
        int extlist[][];


        // header is 348 except nii and anz/hdr w/ extensions is 352
        hsize = Nifti1.ANZ_HDR_SIZE;
        if ((header.isDs_is_nii()) || (header.getExtension()[0] != 0)) {
            hsize += 4;
        }

        try {

            baos = new ByteArrayOutputStream(hsize);
            fos = new FileOutputStream(header.getDs_hdrname());

            ecs = new EndianCorrectOutputStream(baos, header.isBig_endian());


            ecs.writeIntCorrect(header.getSizeof_hdr());

            if (header.getData_type_string().length() >= 10) {
                ecs.writeBytes(header.getData_type_string().substring(0, 10));
            } else {
                ecs.writeBytes(header.getData_type_string().toString());
                for (i = 0; i < (10 - header.getData_type_string().length()); i++) {
                    ecs.writeByte(0);
                }
            }

            if (header.getDb_name().length() >= 18) {
                ecs.writeBytes(header.getDb_name().substring(0, 18));
            } else {
                ecs.writeBytes(header.getDb_name().toString());
                for (i = 0; i < (18 - header.getDb_name().length()); i++) {
                    ecs.writeByte(0);
                }
            }

            ecs.writeIntCorrect(header.getExtents());

            ecs.writeShortCorrect(header.getSession_error());

            ecs.writeByte((int) header.getRegular().charAt(0));

            b = packDimInfo(header.getFreq_dim(), header.getPhase_dim(), header.getSlice_dim());
            ecs.writeByte((int) b);

            for (i = 0; i < 8; i++) {
                ecs.writeShortCorrect(header.getDim()[i]);
            }

            for (i = 0; i < 3; i++) {
                ecs.writeFloatCorrect(header.getIntent()[i]);
            }

            ecs.writeShortCorrect(header.getIntent_code());

            ecs.writeShortCorrect(header.getDatatype());

            ecs.writeShortCorrect(header.getBitpix());

            ecs.writeShortCorrect(header.getSlice_start());

            for (i = 0; i < 8; i++) {
                ecs.writeFloatCorrect(header.getPixdim()[i]);
            }


            ecs.writeFloatCorrect(header.getVox_offset());

            ecs.writeFloatCorrect(header.getScl_slope());
            ecs.writeFloatCorrect(header.getScl_inter());

            ecs.writeShortCorrect(header.getSlice_end());

            ecs.writeByte((int) header.getSlice_code());

            ecs.writeByte((int) packUnits(header.getXyz_unit_code(), header.getT_unit_code()));


            ecs.writeFloatCorrect(header.getCal_max());
            ecs.writeFloatCorrect(header.getCal_min());

            ecs.writeFloatCorrect(header.getSlice_duration());

            ecs.writeFloatCorrect(header.getToffset());

            ecs.writeIntCorrect(header.getGlmax());
            ecs.writeIntCorrect(header.getGlmin());

            ecs.write(setStringSize(header.getDescrip(), 80), 0, 80);
            ecs.write(setStringSize(header.getAux_file(), 24), 0, 24);


            ecs.writeShortCorrect(header.getQform_code());
            ecs.writeShortCorrect(header.getSform_code());

            for (i = 0; i < 3; i++) {
                ecs.writeFloatCorrect(header.getQuatern()[i]);
            }
            for (i = 0; i < 3; i++) {
                ecs.writeFloatCorrect(header.getQoffset()[i]);
            }

            for (i = 0; i < 4; i++) {
                ecs.writeFloatCorrect(header.getSrow_x()[i]);
            }
            for (i = 0; i < 4; i++) {
                ecs.writeFloatCorrect(header.getSrow_y()[i]);
            }
            for (i = 0; i < 4; i++) {
                ecs.writeFloatCorrect(header.getSrow_z()[i]);
            }


            ecs.write(setStringSize(header.getIntent_name(), 16), 0, 16);
            ecs.write(setStringSize(header.getMagic(), 4), 0, 4);


            // nii or anz/hdr w/ ext. gets 4 more
            if ((header.isDs_is_nii()) || (header.getExtension()[0] != 0)) {
                for (i = 0; i < 4; i++) {
                    ecs.writeByte((int) header.getExtension()[i]);
                }
            }

            /** write the header blob to disk */
            baos.writeTo(fos);

        } catch (IOException ex) {
            throw new IOException("Error: unable to write header file " + header.getDs_hdrname() + ": " + ex.getMessage());
        }



        /** write the extension blobs **/
        try {

            ////// extensions
            if (header.getExtension()[0] != 0) {

                baos = new ByteArrayOutputStream(Nifti1.EXT_KEY_SIZE);
                ecs = new EndianCorrectOutputStream(baos, header.isBig_endian());
                extlist = header.getExtensionsList();
                n = extlist.length;
                for (i = 0; i < n; i++) {
                    // write size, code
                    ecs.writeIntCorrect(extlist[i][0]);
                    ecs.writeIntCorrect(extlist[i][1]);
                    baos.writeTo(fos);
                    baos.reset();

                    // write data blob
                    ext_blob = (byte[]) header.getExtension_blobs().get(i);
                    fos.write(ext_blob, 0, extlist[i][0] - Nifti1.EXT_KEY_SIZE);
                }
            }

            fos.close();
        } catch (IOException ex) {
            throw new IOException("Error: unable to write header extensions for file " + header.getDs_hdrname() + ": " + ex.getMessage());
        }


    }

    private byte packDimInfo(short freq, short phase, short slice) {

        int i = 0;

        i = (i & ((int) (slice) & 3)) << 2;
        i = (i & ((int) (phase) & 3)) << 2;
        i = (i & ((int) (freq) & 3));
        return ((byte) i);
    }
    
    private byte packUnits(short space, short time) {


        return ((byte) (((int) (space) & 007) | ((int) (time) & 070)));
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Write one 3D volume blob from byte array to disk
     * @param baos -- blob of bytes to write
     * @param ttt T dimension of vol to write (0 based index)
     * @exception IOException
     */
    public void writeVolBlob(ByteArrayOutputStream baos, short ttt) throws IOException {

        RandomAccessFile raf;
        short ZZZ;
        long skip_head, skip_data;

        // for 2D volumes, zdim may be 0
        ZZZ = header.getZDIM();
        if (header.getDim()[0] == 2) {
            ZZZ = 1;
        }

        skip_head = (long) header.getVox_offset();
        skip_data = (long) (ttt * header.getXDIM() * header.getYDIM() * ZZZ * Nifti1.bytesPerVoxel(header.getDatatype()));

        //System.out.println("\nwriteVolBlob "+ttt+" skip_head: "+skip_head+"  skip_data: "+skip_data);



        // can't write random access compressed yet
        if (header.getDs_datname().endsWith(".gz")) {
            throw new IOException("Sorry, can't write to compressed image data file: " + header.getDs_datname());
        } // write data blob
        else {
            raf = new RandomAccessFile(header.getDs_datname(), "rwd");
            raf.seek(skip_head + skip_data);
            raf.write(baos.toByteArray());
            raf.close();
        }


    }

    //////////////////////////////////////////////////////////////////
	/*
     * truncate or pad a string to make it the needed length
     * @param s input string
     * @param n desired length
     * @return s String of length n with as much of s as possible, padded
     * with 0 if necessary
     */
    private byte[] setStringSize(StringBuffer s, int n) {

        byte b[];
        int i, slen;

        slen = s.length();

        if (slen >= n) {
            return (s.toString().substring(0, n).getBytes());
        }

        b = new byte[n];
        for (i = 0; i < slen; i++) {
            b[i] = (byte) s.charAt(i);
        }
        for (i = slen; i < n; i++) {
            b[i] = 0;
        }

        return (b);
    }

    /**
     * Write a byte array of data to disk, starting at vox_offset,
     * beginning of image data.  It is assumed that usually the entire
     * data array will be written with this call.
     * Note that since the data is handled as bytes, NO byte swapping
     * will be performed.  Applications needing byteswapping must
     * swap the bytes correctly in the input array b.
     * @param b byte array of image data
     * @exception IOException
     */
    public void writeData(byte b[]) throws IOException {

        RandomAccessFile raf;
        int skip_head;

        float vox_offset = header.getVox_offset();
        String ds_datname = header.getDs_datname();

        skip_head = (int) vox_offset;


        // can't write random access compressed yet
        if (ds_datname.endsWith(".gz")) {
            throw new IOException("Sorry, can't write to compressed image data file: " + ds_datname);
        }


        // write data blob
        raf = new RandomAccessFile(ds_datname, "rwd");
        raf.seek(skip_head);
        raf.write(b, 0, b.length);
        raf.close();

    }

}
