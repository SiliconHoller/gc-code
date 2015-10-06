package edu.ohio.graphcuts.util.nii;

import java.nio.ByteOrder;
import java.text.NumberFormat;
import java.util.Vector;

/** 
 * NiftiHeader is a refactored version of Nifti1Dataset.  The IO functions have been
 * removed and placed in the appropriate NiftiReader or NiftiWriter.
 *
 */
public class NiftiHeader {
    // variables for fields in the nifti header
    protected String ds_hdrname;	// 4 byte array, byte 0 is 0/1 indicating extensions or not
    protected String ds_datname;
    protected boolean ds_is_nii;
    protected boolean big_endian;
    protected short XDIM;
    protected short YDIM;
    protected short ZDIM;
    protected short TDIM;
    protected short DIM5;
    protected short DIM6;
    protected short DIM7;
    protected short freq_dim;
    protected short phase_dim;
    protected short slice_dim;
    protected short xyz_unit_code;
    protected short t_unit_code;
    protected short qfac;
    protected Vector extensions_list;
    protected Vector extension_blobs;
    protected int sizeof_hdr;
    protected StringBuffer data_type_string;
    protected StringBuffer db_name;
    protected int extents;
    protected short session_error;
    protected StringBuffer regular;
    protected StringBuffer dim_info;
    protected short[] dim;
    protected float[] intent;
    protected short intent_code;
    protected short datatype;
    protected short bitpix;
    protected short slice_start;
    protected float[] pixdim;
    protected float vox_offset;
    protected float scl_slope;
    protected float scl_inter;
    protected short slice_end;
    protected byte slice_code;
    protected byte xyzt_units;
    protected float cal_max;
    protected float cal_min;
    protected float slice_duration;
    protected float toffset;
    protected int glmax;
    protected int glmin;
    protected StringBuffer descrip;
    protected StringBuffer aux_file;
    protected short qform_code;
    protected short sform_code;
    protected float[] quatern;
    protected float[] qoffset;
    protected float[] srow_x;
    protected float[] srow_y;
    protected float[] srow_z;
    protected StringBuffer intent_name;
    protected StringBuffer magic;
    protected byte[] extension;



    //////////////////////////////////////////////////////////////////
    /**
     * Constructor for creation of a new dataset.  Default values are
     * set, programmer must set or reset all fields appropriate for
     * the new dataset.
     *
     */
    public NiftiHeader() {

        setDefaults();

        
    }


    ////////////////////////////////////////////////////////////////////
    //
    // Copy all in memory header field settings from datset A to this dataset
    // Extension data not set, fields set to no extension
    //
    ////////////////////////////////////////////////////////////////////
    public void copyHeader(NiftiHeader A) {

        int i;

        setDs_hdrname(String.valueOf(A.getDs_hdrname().toCharArray()));
        setDs_datname(String.valueOf(A.getDs_datname().toCharArray()));
        setDs_is_nii(A.isDs_is_nii());
        setBig_endian(A.isBig_endian());
        setSizeof_hdr(A.getSizeof_hdr());
        setData_type_string(new StringBuffer(A.getData_type_string().toString()));
        setDb_name(new StringBuffer(A.getDb_name().toString()));
        setExtents(A.getExtents());
        setSession_error(A.getSession_error());
        setRegular(new StringBuffer(A.getRegular().toString()));
        setDim_info(new StringBuffer(A.getDim_info().toString()));
        setFreq_dim(A.getFreq_dim());
        setPhase_dim(A.getPhase_dim());
        setSlice_dim(A.getSlice_dim());
        for (i = 0; i < 8; i++) {
            getDim()[i] = A.getDim()[i];
        }
        setXDIM(A.getXDIM());
        setYDIM(A.getYDIM());
        setZDIM(A.getZDIM());
        setTDIM(A.getTDIM());
        setDIM5(A.getDIM5());
        setDIM6(A.getDIM6());
        setDIM7(A.getDIM7());
        for (i = 0; i < 3; i++) {
            getIntent()[i] = A.getIntent()[i];
        }
        setIntent_code(A.getIntent_code());
        setDatatype(A.getDatatype());
        setBitpix(A.getBitpix());
        setSlice_start(A.getSlice_start());
        setQfac((short) 1);
        for (i = 0; i < 8; i++) {
            getPixdim()[i] = A.getPixdim()[i];
        }

        setVox_offset(A.getVox_offset());
        setScl_slope(A.getScl_slope());
        setScl_inter(A.getScl_inter());
        setSlice_end(A.getSlice_end());
        setSlice_code(A.getSlice_code());
        setXyzt_units(A.getXyzt_units());
        setXyz_unit_code(A.getXyz_unit_code());
        setT_unit_code(A.getT_unit_code());

        setCal_max(A.getCal_max());
        setCal_min(A.getCal_min());
        setSlice_duration(A.getSlice_duration());
        setToffset(A.getToffset());
        setGlmax(A.getGlmax());
        setGlmin(A.getGlmin());

        setDescrip(new StringBuffer(A.getDescrip().toString()));
        setAux_file(new StringBuffer(A.getAux_file().toString()));

        setQform_code(A.getQform_code());
        setSform_code(A.getSform_code());

        for (i = 0; i < 3; i++) {
            getQuatern()[i] = A.getQuatern()[i];
            getQoffset()[i] = A.getQoffset()[i];
        }

        for (i = 0; i < 4; i++) {
            getSrow_x()[i] = A.getSrow_x()[i];
            getSrow_y()[i] = A.getSrow_y()[i];
            getSrow_z()[i] = A.getSrow_z()[i];
        }

        setIntent_name(new StringBuffer(A.getIntent_name().toString()));

        setMagic(new StringBuffer(A.getMagic().toString()));

        for (i = 0; i < 4; i++) {
            getExtension()[i] = (byte) 0;
        }

        
    }



    //////////////////////////////////////////////////////////////////
    /**
     * Get list of extensions and return it as nx2 array
     * @return nx2 array where n = # of extensions, array elem 0
     * is the size in bytes of that extension and array elem 1 is
     * the extension code.
     */
    public int[][] getExtensionsList() {

        int n, i;
        int size_code[];
        int extlist[][];

        size_code = new int[2];
        n = getExtensions_list().size();
        extlist = new int[n][2];


        for (i = 0; i < n; i++) {
            size_code = (int[]) getExtensions_list().get(i);
            extlist[i][0] = size_code[0];
            extlist[i][1] = size_code[1];
        }

        return (extlist);
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Remove an extension from a header
     * @param index number of the extension to remove (0 based)
     */
    public void removeExtension(int index) {

        int n;
        int size_code[] = new int[2];

        n = getExtensions_list().size();

        if (index >= n) {
            System.out.println("\nERROR: could not remove extension " + index + 1 + " from " + getDs_hdrname() + ". It only has " + n + " extensions.");
            return;
        }

        // remove extension from lists
        size_code = (int[]) getExtensions_list().get(index);
        getExtensions_list().remove(index);
        getExtension_blobs().remove(index);

        // readjust vox_offset if necessary
        if (isDs_is_nii()) {
            setVox_offset(getVox_offset() - size_code[0]);
        }

        
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Add an extension stored in a file to a header
     * @param code -- code identifying the extension
     * @param filename -- filename containing the extension.  The entire
     * file will be added as an extension
     */
    public void addExtension(int code, String filename) {

        throw new UnsupportedOperationException("Not implemented yet.");
    }

     //////////////////////////////////////////////////////////////////
    /**
     * Print header information to standard out.
     */
    public void printHeader() {

        int i;
        int extlist[][], n;

        System.out.println("\n");
        System.out.println("Dataset header file:\t\t\t\t" + getDs_hdrname());
        System.out.println("Dataset data file:\t\t\t\t" + getDs_datname());
        System.out.println("Size of header:\t\t\t\t\t" + getSizeof_hdr());
        System.out.println("File offset to data blob:\t\t\t" + getVox_offset());

        System.out.print("Endianness:\t\t\t\t\t");
        if (isBig_endian()) {
            System.out.println("big");
        } else {
            System.out.println("little");
        }

        System.out.println("Magic filetype string:\t\t\t\t" + getMagic());



        ///// Dataset datatype, size, units
        System.out.println("Datatype:\t\t\t\t\t" + getDatatype() + " (" +  Nifti1.decodeDatatype(getDatatype()) + ")");
        System.out.println("Bits per voxel:\t\t\t\t\t" + getBitpix());
        System.out.println("Scaling slope and intercept:\t\t\t" + getScl_slope() + " " + getScl_inter());


        System.out.print("Dataset dimensions (Count, X,Y,Z,T...):\t\t");
        for (i = 0; i <= getDim()[0]; i++) {
            System.out.print(getDim()[i] + " ");
        }
        System.out.println("");

        System.out.print("Grid spacings (X,Y,Z,T,...):\t\t\t");
        for (i = 1; i <= getDim()[0]; i++) {
            System.out.print(getPixdim()[i] + " ");
        }
        System.out.println("");

        System.out.println("XYZ  units:\t\t\t\t\t" + getXyz_unit_code() + " (" + Nifti1.decodeUnits(getXyz_unit_code()) + ")");
        System.out.println("T units:\t\t\t\t\t" + getT_unit_code() + " (" + Nifti1.decodeUnits(getT_unit_code()) + ")");
        System.out.println("T offset:\t\t\t\t\t" + getToffset());


        System.out.print("Intent parameters:\t\t\t\t");
        for (i = 0; i < 3; i++) {
            System.out.print(getIntent()[i] + " ");
        }
        System.out.println("");
        System.out.println("Intent code:\t\t\t\t\t" + getIntent_code() + " (" + Nifti1.decodeIntent(getIntent_code()) + ")");

        System.out.println("Cal. (display) max/min:\t\t\t\t" + getCal_max() + " " + getCal_min());


        ///// Slice order/timing stuff
        System.out.println("Slice timing code:\t\t\t\t" + getSlice_code() + " (" + Nifti1.decodeSliceOrder((short) getSlice_code()) + ")");
        System.out.println("MRI slice ordering (freq, phase, slice index):\t" + getFreq_dim() + " " + getPhase_dim() + " " + getSlice_dim());

        System.out.println("Start/end slice:\t\t\t\t" + getSlice_start() + " " + getSlice_end());
        System.out.println("Slice duration:\t\t\t\t\t" + getSlice_duration());


        ///// Orientation stuff
        System.out.println("Q factor:\t\t\t\t\t" + getQfac());
        System.out.println("Qform transform code:\t\t\t\t" + getQform_code() + " (" + Nifti1.decodeXform(getQform_code()) + ")");
        System.out.println("Quaternion b,c,d params:\t\t\t" + getQuatern()[0] + " " + getQuatern()[1] + " " + getQuatern()[2]);
        System.out.println("Quaternion x,y,z shifts:\t\t\t" + getQoffset()[0] + " " + getQoffset()[1] + " " + getQoffset()[2]);

        System.out.println("Affine transform code:\t\t\t\t" + getSform_code() + " (" + Nifti1.decodeXform(getSform_code()) + ")");
        System.out.print("1st row affine transform:\t\t\t");
        for (i = 0; i < 4; i++) {
            System.out.print(getSrow_x()[i] + " ");
        }
        System.out.println("");
        System.out.print("2nd row affine transform:\t\t\t");
        for (i = 0; i < 4; i++) {
            System.out.print(getSrow_y()[i] + " ");
        }
        System.out.println("");
        System.out.print("3rd row affine transform:\t\t\t");
        for (i = 0; i < 4; i++) {
            System.out.print(getSrow_z()[i] + " ");
        }
        System.out.println("");


        ///// comment stuff
        System.out.println("Description:\t\t\t\t\t" + getDescrip());
        System.out.println("Intent name:\t\t\t\t\t" + getIntent_name());
        System.out.println("Auxiliary file:\t\t\t\t\t" + getAux_file());
        System.out.println("Extension byte 1:\t\t\t\t\t" + (int) getExtension()[0]);


        ///// unused stuff
        System.out.println("\n\nUnused Fields");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Data type string:\t\t\t" + getData_type_string());
        System.out.println("db_name:\t\t\t\t\t" + getDb_name());
        System.out.println("extents:\t\t\t\t\t" + getExtents());
        System.out.println("session_error:\t\t\t\t\t" + getSession_error());
        System.out.println("regular:\t\t\t\t\t" + getRegular());
        System.out.println("glmax/glmin:\t\t\t\t\t" + getGlmax() + " " + getGlmin());
        System.out.println("Extension bytes 2-4:\t\t\t\t" + (int) getExtension()[1] + " " + (int) getExtension()[2] + " " + (int) getExtension()[3]);


        ////// extensions
        if (getExtension()[0] != 0) {
            extlist = getExtensionsList();
            n = extlist.length;
            System.out.println("\n\nExtensions");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("#\tCode\tSize");
            for (i = 0; i < n; i++) {
                System.out.println((i + 1) + "\t" + extlist[i][1] + "\t" + extlist[i][0]);
            }
            System.out.println("\n");
        }


        
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Print a voxel timecourse to standard out.
     * @param d 1D double array of timecourse values, length TDIM
     */
    public void printDoubleTmcrs(double d[]) {

        short i;
        NumberFormat nf;

        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(6);
        nf.setGroupingUsed(false);

        for (i = 0; i < getTDIM(); i++) {
            System.out.println(nf.format(d[i]));
        }

        
    }


    //////////////////////////////////////////////////////////////////
    /**
     * Set the filename for the dataset header file
     * @param s filename for the dataset header file
     */
    public void setHeaderFilename(String s) {

        if (s.endsWith(Nifti1.NI1_EXT)) {
            setToNii();
        } else {
            setToNi1();
        }

        setDs_hdrname(s);
        if (isDs_is_nii()) {
            if (!ds_hdrname.endsWith(Nifti1.NI1_EXT)) {
                setDs_hdrname(getDs_hdrname() + Nifti1.NI1_EXT);
            }
        } else {
            if (!ds_hdrname.endsWith(Nifti1.ANZ_HDR_EXT)) {
                setDs_hdrname(getDs_hdrname() + Nifti1.ANZ_HDR_EXT);
            }
        }
        
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Get the filename for the dataset header file
     * @return String with the disk filename for the dataset header file
     */
    public String getHeaderFilename() {
        return (getDs_hdrname());
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Set the filename for the dataset data file
     * @param s filename for the dataset data file
     */
    public void setDataFilename(String s) {
        setDs_datname(s);
        if (isDs_is_nii()) {
            if (!ds_datname.endsWith(Nifti1.NI1_EXT)) {
                setDs_datname(getDs_datname() + Nifti1.NI1_EXT);
            }
        } else {
            if (!ds_datname.endsWith(Nifti1.ANZ_DAT_EXT)) {
                setDs_datname(getDs_datname() + Nifti1.ANZ_DAT_EXT);
            }
        }

        
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Get the filename for the dataset data file
     * @return  filename for the dataset data file
     */
    public String getDataFilename() {
        return (getDs_datname());
    }

    //////////////////////////////////////////////////////////////////
	/*
     * Set fields to make this a nii (n+1) (single file) dataset
     * switching from nii to anz/hdr affects
     * -- magic field "n+1\0" not "ni1\0"
     * -- vox_offset must include 352+extensions
     * -- internal ds_is_nii flag
     *
     * NOTE: all changes are in memory, app still needs to
     * write header and data for change to occur on disk
     *
     * maybe add auto set of dat name to hdr name, strip img/hdr ??
     */
    protected void setToNii() {
        int i, n;
        int extlist[][];

        setDs_is_nii(true);
        setMagic(new StringBuffer(Nifti1.NII_MAGIC_STRING));

        setVox_offset(Nifti1.NII_HDR_SIZE);
        if (getExtension()[0] != 0) {
            extlist = getExtensionsList();
            n = extlist.length;
            for (i = 0; i < n; i++) {
                setVox_offset(getVox_offset() + extlist[i][0]);
            }
        }
        
    }

    //////////////////////////////////////////////////////////////////
	/*
     * Set fields to make this a ni1 (2 file img/hdr) dataset
     * switching from nii to anz/hdr affects
     * -- magic field "n+1\0" vs "ni1\0"
     * -- vox_offset does notinclude 352+extensions
     * -- internal ds_is_nii flag
     *
     * NOTE: all changes are in memory, app still needs to
     * write header and data for change to occur on disk
     *
     * maybe add auto set of dat name to hdr name, strip img/hdr ??
     */
    protected void setToNi1() {


        setDs_is_nii(false);
        setMagic(new StringBuffer(Nifti1.ANZ_MAGIC_STRING));

        // if there was stuff after header before data it cannot
        // survive transition from nii to ni1
        setVox_offset(0);

        
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Set the dataset dimensions
     */
    public void setDims(short a, short x, short y, short z, short t, short d5, short d6, short d7) {

        dim[0] = a;
        dim[1] = x;
        dim[2] = y;
        dim[3] = z;
        dim[4] = t;
        dim[5] = d5;
        dim[6] = d6;
        dim[7] = d7;

        setXDIM(x);
        setYDIM(y);
        setZDIM(z);
        setTDIM(t);

        
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Set the dataset datatype.  (bitpix will also be set accordingly.)
     * @param code nifti-1 datatype code
     */
    public void setDatatype(short code) {
        datatype = code;
        setBitpix((short) (Nifti1.bytesPerVoxel(code) * 8));
        
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Get the dataset datatype.
     * @return  datatype code (note: it is not guaranteed to be a valid
     * code, what is there is what you get...)
     */
    public short getDatatype() {
        return (datatype);
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Get the bitpix field
     * @return  bitpix: number of bits per pixel
     */
    public short getBitpix() {
        return (bitpix);
    }




    ////////////////////////////////////////////////////////////////////
    //
    // Set default settings for all nifti1 header fields and some
    // other fields for this class
    //
    ////////////////////////////////////////////////////////////////////
    private void setDefaults() {

        int i;
        ByteOrder bo;

        setDs_hdrname("");
        setDs_datname("");
        setDs_is_nii(false);		// use single file .nii format or not
        bo = ByteOrder.nativeOrder();
        if (bo == ByteOrder.BIG_ENDIAN) {
            setBig_endian(true);	// endian flag, need to flip little end.
        } else {
            setBig_endian(false);
        }
        setSizeof_hdr(Nifti1.ANZ_HDR_SIZE);		// must be 348 bytes
        setData_type_string(new StringBuffer()); // 10 char UNUSED
        for (i = 0; i < 10; i++) {
            getData_type_string().append("\0");
        }
        setDb_name(new StringBuffer());	// 18 char UNUSED
        for (i = 0; i < 18; i++) {
            getDb_name().append("\0");
        }
        setExtents(0);		// UNUSED
        setSession_error((short) 0);		// UNUSED
        setRegular(new StringBuffer("\0"));	// UNUSED
        setDim_info(new StringBuffer("\0"));	// MRI slice ordering
        setFreq_dim((short) 0);
        setPhase_dim((short) 0);
        setSlice_dim((short) 0);
        setDim(new short[8]);		// data array dimensions (8 shorts)
        for (i = 0; i < 8; i++) {
            getDim()[i] = 0;
        }
        setXDIM((short) 0);
        setYDIM((short) 0);
        setZDIM((short) 0);
        setTDIM((short) 0);
        setIntent(new float[3]);		// intents p1 p2 p3
        for (i = 0; i < 3; i++) {
            getIntent()[i] = (float) 0.0;
        }
        setIntent_code(Nifti1.NIFTI_INTENT_NONE);
        setDatatype(Nifti1.DT_NONE);		// datatype of image blob
        setBitpix((short) 0);			// #bits per voxel
        setSlice_start((short) 0);		// first slice index
        setPixdim(new float[8]);		// grid spacings
        getPixdim()[0] = 1;
        setQfac((short) 1);
        for (i = 1; i < 8; i++) {
            getPixdim()[i] = (float) 0.0;
        }

        setVox_offset((float) 0.0);	// offset to data blob in .nii file
        // for .nii files default is 352 but
        // at this point don't know filetype
        setScl_slope((float) 0.0);		// data scaling: slope
        setScl_inter((float) 0.0);		// data scaling: intercept
        setSlice_end((short) 0);			// last slice index
        setSlice_code((byte) 0);		// slice timing order
        setXyzt_units((byte) 0);		// units of pixdim[1-4]
        setXyz_unit_code(Nifti1.NIFTI_UNITS_UNKNOWN);
        setT_unit_code(Nifti1.NIFTI_UNITS_UNKNOWN);

        setCal_max((float) 0.0);		// max display intensity
        setCal_min((float) 0.0);		// min display intensity
        setSlice_duration((float) 0.0);	// time to acq. 1 slice
        setToffset((float) 0.0);		// time axis shift
        setGlmax(0);			// UNUSED
        setGlmin(0);			// UNUSED

        setDescrip(new StringBuffer());	// 80 char any text you'd like (comment)
        for (i = 0; i < 80; i++) {
            getDescrip().append("\0");
        }
        setAux_file(new StringBuffer());	// 24 char auxiliary filename
        for (i = 0; i < 24; i++) {
            getAux_file().append("\0");
        }


        setQform_code(Nifti1.NIFTI_XFORM_UNKNOWN);	// code for quat. xform
        setSform_code(Nifti1.NIFTI_XFORM_UNKNOWN);	// code for affine xform

        setQuatern(new float[3]);		// 3 floats Quaternion b,c,d params
        setQoffset(new float[3]);		// 3 floats Quaternion x,y,z shift
        for (i = 0; i < 3; i++) {
            getQuatern()[i] = (float) 0.0;
            getQoffset()[i] = (float) 0.0;
        }

        setSrow_x(new float[4]);		// 4 floats 1st row affine xform
        setSrow_y(new float[4]);		// 4 floats 2nd row affine xform
        setSrow_z(new float[4]);		// 4 floats 3rd row affine xform
        for (i = 0; i < 4; i++) {
            getSrow_x()[i] = (float) 0.0;
            getSrow_y()[i] = (float) 0.0;
            getSrow_z()[i] = (float) 0.0;
        }

        setIntent_name(new StringBuffer()); // 16 char name/meaning/id for data
        for (i = 0; i < 16; i++) {
            getIntent_name().append("\0");
        }

        // defaulting to n+1 nii as per vox_offset 0
        setMagic(new StringBuffer(Nifti1.NII_MAGIC_STRING)); // 4 char id must be "ni1\0" or "n+1\0"

        setExtension(new byte[4]);	// 4 byte array, [0] is for extensions
        for (i = 0; i < 4; i++) {
            getExtension()[i] = (byte) 0;
        }

        setExtensions_list(new Vector(5));	// list of int[2] size/code pairs for exts.
        setExtension_blobs(new Vector(5));	// vector to hold data in each ext.

        
    }



    //////////////////////////////////////////////////////////////////
	/*
     * truncate or pad a string to make it the needed length
     * @param s input string
     * @param n desired length
     * @return s String of length n with as much of s as possible, padded
     * with 0 if necessary
     */
    protected byte[] setStringSize(StringBuffer s, int n) {

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
     * @return the ds_hdrname
     */
    public String getDs_hdrname() {
        return ds_hdrname;
    }

    /**
     * @param ds_hdrname the ds_hdrname to set
     */
    public void setDs_hdrname(String ds_hdrname) {
        this.ds_hdrname = ds_hdrname;
    }

    /**
     * @return the ds_datname
     */
    public String getDs_datname() {
        return ds_datname;
    }

    /**
     * @param ds_datname the ds_datname to set
     */
    public void setDs_datname(String ds_datname) {
        this.ds_datname = ds_datname;
    }

    /**
     * @return the ds_is_nii
     */
    public boolean isDs_is_nii() {
        return ds_is_nii;
    }

    /**
     * @param ds_is_nii the ds_is_nii to set
     */
    public void setDs_is_nii(boolean ds_is_nii) {
        this.ds_is_nii = ds_is_nii;
    }

    /**
     * @return the big_endian
     */
    public boolean isBig_endian() {
        return big_endian;
    }

    /**
     * @param big_endian the big_endian to set
     */
    public void setBig_endian(boolean big_endian) {
        this.big_endian = big_endian;
    }

    /**
     * @return the XDIM
     */
    public short getXDIM() {
        return XDIM;
    }

    /**
     * @param XDIM the XDIM to set
     */
    public void setXDIM(short XDIM) {
        this.XDIM = XDIM;
    }

    /**
     * @return the YDIM
     */
    public short getYDIM() {
        return YDIM;
    }

    /**
     * @param YDIM the YDIM to set
     */
    public void setYDIM(short YDIM) {
        this.YDIM = YDIM;
    }

    /**
     * @return the ZDIM
     */
    public short getZDIM() {
        return ZDIM;
    }

    /**
     * @param ZDIM the ZDIM to set
     */
    public void setZDIM(short ZDIM) {
        this.ZDIM = ZDIM;
    }

    /**
     * @return the TDIM
     */
    public short getTDIM() {
        return TDIM;
    }

    /**
     * @param TDIM the TDIM to set
     */
    public void setTDIM(short TDIM) {
        this.TDIM = TDIM;
    }

    /**
     * @return the DIM5
     */
    public short getDIM5() {
        return DIM5;
    }

    /**
     * @param DIM5 the DIM5 to set
     */
    public void setDIM5(short DIM5) {
        this.DIM5 = DIM5;
    }

    /**
     * @return the DIM6
     */
    public short getDIM6() {
        return DIM6;
    }

    /**
     * @param DIM6 the DIM6 to set
     */
    public void setDIM6(short DIM6) {
        this.DIM6 = DIM6;
    }

    /**
     * @return the DIM7
     */
    public short getDIM7() {
        return DIM7;
    }

    /**
     * @param DIM7 the DIM7 to set
     */
    public void setDIM7(short DIM7) {
        this.DIM7 = DIM7;
    }

    /**
     * @return the freq_dim
     */
    public short getFreq_dim() {
        return freq_dim;
    }

    /**
     * @param freq_dim the freq_dim to set
     */
    public void setFreq_dim(short freq_dim) {
        this.freq_dim = freq_dim;
    }

    /**
     * @return the phase_dim
     */
    public short getPhase_dim() {
        return phase_dim;
    }

    /**
     * @param phase_dim the phase_dim to set
     */
    public void setPhase_dim(short phase_dim) {
        this.phase_dim = phase_dim;
    }

    /**
     * @return the slice_dim
     */
    public short getSlice_dim() {
        return slice_dim;
    }

    /**
     * @param slice_dim the slice_dim to set
     */
    public void setSlice_dim(short slice_dim) {
        this.slice_dim = slice_dim;
    }

    /**
     * @return the xyz_unit_code
     */
    public short getXyz_unit_code() {
        return xyz_unit_code;
    }

    /**
     * @param xyz_unit_code the xyz_unit_code to set
     */
    public void setXyz_unit_code(short xyz_unit_code) {
        this.xyz_unit_code = xyz_unit_code;
    }

    /**
     * @return the t_unit_code
     */
    public short getT_unit_code() {
        return t_unit_code;
    }

    /**
     * @param t_unit_code the t_unit_code to set
     */
    public void setT_unit_code(short t_unit_code) {
        this.t_unit_code = t_unit_code;
    }

    /**
     * @return the qfac
     */
    public short getQfac() {
        return qfac;
    }

    /**
     * @param qfac the qfac to set
     */
    public void setQfac(short qfac) {
        this.qfac = qfac;
    }

    /**
     * @return the extensions_list
     */
    public Vector getExtensions_list() {
        return extensions_list;
    }

    /**
     * @param extensions_list the extensions_list to set
     */
    public void setExtensions_list(Vector extensions_list) {
        this.extensions_list = extensions_list;
    }

    /**
     * @return the extension_blobs
     */
    public Vector getExtension_blobs() {
        return extension_blobs;
    }

    /**
     * @param extension_blobs the extension_blobs to set
     */
    public void setExtension_blobs(Vector extension_blobs) {
        this.extension_blobs = extension_blobs;
    }

    /**
     * @return the sizeof_hdr
     */
    public int getSizeof_hdr() {
        return sizeof_hdr;
    }

    /**
     * @param sizeof_hdr the sizeof_hdr to set
     */
    public void setSizeof_hdr(int sizeof_hdr) {
        this.sizeof_hdr = sizeof_hdr;
    }

    /**
     * @return the data_type_string
     */
    public StringBuffer getData_type_string() {
        return data_type_string;
    }

    /**
     * @param data_type_string the data_type_string to set
     */
    public void setData_type_string(StringBuffer data_type_string) {
        this.data_type_string = data_type_string;
    }

    /**
     * @return the db_name
     */
    public StringBuffer getDb_name() {
        return db_name;
    }

    /**
     * @param db_name the db_name to set
     */
    public void setDb_name(StringBuffer db_name) {
        this.db_name = db_name;
    }

    /**
     * @return the extents
     */
    public int getExtents() {
        return extents;
    }

    /**
     * @param extents the extents to set
     */
    public void setExtents(int extents) {
        this.extents = extents;
    }

    /**
     * @return the session_error
     */
    public short getSession_error() {
        return session_error;
    }

    /**
     * @param session_error the session_error to set
     */
    public void setSession_error(short session_error) {
        this.session_error = session_error;
    }

    /**
     * @return the regular
     */
    public StringBuffer getRegular() {
        return regular;
    }

    /**
     * @param regular the regular to set
     */
    public void setRegular(StringBuffer regular) {
        this.regular = regular;
    }
    

    /**
     * @return the dim_info
     */
    public StringBuffer getDim_info() {
        return dim_info;
    }

    /**
     * @param dim_info the dim_info to set
     */
    public void setDim_info(StringBuffer dim_info) {
        this.dim_info = dim_info;
    }

    /**
     * @return the dim
     */
    public short[] getDim() {
        return dim;
    }

    /**
     * @param dim the dim to set
     */
    public void setDim(short[] dim) {
        this.dim = dim;
    }

    /**
     * @return the intent
     */
    public float[] getIntent() {
        return intent;
    }

    /**
     * @param intent the intent to set
     */
    public void setIntent(float[] intent) {
        this.intent = intent;
    }

    /**
     * @return the intent_code
     */
    public short getIntent_code() {
        return intent_code;
    }

    /**
     * @param intent_code the intent_code to set
     */
    public void setIntent_code(short intent_code) {
        this.intent_code = intent_code;
    }

    /**
     * @param bitpix the bitpix to set
     */
    public void setBitpix(short bitpix) {
        this.bitpix = bitpix;
    }

    /**
     * @return the slice_start
     */
    public short getSlice_start() {
        return slice_start;
    }

    /**
     * @param slice_start the slice_start to set
     */
    public void setSlice_start(short slice_start) {
        this.slice_start = slice_start;
    }

    /**
     * @return the pixdim
     */
    public float[] getPixdim() {
        return pixdim;
    }

    /**
     * @param pixdim the pixdim to set
     */
    public void setPixdim(float[] pixdim) {
        this.pixdim = pixdim;
    }

    /**
     * @return the vox_offset
     */
    public float getVox_offset() {
        return vox_offset;
    }

    /**
     * @param vox_offset the vox_offset to set
     */
    public void setVox_offset(float vox_offset) {
        this.vox_offset = vox_offset;
    }

    /**
     * @return the scl_slope
     */
    public float getScl_slope() {
        return scl_slope;
    }

    /**
     * @param scl_slope the scl_slope to set
     */
    public void setScl_slope(float scl_slope) {
        this.scl_slope = scl_slope;
    }

    /**
     * @return the scl_inter
     */
    public float getScl_inter() {
        return scl_inter;
    }

    /**
     * @param scl_inter the scl_inter to set
     */
    public void setScl_inter(float scl_inter) {
        this.scl_inter = scl_inter;
    }

    /**
     * @return the slice_end
     */
    public short getSlice_end() {
        return slice_end;
    }

    /**
     * @param slice_end the slice_end to set
     */
    public void setSlice_end(short slice_end) {
        this.slice_end = slice_end;
    }

    /**
     * @return the slice_code
     */
    public byte getSlice_code() {
        return slice_code;
    }

    /**
     * @param slice_code the slice_code to set
     */
    public void setSlice_code(byte slice_code) {
        this.slice_code = slice_code;
    }

    /**
     * @return the xyzt_units
     */
    public byte getXyzt_units() {
        return xyzt_units;
    }

    /**
     * @param xyzt_units the xyzt_units to set
     */
    public void setXyzt_units(byte xyzt_units) {
        this.xyzt_units = xyzt_units;
    }

    /**
     * @return the cal_max
     */
    public float getCal_max() {
        return cal_max;
    }

    /**
     * @param cal_max the cal_max to set
     */
    public void setCal_max(float cal_max) {
        this.cal_max = cal_max;
    }

    /**
     * @return the cal_min
     */
    public float getCal_min() {
        return cal_min;
    }

    /**
     * @param cal_min the cal_min to set
     */
    public void setCal_min(float cal_min) {
        this.cal_min = cal_min;
    }

    /**
     * @return the slice_duration
     */
    public float getSlice_duration() {
        return slice_duration;
    }

    /**
     * @param slice_duration the slice_duration to set
     */
    public void setSlice_duration(float slice_duration) {
        this.slice_duration = slice_duration;
    }

    /**
     * @return the toffset
     */
    public float getToffset() {
        return toffset;
    }

    /**
     * @param toffset the toffset to set
     */
    public void setToffset(float toffset) {
        this.toffset = toffset;
    }

    /**
     * @return the glmax
     */
    public int getGlmax() {
        return glmax;
    }

    /**
     * @param glmax the glmax to set
     */
    public void setGlmax(int glmax) {
        this.glmax = glmax;
    }

    /**
     * @return the glmin
     */
    public int getGlmin() {
        return glmin;
    }

    /**
     * @param glmin the glmin to set
     */
    public void setGlmin(int glmin) {
        this.glmin = glmin;
    }

    /**
     * @return the descrip
     */
    public StringBuffer getDescrip() {
        return descrip;
    }

    /**
     * @param descrip the descrip to set
     */
    public void setDescrip(StringBuffer descrip) {
        this.descrip = descrip;
    }

    /**
     * @return the aux_file
     */
    public StringBuffer getAux_file() {
        return aux_file;
    }

    /**
     * @param aux_file the aux_file to set
     */
    public void setAux_file(StringBuffer aux_file) {
        this.aux_file = aux_file;
    }

    /**
     * @return the qform_code
     */
    public short getQform_code() {
        return qform_code;
    }

    /**
     * @param qform_code the qform_code to set
     */
    public void setQform_code(short qform_code) {
        this.qform_code = qform_code;
    }

    /**
     * @return the sform_code
     */
    public short getSform_code() {
        return sform_code;
    }

    /**
     * @param sform_code the sform_code to set
     */
    public void setSform_code(short sform_code) {
        this.sform_code = sform_code;
    }

    /**
     * @return the quatern
     */
    public float[] getQuatern() {
        return quatern;
    }

    /**
     * @param quatern the quatern to set
     */
    public void setQuatern(float[] quatern) {
        this.quatern = quatern;
    }

    /**
     * @return the qoffset
     */
    public float[] getQoffset() {
        return qoffset;
    }

    /**
     * @param qoffset the qoffset to set
     */
    public void setQoffset(float[] qoffset) {
        this.qoffset = qoffset;
    }

    /**
     * @return the srow_x
     */
    public float[] getSrow_x() {
        return srow_x;
    }

    /**
     * @param srow_x the srow_x to set
     */
    public void setSrow_x(float[] srow_x) {
        this.srow_x = srow_x;
    }

    /**
     * @return the srow_y
     */
    public float[] getSrow_y() {
        return srow_y;
    }

    /**
     * @param srow_y the srow_y to set
     */
    public void setSrow_y(float[] srow_y) {
        this.srow_y = srow_y;
    }

    /**
     * @return the srow_z
     */
    public float[] getSrow_z() {
        return srow_z;
    }

    /**
     * @param srow_z the srow_z to set
     */
    public void setSrow_z(float[] srow_z) {
        this.srow_z = srow_z;
    }

    /**
     * @return the intent_name
     */
    public StringBuffer getIntent_name() {
        return intent_name;
    }

    /**
     * @param intent_name the intent_name to set
     */
    public void setIntent_name(StringBuffer intent_name) {
        this.intent_name = intent_name;
    }

    /**
     * @return the magic
     */
    public StringBuffer getMagic() {
        return magic;
    }

    /**
     * @param magic the magic to set
     */
    public void setMagic(StringBuffer magic) {
        this.magic = magic;
    }

    /**
     * @return the extension
     */
    public byte[] getExtension() {
        return extension;
    }

    /**
     * @param extension the extension to set
     */
    public void setExtension(byte[] extension) {
        this.extension = extension;
    }


}
