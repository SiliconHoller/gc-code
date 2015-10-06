function im = rload(fname, im_size)

fid = fopen([fname], 'rb');
im = fread(fid, im_size, 'uchar');
fclose(fid);