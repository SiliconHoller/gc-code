/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util.nii;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;


/**
 * Common read functions for NiftiData.
 * @author David Days <david.c.days@gmail.com>
 */
public class NiftiReader {

    protected NiftiHeader header;

    public NiftiReader() {
        //Empty constructor for convenience
    }

    public NiftiReader(NiftiHeader header) {
        this.header = header;
    }

    public void setHeader(NiftiHeader header) {
        this.header = header;
    }


    /**
     * Read header information into memory
     * @exception IOException
     * @exception FileNotFoundException
     */
    public void readHeader() throws IOException, FileNotFoundException {

        DataInputStream dis;
        EndianCorrectInputStream ecs;
        short s, ss[];
        byte bb[];
        int i;

        String ds_hdrname = header.getDs_hdrname();
        if (ds_hdrname.endsWith(".gz")) {
            dis = new DataInputStream(new GZIPInputStream(new FileInputStream(ds_hdrname)));
        } else {
            dis = new DataInputStream(new FileInputStream(ds_hdrname));
        }
        try {

            ///// first, read dim[0] to get endian-ness
            dis.skipBytes(40);
            s = dis.readShort();
            dis.close();
            if ((s < 1) || (s > 7)) {
                header.setBig_endian(false);
            } else {
                header.setBig_endian(true);
            }


            ///// get input stream that will flip bytes if necessary
            if (ds_hdrname.endsWith(".gz")) {
                ecs = new EndianCorrectInputStream(new GZIPInputStream(new FileInputStream(ds_hdrname)), header.isBig_endian());
            } else {
                ecs = new EndianCorrectInputStream(ds_hdrname, header.isBig_endian());
            }

            header.setSizeof_hdr(ecs.readIntCorrect());

            bb = new byte[10];
            ecs.readFully(bb, 0, 10);
            header.setData_type_string(new StringBuffer(new String(bb)));

            bb = new byte[18];
            ecs.readFully(bb, 0, 18);
            header.setDb_name(new StringBuffer(new String(bb)));

            header.setExtents(ecs.readIntCorrect());

            header.setSession_error(ecs.readShortCorrect());

            StringBuffer regular = new StringBuffer();
            regular.append((char) (ecs.readUnsignedByte()));
            header.setRegular(regular);

            StringBuffer dim_info = new StringBuffer();
            dim_info.append((char) (ecs.readUnsignedByte()));
            header.setDim_info(dim_info);

            ss = unpackDimInfo((int) dim_info.charAt(0));
            header.setFreq_dim(ss[0]);
            header.setPhase_dim(ss[1]);
            header.setSlice_dim(ss[2]);

            short[] dim = header.getDim();

            for (i = 0; i < 8; i++) {
                dim[i] = ecs.readShortCorrect();
            }
            if (dim[0] > 0) {
                header.setXDIM(dim[1]);
            }
            if (dim[0] > 1) {
                header.setYDIM(dim[2]);
            }
            if (dim[0] > 2) {
                header.setZDIM(dim[3]);
            }
            if (dim[0] > 3) {
                header.setTDIM(dim[4]);
            }

            float[] intent = header.getIntent();
            for (i = 0; i < 3; i++) {
                intent[i] = ecs.readFloatCorrect();
            }

            header.setIntent_code(ecs.readShortCorrect());

            header.setDatatype(ecs.readShortCorrect());

            header.setBitpix(ecs.readShortCorrect());

            header.setSlice_start(ecs.readShortCorrect());

            float[] pixdim = header.getPixdim();
            for (i = 0; i < 8; i++) {
                pixdim[i] = ecs.readFloatCorrect();
            }
            header.setQfac((short) Math.floor((double) (pixdim[0])));

            header.setVox_offset(ecs.readFloatCorrect());

            header.setScl_slope(ecs.readFloatCorrect());
            header.setScl_inter(ecs.readFloatCorrect());

            header.setSlice_end(ecs.readShortCorrect());

            header.setSlice_code((byte) ecs.readUnsignedByte());

            header.setXyzt_units((byte) ecs.readUnsignedByte());
            ss = unpackUnits((int) header.getXyzt_units());
            header.setXyz_unit_code(ss[0]);
            header.setT_unit_code(ss[1]);

            header.setCal_max(ecs.readFloatCorrect());
            header.setCal_min(ecs.readFloatCorrect());

            header.setSlice_duration(ecs.readFloatCorrect());

            header.setToffset(ecs.readFloatCorrect());

            header.setGlmax(ecs.readIntCorrect());
            header.setGlmin(ecs.readIntCorrect());

            bb = new byte[80];
            ecs.readFully(bb, 0, 80);
            header.setDescrip(new StringBuffer(new String(bb)));

            bb = new byte[24];
            ecs.readFully(bb, 0, 24);
            header.setAux_file(new StringBuffer(new String(bb)));

            header.setQform_code(ecs.readShortCorrect());
            header.setSform_code(ecs.readShortCorrect());

            float[] quatern = header.getQuatern();
            for (i = 0; i < 3; i++) {
                quatern[i] = ecs.readFloatCorrect();
            }
            float[] qoffset = header.getQoffset();
            for (i = 0; i < 3; i++) {
                qoffset[i] = ecs.readFloatCorrect();
            }

            float[] srow_x = header.getSrow_x();
            for (i = 0; i < 4; i++) {
                srow_x[i] = ecs.readFloatCorrect();
            }
            float[] srow_y = header.getSrow_y();
            for (i = 0; i < 4; i++) {
                srow_y[i] = ecs.readFloatCorrect();
            }
            float[] srow_z = header.getSrow_z();
            for (i = 0; i < 4; i++) {
                srow_z[i] = ecs.readFloatCorrect();
            }


            bb = new byte[16];
            ecs.readFully(bb, 0, 16);
            header.setIntent_name(new StringBuffer(new String(bb)));

            bb = new byte[4];
            ecs.readFully(bb, 0, 4);
            header.setMagic(new StringBuffer(new String(bb)));

        } catch (IOException ex) {
            throw new IOException("Error: unable to read header file " + ds_hdrname + ": " + ex.getMessage());
        }


        /////// Read possible extensions
        if (header.isDs_is_nii()) {
            readNiiExt(ecs);
        } else {
            readNp1Ext(ecs);
        }


        ecs.close();


    }


    /**
     * Read extension data from nii (1 file) dataset
     * @param ecs is an InputStream open and pointing to begining of
     * 	extensions array
     * @exception IOException
     */
    private void readNiiExt(EndianCorrectInputStream ecs) throws IOException {

        int size_code[];
        int start_addr;
        byte eblob[];

        // read 4 ext bytes if it is nii, bytes 348-351 must be there
        try {
            ecs.readFully(header.getExtension(), 0, 4);
        } catch (IOException ex) {
            throw new IOException("Error: i/o error reading extension bytes on header file " + header.getDs_hdrname() + ": " + ex.getMessage());
        }


        /// jump thru extensions getting sizes and codes
        size_code = new int[2];
        if (header.getExtension()[0] != (byte) 0) {
            start_addr = Nifti1.ANZ_HDR_SIZE + 4;

            size_code[0] = 0;
            size_code[1] = 0;

            /// in nii files, vox_offset is end of hdr/ext,
            /// beginning of data.
            int vox_offset = (int)header.getVox_offset();
            while (start_addr < vox_offset) {
                try {
                    size_code = new int[2];
                    size_code[0] = ecs.readIntCorrect();
                    size_code[1] = ecs.readIntCorrect();
                    eblob = new byte[size_code[0] - Nifti1.EXT_KEY_SIZE];
                    ecs.readFully(eblob, 0, size_code[0] - Nifti1.EXT_KEY_SIZE);
                    header.getExtension_blobs().add(eblob);
                } catch (IOException ex) {
                    header.printHeader();
                    throw new EOFException("Error: i/o error reading extension data for extension " + (header.getExtensions_list().size() + 1) + " on header file " + header.getDs_hdrname() + ": " + ex.getMessage());
                }

                header.getExtensions_list().add(size_code);
                start_addr += (size_code[0]);

                // check if extensions appeared to overrun data blob
                // when extensions are done, start_addr should == vox_offset
                if (start_addr > (int) vox_offset) {
                    header.printHeader();
                    throw new IOException("Error: Data  for extension " + (header.getExtensions_list().size()) + " on header file " + header.getDs_hdrname() + " appears to overrun start of image data.");
                }
            } // while not yet at data blob

        }	// if there are extensions



    }

    /**
     * Read extension data from n+1 (2 file) dataset
     * @param ecs is an InputStream open and pointing to begining of
     * 	extensions array
     * @exception IOException
     */
    private void readNp1Ext(EndianCorrectInputStream ecs) throws IOException, EOFException {

        int size_code[];
        byte eblob[];


        // read 4 ext bytes if it is n+1, bytes 348-351 do NOT
        // need to be there
        try {
            ecs.readFully(header.getExtension(), 0, 4);
        } catch (EOFException ex) {

        } catch (IOException ex) {
            throw new IOException("Error: i/o error reading extension bytes on header file " + header.getDs_hdrname() + ": " + ex.getMessage());
        }


        /// jump thru extensions getting sizes and codes
        size_code = new int[2];
        if (header.getExtension()[0] != (byte) 0) {

            size_code[0] = 0;
            size_code[1] = 0;

            /// in np1 files, read to end of hdr file looking
            /// for extensions
            while (true) {
                try {
                    size_code = new int[2];
                    size_code[0] = ecs.readIntCorrect();
                    size_code[1] = ecs.readIntCorrect();
                    eblob = new byte[size_code[0] - Nifti1.EXT_KEY_SIZE];
                    ecs.readFully(eblob, 0, size_code[0] - Nifti1.EXT_KEY_SIZE);
                    header.getExtension_blobs().add(eblob);
                } catch (EOFException ex) {
                    return;
                } catch (IOException ex) {
                    throw new EOFException("Error: i/o error reading extension data for extension " + (header.getExtensions_list().size() + 1) + " on header file " + header.getDs_hdrname() + ": " + ex.getMessage());
                }

                header.getExtensions_list().add(size_code);
            } // while not yet at EOF

        }	// if there are extensions



    }

    ////////////////////////////////////////////////////////////////////
    //
    // Unpack/pack the 3 bitfields in the dim_info char
    //	bits 0,1 freq_dim
    //	bits 2,3 phase_dim
    //	bits 4,5 slice_dim
    //
    ////////////////////////////////////////////////////////////////////
    private short[] unpackDimInfo(int b) {
        short s[];

        s = new short[3];
        s[0] = (short) (b & 3);
        s[1] = (short) ((b >>> 2) & 3);
        s[2] = (short) ((b >>> 4) & 3);
        return s;
    }

    ////////////////////////////////////////////////////////////////////
    //
    // Unpack/pack the 2 bitfields in the xyzt_units field
    //	bits 0,1,2 are the code for units for xyz
    //	bits 3,4,5 are the code for units for t, no need to shift
    //	bits for t code, the code is a multiple of 8.
    //
    ////////////////////////////////////////////////////////////////////
    private short[] unpackUnits(int b) {
        short s[];

        s = new short[2];
        s[0] = (short) (b & 007);
        s[1] = (short) (b & 070);
        return s;
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Read one 3D volume blob from disk to byte array
     * ttt T dimension of vol to read (0 based index)
     * @return byte array
     * @exception IOException
     */
    public byte[] readVolBlob(short ttt) throws IOException {

        byte b[];
        RandomAccessFile raf;
        BufferedInputStream bis;
        short ZZZ;
        long skip_head, skip_data;
        int blob_size;

        // for 2D volumes, zdim may be 0
        ZZZ = header.getZDIM();
        if (header.getDim()[0] == 2) {
            ZZZ = 1;
        }

        blob_size = header.getXDIM() * header.getYDIM() * ZZZ * Nifti1.bytesPerVoxel(header.getDatatype());
        b = new byte[blob_size];

        skip_head = (long) header.getVox_offset();
        skip_data = (long) (ttt * blob_size);

        ///System.out.println("\nblob_size: "+blob_size+"  skip_head: "+skip_head+"  skip_data: "+skip_data);



        // read the volume from disk into a byte array
        // read compressed data with BufferedInputStream
        if (header.getDs_datname().endsWith(".gz")) {
            bis = new BufferedInputStream(new GZIPInputStream(new FileInputStream(header.getDs_datname())));
            bis.skip(skip_head + skip_data);
            bis.read(b, 0, blob_size);
            bis.close();
        } // read uncompressed data with RandomAccessFile
        else {
            raf = new RandomAccessFile(header.getDs_datname(), "r");
            raf.seek(skip_head + skip_data);
            raf.readFully(b, 0, blob_size);
            raf.close();
        }

        return b;
    }


    /**
     * check the input filename for anz/nii extensions, gz compression
     * extension so as to get the actual disk filenames for hdr and data
     * The purpose of this routine is to find the file(s) the user
     * means, letting them specify with a variety of extensions, eg
     * they may or may not add the /img/hdr/nii extensions, may or may
     * not add the .gz extension.
     *
     * @param name The string the user gave as input file
     *
     *
     */
    public void checkName(String name) {

        String wname;
        File f, nii_file, niig_file, anzh_file, anzhg_file, anzd_file, anzdg_file;

        wname = String.valueOf(name.toCharArray());


        // strip off .gz suffix, will determine gz by disk filenames
        if (wname.endsWith(Nifti1.GZIP_EXT)) {
            wname = wname.substring(0, wname.length() - Nifti1.GZIP_EXT.length());
        }

        // get filenames if only basename is specified
        // note that the checkName routine is not checking whether a
        // valid dataset exists (use exists() for that).  But, if only
        // a basename was specified, we need to check for existing
        // files to see if they meant .anz .hdr .nii etc..
        if (!wname.endsWith(Nifti1.ANZ_HDR_EXT) && !wname.endsWith(Nifti1.ANZ_DAT_EXT) && !wname.endsWith(Nifti1.NI1_EXT)) {
            // files to look for
            nii_file = new File(wname + Nifti1.NI1_EXT);
            niig_file = new File(wname + Nifti1.NI1_EXT + Nifti1.GZIP_EXT);
            anzh_file = new File(wname + Nifti1.ANZ_HDR_EXT);
            anzhg_file = new File(wname + Nifti1.ANZ_HDR_EXT + Nifti1.GZIP_EXT);
            anzd_file = new File(wname + Nifti1.ANZ_DAT_EXT);
            anzdg_file = new File(wname + Nifti1.ANZ_DAT_EXT + Nifti1.GZIP_EXT);

            if (nii_file.exists()) {
                wname = wname + Nifti1.NI1_EXT;
            } else if (niig_file.exists()) {
                wname = wname + Nifti1.NI1_EXT;
            } else if (anzh_file.exists()) {
                wname = wname + Nifti1.ANZ_HDR_EXT;
            } else if (anzhg_file.exists()) {
                wname = wname + Nifti1.ANZ_HDR_EXT;
            } else if (anzd_file.exists()) {
                wname = wname + Nifti1.ANZ_HDR_EXT;
            } else if (anzdg_file.exists()) {
                wname = wname + Nifti1.ANZ_HDR_EXT;
            }
        }

        ///// if an anz or nii suffix is given, use that to find file names
        if (wname.endsWith(Nifti1.ANZ_HDR_EXT)) {
            header.setDs_hdrname(String.valueOf(wname.toCharArray()));
            header.setDs_datname(wname.substring(0, wname.length() - Nifti1.ANZ_HDR_EXT.length()) + Nifti1.ANZ_DAT_EXT);
        } else if (wname.endsWith(Nifti1.ANZ_DAT_EXT)) {
            header.setDs_datname(String.valueOf(wname.toCharArray()));
            header.setDs_hdrname(wname.substring(0, wname.length() - Nifti1.ANZ_DAT_EXT.length()) + Nifti1.ANZ_HDR_EXT);
        } else if (wname.endsWith(Nifti1.NI1_EXT)) {
            header.setDs_datname(String.valueOf(wname.toCharArray()));
            header.setDs_hdrname(String.valueOf(wname.toCharArray()));
            header.setDs_is_nii(true);
        }


        /// add gz suffix if gzipped file exists
        f = new File(header.getDs_hdrname() + Nifti1.GZIP_EXT);
        if (f.exists()) {
            header.setDs_hdrname(header.getDs_hdrname() + Nifti1.GZIP_EXT);
        }
        f = new File(header.getDs_datname() + Nifti1.GZIP_EXT);
        if (f.exists()) {
            header.setDs_datname(header.getDs_datname() + Nifti1.GZIP_EXT);
        }


    }

    /**
     * Read one 1D timecourse from a 4D dataset, ie all T values for
     * a given XYZ location.  Scaling is applied.
     * @param x X dimension of vol to read (0 based index)
     * @param y Y dimension of vol to read (0 based index)
     * @param z Z dimension of vol to read (0 based index)
     * @return 1D double array
     * @exception IOException
     */
    public double[] readDoubleTmcrs(short x, short y, short z) throws IOException {

        EndianCorrectInputStream ecs;
        short ZZZ, i;
        long skip_head, skip_data, skip_vol, skip_vol2;
        double data[];

        // for 2D volumes, zdim may be 0
        ZZZ = header.getZDIM();
        if (header.getDim()[0] == 2) {
            ZZZ = 1;
        }


        data = new double[header.getTDIM()];

        skip_head = (long) (header.getVox_offset());
        skip_data = (long) ((z * header.getXDIM() * header.getYDIM() + y * header.getXDIM() + x) * Nifti1.bytesPerVoxel(header.getDatatype()));
        skip_vol = (header.getXDIM() * header.getYDIM() * ZZZ * Nifti1.bytesPerVoxel(header.getDatatype())) - Nifti1.bytesPerVoxel(header.getDatatype());
        skip_vol2 = 0;

        ///System.out.println("\nskip_head: "+skip_head+"  skip_data: "+skip_data+"  skip_vol: "+skip_vol);



        // get input handle
        if (header.getDs_datname().endsWith(".gz")) {
            ecs = new EndianCorrectInputStream(new GZIPInputStream(new FileInputStream(header.getDs_datname())), header.isBig_endian());
        } else {
            ecs = new EndianCorrectInputStream(new FileInputStream(header.getDs_datname()), header.isBig_endian());
        }


        // skip header stuff and data up to 1st voxel
        ecs.skip((int) (skip_head + skip_data));

        short TDIM = header.getTDIM();
        // loop over all timepoints
        for (i = 0; i < TDIM; i++) {

            // skip 0 first time, then vol skip
            ecs.skip(skip_vol2);
            skip_vol2 = skip_vol;

            short datatype = header.getDatatype();
            // read voxel value, convert to double, fix sign, scale
            switch (datatype) {

                case Nifti1.NIFTI_TYPE_INT8:
                case Nifti1.NIFTI_TYPE_UINT8:
                    data[i] = (double) (ecs.readByte());
                    if ((datatype == Nifti1.NIFTI_TYPE_UINT8) && (data[i] < 0)) {
                        data[i] = Math.abs(data[i]) + (double) (1 << 7);
                    }
                    if (header.getScl_slope() != 0) {
                        data[i] = data[i] * header.getScl_slope() + header.getScl_inter();
                    }
                    break;

                case Nifti1.NIFTI_TYPE_INT16:
                case Nifti1.NIFTI_TYPE_UINT16:
                    data[i] = (double) (ecs.readShortCorrect());
                    if ((datatype == Nifti1.NIFTI_TYPE_UINT16) && (data[i] < 0)) {
                        data[i] = Math.abs(data[i]) + (double) (1 << 15);
                    }
                    if (header.getScl_slope() != 0) {
                        data[i] = data[i] * header.getScl_slope() + header.getScl_inter();
                    }
                    break;

                case Nifti1.NIFTI_TYPE_INT32:
                case Nifti1.NIFTI_TYPE_UINT32:
                    data[i] = (double) (ecs.readIntCorrect());
                    if ((datatype == Nifti1.NIFTI_TYPE_UINT32) && (data[i] < 0)) {
                        data[i] = Math.abs(data[i]) + (double) (1 << 31);
                    }
                    if (header.getScl_slope() != 0) {
                        data[i] = data[i] * header.getScl_slope() + header.getScl_inter();
                    }
                    break;


                case Nifti1.NIFTI_TYPE_INT64:
                case Nifti1.NIFTI_TYPE_UINT64:
                    data[i] = (double) (ecs.readLongCorrect());
                    if ((datatype == Nifti1.NIFTI_TYPE_UINT64) && (data[i] < 0)) {
                        data[i] = Math.abs(data[i]) + (double) (1 << 63);
                    }
                    if (header.getScl_slope() != 0) {
                        data[i] = data[i] * header.getScl_slope() + header.getScl_inter();
                    }
                    break;


                case Nifti1.NIFTI_TYPE_FLOAT32:
                    data[i] = (double) (ecs.readFloatCorrect());
                    if (header.getScl_slope() != 0) {
                        data[i] = data[i] * header.getScl_slope() + header.getScl_inter();
                    }
                    break;


                case Nifti1.NIFTI_TYPE_FLOAT64:
                    data[i] = (double) (ecs.readDoubleCorrect());
                    if (header.getScl_slope() != 0) {
                        data[i] = data[i] * header.getScl_slope() + header.getScl_inter();
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



        }  // loop over TDIM

        ecs.close();


        return data;
    }

    /**
     * Read all the data into one byte array.
     * Note that since the data is handled as bytes, NO byte swapping
     * has been performed.  Applications converting from the byte
     * array to int/float datatypes will need to swap bytes if
     * necessary.
     * return byte array with all image data
     * @exception IOException
     */
    public byte[] readData() throws IOException {

        int i;
        byte b[];
        GZIPInputStream gis;
        DataInputStream dis;
        long skip_head, blob_size;

        short datatype = header.getDatatype();
        short[] dim = header.getDim();
        float vox_offset = header.getVox_offset();
        /// compute size of data, allocate array
        blob_size = 1;
        for (i = 1; i <= dim[0]; i++) {
            blob_size *= dim[i];
        }
        blob_size *= Nifti1.bytesPerVoxel(datatype);

        if (blob_size > Integer.MAX_VALUE) {
            throw new IOException("\nSorry, cannot yet handle data arrays bigger than " + Integer.MAX_VALUE + " bytes.  " + header.getDs_datname() + " has " + blob_size + " bytes.");
        }

        b = new byte[(int) blob_size];

        skip_head = (long) vox_offset;

        ///System.out.println("\nblob_size: "+blob_size+"  skip_head: "+skip_head);


        // read the volume from disk into a byte array
        if (header.getDs_datname().endsWith(".gz")) {
            dis = new DataInputStream(new GZIPInputStream(new FileInputStream(header.getDs_datname())));
        } else {
            dis = new DataInputStream(new FileInputStream(header.getDs_datname()));
        }

        dis.skipBytes((int) skip_head);
        dis.readFully(b, 0, (int) blob_size);
        dis.close();

        return b;
    }



}
