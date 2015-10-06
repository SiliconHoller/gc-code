/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util.nii;

import java.io.File;
import java.io.IOException;

/**
 * <p>Refactored Nifti1 dataset classes.</p>
 * <p>This abstract class in the front for all IO operations on image/header file
 * data handling.  For systems that wish to use an in-memory set of data (for speed), calling
 * the getMemoryInstance() factory method will return a subclass that loads and manipulates
 * all data in the application memory.</p>
 * <p>For usage which minimizes memory loading by sacrificing some speed, the getFileInstance()
 * returns a subclass that performs all data management and access directly from the disk.</p>
 * @author David Days <david.c.days@gmail.com>
 */
public abstract class NiftiData {

    protected NiftiHeader header;
    protected NiftiReader reader;
    protected NiftiWriter writer;

    protected NiftiData() {
    }

    public static NiftiData getFileInstance() {
        return new NiftiFileBackedData();
    }

    public static NiftiData getMemoryInstance() {
        return new NiftiMemoryData();
    }

    public NiftiHeader getHeader() {
        return header;
    }

    public void load(String filename) throws IOException {
        header = new NiftiHeader();
        header.setDs_datname(filename);
        header.setDs_hdrname(filename);
        reader = new NiftiReader(header);
        writer = new NiftiWriter(header);
        reader.readHeader();
        loadData();
    }

    public void load(String headerfile, String datafile) throws IOException {
        header = new NiftiHeader();
        header.setDs_hdrname(headerfile);
        header.setDs_datname(datafile);
        reader.readHeader();
        loadData();
    }

    /**
     * <p>Creates a new data file and NiftiData instance based on the
     * settings and the class of the current one.</p>
     * <p>The new instance of NiftiData has the following properties and actions completed:</p>
     * <ul>
     * <li>Instance type (NiftiMemoryData or NiftiFieBackedData) is the same.</li>
     * <li>Settings from the current instance of NiftHeader are copied, after which...</li>
     * <li>NiftiHeader header name and data name are set to filename, with appropriate
     * internal structural settings changed.</li>
     * <li>New, internal instances of NiftiReader and NiftiWriter are created.</li>
     * <li>The new, restructured NiftiHeader is written to the disk.</li>
     * <li>The new copy is then returned to the user.</li>
     * </ul>
     * @param filename  Single filename for all data for the new instance.
     * @return An instance of NiftiData based on the new file.
     * @throws IOException If there is a problem with the new file.
     */
    public NiftiData newCopy(String filename) throws IOException {
        NiftiHeader nheader = new NiftiHeader();
        nheader.copyHeader(header);
        nheader.setHeaderFilename(filename);
        nheader.setDataFilename(filename);
        NiftiData copy = null;
        if (this instanceof NiftiMemoryData) {
            copy = new NiftiMemoryData();
        } else if (this instanceof NiftiFileBackedData) {
            copy = new NiftiFileBackedData();
        }
        copy.header = nheader;
        copy.reader = new NiftiReader(nheader);
        copy.writer = new NiftiWriter(nheader);
        copy.writer.writeHeader();
        return copy;
    }

        /**
     * <p>Creates a new data file and NiftiData instance based on the
     * settings and the class of the current one.</p>
     * <p>The new instance of NiftiData has the following properties and actions completed:</p>
     * <ul>
     * <li>Instance type (NiftiMemoryData or NiftiFieBackedData) is the same.</li>
     * <li>Settings from the current instance of NiftHeader are copied, after which...</li>
     * <li>NiftiHeader header name and data name are set to filename, with appropriate
     * internal structural settings changed.</li>
     * <li>New, internal instances of NiftiReader and NiftiWriter are created.</li>
     * <li>The new, restructured NiftiHeader is written to the disk.</li>
     * <li>The new copy is then returned to the user.</li>
     * </ul>
     * @param hdrname  Filename containing the header data.
     * @param datname  Filename containing the data.
     * @return An instance of NiftiData based on the new files.
     * @throws IOException If there is a problem with the new files.
     */
    public NiftiData newCopy(String hdrname, String datname) throws IOException {
        NiftiHeader nheader = new NiftiHeader();
        nheader.copyHeader(header);
        nheader.setHeaderFilename(hdrname);
        nheader.setDataFilename(datname);
        NiftiData copy = null;
        if (this instanceof NiftiMemoryData) {
            copy = new NiftiMemoryData();
        } else if (this instanceof NiftiFileBackedData) {
            copy = new NiftiFileBackedData();
        }
        copy.header = nheader;
        copy.reader = new NiftiReader(nheader);
        copy.writer = new NiftiWriter(nheader);
        copy.writer.writeHeader();
        return copy;
    }

    /**
     * Load the data in the relevant manner.
     * @throws IOException If there is a problem reading the data from the disk.
     */
    protected abstract void loadData() throws IOException;

    /**
     * Add an extension stored in a file to a header
     * @param code -- code identifying the extension
     * @param filename -- filename containing the extension.  The entire
     * file will be added as an extension
     */
    public abstract void addExtension(int code, String filename) throws IOException;

    /**
     * Read one 3D volume from disk and return it as 3D double array
     * @param ttt T dimension of vol to read (0 based index)
     * @return 3D double array, scale and offset have been applied.
     * Array indices are [Z][Y][X], assuming an xyzt ordering of dimensions.
     * ie  indices are data[dim[3]][dim[2]][dim[1]]
     * @exception IOException
     *
     */
    public abstract double[][][] readDoubleVol(short ttt) throws IOException;

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
    public abstract void writeVol(double data[][][], short ttt) throws IOException;


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
        writer.writeData(b);
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
        return reader.readData();
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
        return reader.readDoubleTmcrs(x, y, z);
    }


    /**
     * Pass-through for the removeExtension() method.
     * @param index
     */
    public void removeExtension(int index) {
        header.removeExtension(index);
    }



    /**
     * Check if a valid dataset (header+data) exists.  Note that
     * some operations (e.g. header editing) do not actually require
     * that a data file exist.  Use existsHdr() existsDat() in those cases.
     * Gzipped files with .gz extension are permitted.
     * @return true if header and data file exist, else false
     */
    public boolean exists() {
        return (existsHdr() && existsDat());
    }

    /**
     * Check if a valid dataset header file exists.
     * @return true if header file exist, else false
     */
    public boolean existsHdr() {
        File f;
        f = new File(header.getDs_hdrname());
        if (f.exists()) {
            return true;
        }
        f = new File(header.getDs_hdrname() + Nifti1.GZIP_EXT);
        if (f.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Check if a valid dataset data file exists.
     * @return true if data file exist, else false
     */
    public boolean existsDat() {
        File f;
        f = new File(header.getDs_datname());
        if (f.exists()) {
            return true;
        }
        f = new File(header.getDs_datname() + Nifti1.GZIP_EXT);
        if (f.exists()) {
            return true;
        }
        return (false);
    }


}
