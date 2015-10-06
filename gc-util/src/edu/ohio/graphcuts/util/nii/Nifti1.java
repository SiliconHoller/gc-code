/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util.nii;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class Nifti1 {

    //////////////////////////////////////////////////////////////////
    //
    //		Nifti-1 Defines
    //
    //////////////////////////////////////////////////////////////////
    public static final String ANZ_HDR_EXT = ".hdr";
    public static final String ANZ_DAT_EXT = ".img";
    public static final String NI1_EXT = ".nii";
    public static final String GZIP_EXT = ".gz";
    public static final int ANZ_HDR_SIZE = 348;
    public static final long NII_HDR_SIZE = 352;
    public static final int EXT_KEY_SIZE = 8;   // esize+ecode
    public static final String NII_MAGIC_STRING = "n+1";
    public static final String ANZ_MAGIC_STRING = "ni1";
    //////////////////////////////////////////////////////////////////
    //
    //		Nifti-1 Codes from C nifti1.h
    //
    //////////////////////////////////////////////////////////////////
    // intent codes for field intent_code
    public static final short NIFTI_INTENT_NONE = 0;
    public static final short NIFTI_INTENT_CORREL = 2;
    public static final short NIFTI_INTENT_TTEST = 3;
    public static final short NIFTI_INTENT_FTEST = 4;
    public static final short NIFTI_INTENT_ZSCORE = 5;
    public static final short NIFTI_INTENT_CHISQ = 6;
    public static final short NIFTI_INTENT_BETA = 7;
    public static final short NIFTI_INTENT_BINOM = 8;
    public static final short NIFTI_INTENT_GAMMA = 9;
    public static final short NIFTI_INTENT_POISSON = 10;
    public static final short NIFTI_INTENT_NORMAL = 11;
    public static final short NIFTI_INTENT_FTEST_NONC = 12;
    public static final short NIFTI_INTENT_CHISQ_NONC = 13;
    public static final short NIFTI_INTENT_LOGISTIC = 14;
    public static final short NIFTI_INTENT_LAPLACE = 15;
    public static final short NIFTI_INTENT_UNIFORM = 16;
    public static final short NIFTI_INTENT_TTEST_NONC = 17;
    public static final short NIFTI_INTENT_WEIBULL = 18;
    public static final short NIFTI_INTENT_CHI = 19;
    public static final short NIFTI_INTENT_INVGAUSS = 20;
    public static final short NIFTI_INTENT_EXTVAL = 21;
    public static final short NIFTI_INTENT_PVAL = 22;
    public static final short NIFTI_INTENT_ESTIMATE = 1001;
    public static final short NIFTI_INTENT_LABEL = 1002;
    public static final short NIFTI_INTENT_NEURONAME = 1003;
    public static final short NIFTI_INTENT_GENMATRIX = 1004;
    public static final short NIFTI_INTENT_SYMMATRIX = 1005;
    public static final short NIFTI_INTENT_DISPVECT = 1006;
    public static final short NIFTI_INTENT_VECTOR = 1007;
    public static final short NIFTI_INTENT_POINTSET = 1008;
    public static final short NIFTI_INTENT_TRIANGLE = 1009;
    public static final short NIFTI_INTENT_QUATERNION = 1010;
    public static final short NIFTI_FIRST_STATCODE = 2;
    public static final short NIFTI_LAST_STATCODE = 22;
    // datatype codes for field datatype
    public static final short DT_NONE = 0;
    public static final short DT_BINARY = 1;
    public static final short NIFTI_TYPE_UINT8 = 2;
    public static final short NIFTI_TYPE_INT16 = 4;
    public static final short NIFTI_TYPE_INT32 = 8;
    public static final short NIFTI_TYPE_FLOAT32 = 16;
    public static final short NIFTI_TYPE_COMPLEX64 = 32;
    public static final short NIFTI_TYPE_FLOAT64 = 64;
    public static final short NIFTI_TYPE_RGB24 = 128;
    public static final short DT_ALL = 255;
    public static final short NIFTI_TYPE_INT8 = 256;
    public static final short NIFTI_TYPE_UINT16 = 512;
    public static final short NIFTI_TYPE_UINT32 = 768;
    public static final short NIFTI_TYPE_INT64 = 1024;
    public static final short NIFTI_TYPE_UINT64 = 1280;
    public static final short NIFTI_TYPE_FLOAT128 = 1536;
    public static final short NIFTI_TYPE_COMPLEX128 = 1792;
    public static final short NIFTI_TYPE_COMPLEX256 = 2048;
    // units codes for xyzt_units
    public static final short NIFTI_UNITS_UNKNOWN = 0;
    public static final short NIFTI_UNITS_METER = 1;
    public static final short NIFTI_UNITS_MM = 2;
    public static final short NIFTI_UNITS_MICRON = 3;
    public static final short NIFTI_UNITS_SEC = 8;
    public static final short NIFTI_UNITS_MSEC = 16;
    public static final short NIFTI_UNITS_USEC = 24;
    public static final short NIFTI_UNITS_HZ = 32;
    public static final short NIFTI_UNITS_PPM = 40;
    // slice order codes for slice_code
    public static final short NIFTI_SLICE_SEQ_INC = 1;
    public static final short NIFTI_SLICE_SEQ_DEC = 2;
    public static final short NIFTI_SLICE_ALT_INC = 3;
    public static final short NIFTI_SLICE_ALT_DEC = 4;
    // codes for qform_code sform_code
    public static final short NIFTI_XFORM_UNKNOWN = 0;
    public static final short NIFTI_XFORM_SCANNER_ANAT = 1;
    public static final short NIFTI_XFORM_ALIGNED_ANAT = 2;
    public static final short NIFTI_XFORM_TALAIRACH = 3;
    public static final short NIFTI_XFORM_MNI_152 = 4;


    //////////////////////////////////////////////////////////////////
    /**
     * Decode the nifti intent codes
     * @param icode nifti intent code
     * @return a terse string describing the intent
     */
    public static String decodeIntent(short icode) {

        switch (icode) {

            case NIFTI_INTENT_NONE:
                return ("NIFTI_INTENT_NONE");
            case NIFTI_INTENT_CORREL:
                return ("NIFTI_INTENT_CORREL");
            case NIFTI_INTENT_TTEST:
                return ("NIFTI_INTENT_TTEST");
            case NIFTI_INTENT_FTEST:
                return ("NIFTI_INTENT_FTEST");
            case NIFTI_INTENT_ZSCORE:
                return ("NIFTI_INTENT_ZSCORE");
            case NIFTI_INTENT_CHISQ:
                return ("NIFTI_INTENT_CHISQ");
            case NIFTI_INTENT_BETA:
                return ("NIFTI_INTENT_BETA");
            case NIFTI_INTENT_BINOM:
                return ("NIFTI_INTENT_BINOM");
            case NIFTI_INTENT_GAMMA:
                return ("NIFTI_INTENT_GAMMA");
            case NIFTI_INTENT_POISSON:
                return ("NIFTI_INTENT_POISSON");
            case NIFTI_INTENT_NORMAL:
                return ("NIFTI_INTENT_NORMAL");
            case NIFTI_INTENT_FTEST_NONC:
                return ("NIFTI_INTENT_FTEST_NONC");
            case NIFTI_INTENT_CHISQ_NONC:
                return ("NIFTI_INTENT_CHISQ_NONC");
            case NIFTI_INTENT_LOGISTIC:
                return ("NIFTI_INTENT_LOGISTIC");
            case NIFTI_INTENT_LAPLACE:
                return ("NIFTI_INTENT_LAPLACE");
            case NIFTI_INTENT_UNIFORM:
                return ("NIFTI_INTENT_UNIFORM");
            case NIFTI_INTENT_TTEST_NONC:
                return ("NIFTI_INTENT_TTEST_NONC");
            case NIFTI_INTENT_WEIBULL:
                return ("NIFTI_INTENT_WEIBULL");
            case NIFTI_INTENT_CHI:
                return ("NIFTI_INTENT_CHI");
            case NIFTI_INTENT_INVGAUSS:
                return ("NIFTI_INTENT_INVGAUSS");
            case NIFTI_INTENT_EXTVAL:
                return ("NIFTI_INTENT_EXTVAL");
            case NIFTI_INTENT_PVAL:
                return ("NIFTI_INTENT_PVAL");
            case NIFTI_INTENT_ESTIMATE:
                return ("NIFTI_INTENT_ESTIMATE");
            case NIFTI_INTENT_LABEL:
                return ("NIFTI_INTENT_LABEL");
            case NIFTI_INTENT_NEURONAME:
                return ("NIFTI_INTENT_NEURONAME");
            case NIFTI_INTENT_GENMATRIX:
                return ("NIFTI_INTENT_GENMATRIX");
            case NIFTI_INTENT_SYMMATRIX:
                return ("NIFTI_INTENT_SYMMATRIX");
            case NIFTI_INTENT_DISPVECT:
                return ("NIFTI_INTENT_DISPVECT");
            case NIFTI_INTENT_VECTOR:
                return ("NIFTI_INTENT_VECTOR");
            case NIFTI_INTENT_POINTSET:
                return ("NIFTI_INTENT_POINTSET");
            case NIFTI_INTENT_TRIANGLE:
                return ("NIFTI_INTENT_TRIANGLE");
            case NIFTI_INTENT_QUATERNION:
                return ("NIFTI_INTENT_QUATERNION");
            default:
                return ("INVALID_NIFTI_INTENT_CODE");
        }
    }


    //////////////////////////////////////////////////////////////////
    /**
     * Decode the nifti datatype codes
     * @param dcode nifti datatype code
     * @return a terse string describing the datatype
     */
    public static String decodeDatatype(short dcode) {

        switch (dcode) {

            case DT_NONE:
                return ("DT_NONE");
            case DT_BINARY:
                return ("DT_BINARY");
            case NIFTI_TYPE_UINT8:
                return ("NIFTI_TYPE_UINT8");
            case NIFTI_TYPE_INT16:
                return ("NIFTI_TYPE_INT16");
            case NIFTI_TYPE_INT32:
                return ("NIFTI_TYPE_INT32");
            case NIFTI_TYPE_FLOAT32:
                return ("NIFTI_TYPE_FLOAT32");
            case NIFTI_TYPE_COMPLEX64:
                return ("NIFTI_TYPE_COMPLEX64");
            case NIFTI_TYPE_FLOAT64:
                return ("NIFTI_TYPE_FLOAT64");
            case NIFTI_TYPE_RGB24:
                return ("NIFTI_TYPE_RGB24");
            case DT_ALL:
                return ("DT_ALL");
            case NIFTI_TYPE_INT8:
                return ("NIFTI_TYPE_INT8");
            case NIFTI_TYPE_UINT16:
                return ("NIFTI_TYPE_UINT16");
            case NIFTI_TYPE_UINT32:
                return ("NIFTI_TYPE_UINT32");
            case NIFTI_TYPE_INT64:
                return ("NIFTI_TYPE_INT64");
            case NIFTI_TYPE_UINT64:
                return ("NIFTI_TYPE_UINT64");
            case NIFTI_TYPE_FLOAT128:
                return ("NIFTI_TYPE_FLOAT128");
            case NIFTI_TYPE_COMPLEX128:
                return ("NIFTI_TYPE_COMPLEX128");
            case NIFTI_TYPE_COMPLEX256:
                return ("NIFTI_TYPE_COMPLEX256");
            default:
                return ("INVALID_NIFTI_DATATYPE_CODE");
        }
    }

    //////////////////////////////////////////////////////////////////
    /**
     * Return bytes per voxel for each nifti-1 datatype
     * @param dcode nifti datatype code
     * @return a short with number of bytes per voxel, 0 for unknown,
     *  -1 for 1 bit
     */
    public static short bytesPerVoxel(short dcode) {

        switch (dcode) {

            case DT_NONE:
                return (0);
            case DT_BINARY:
                return (-1);
            case NIFTI_TYPE_UINT8:
                return (1);
            case NIFTI_TYPE_INT16:
                return (2);
            case NIFTI_TYPE_INT32:
                return (4);
            case NIFTI_TYPE_FLOAT32:
                return (4);
            case NIFTI_TYPE_COMPLEX64:
                return (8);
            case NIFTI_TYPE_FLOAT64:
                return (8);
            case NIFTI_TYPE_RGB24:
                return (3);
            case DT_ALL:
                return (0);
            case NIFTI_TYPE_INT8:
                return (1);
            case NIFTI_TYPE_UINT16:
                return (2);
            case NIFTI_TYPE_UINT32:
                return (4);
            case NIFTI_TYPE_INT64:
                return (8);
            case NIFTI_TYPE_UINT64:
                return (8);
            case NIFTI_TYPE_FLOAT128:
                return (16);
            case NIFTI_TYPE_COMPLEX128:
                return (16);
            case NIFTI_TYPE_COMPLEX256:
                return (32);
            default:
                return (0);
        }
    }


    //////////////////////////////////////////////////////////////////
    /**
     * Decode the nifti slice order codes
     * @param code nifti slice order code
     * @return a terse string describing the slice order
     */
    public static String decodeSliceOrder(short code) {

        switch (code) {

            case NIFTI_SLICE_SEQ_INC:
                return ("NIFTI_SLICE_SEQ_INC");
            case NIFTI_SLICE_SEQ_DEC:
                return ("NIFTI_SLICE_SEQ_DEC");
            case NIFTI_SLICE_ALT_INC:
                return ("NIFTI_SLICE_ALT_INC");
            case NIFTI_SLICE_ALT_DEC:
                return ("NIFTI_SLICE_ALT_DEC");
            default:
                return ("INVALID_NIFTI_SLICE_SEQ_CODE");
        }
    }


    //////////////////////////////////////////////////////////////////
    /**
     * Decode the nifti xform codes
     * @param code nifti xform code
     * @return a terse string describing the coord. system
     */
    public static String decodeXform(short code) {

        switch (code) {
            case NIFTI_XFORM_UNKNOWN:
                return ("NIFTI_XFORM_UNKNOWN");
            case NIFTI_XFORM_SCANNER_ANAT:
                return ("NIFTI_XFORM_SCANNER_ANAT");
            case NIFTI_XFORM_ALIGNED_ANAT:
                return ("NIFTI_XFORM_ALIGNED_ANAT");
            case NIFTI_XFORM_TALAIRACH:
                return ("NIFTI_XFORM_TALAIRACH");
            case NIFTI_XFORM_MNI_152:
                return ("NIFTI_XFORM_MNI_152");
            default:
                return ("INVALID_NIFTI_XFORM_CODE");
        }
    }


    //////////////////////////////////////////////////////////////////
    /**
     * Decode the nifti unit codes
     * @param code nifti units code
     * @return a terse string describing the unit
     */
    public static String decodeUnits(short code) {

        switch (code) {
            case NIFTI_UNITS_UNKNOWN:
                return ("NIFTI_UNITS_UNKNOWN");
            case NIFTI_UNITS_METER:
                return ("NIFTI_UNITS_METER");
            case NIFTI_UNITS_MM:
                return ("NIFTI_UNITS_MM");
            case NIFTI_UNITS_MICRON:
                return ("NIFTI_UNITS_MICRON");
            case NIFTI_UNITS_SEC:
                return ("NIFTI_UNITS_SEC");
            case NIFTI_UNITS_MSEC:
                return ("NIFTI_UNITS_MSEC");
            case NIFTI_UNITS_USEC:
                return ("NIFTI_UNITS_USEC");
            case NIFTI_UNITS_HZ:
                return ("NIFTI_UNITS_HZ");
            case NIFTI_UNITS_PPM:
                return ("NIFTI_UNITS_PPM");
            default:
                return ("INVALID_NIFTI_UNITS_CODE");
        }
    }

}
