package com.github.libliboom.lbzip.core;

import com.github.libliboom.lbzip.callback.DecompressListener;
import com.github.libliboom.lbzip.core.Decompressor.CountDownWatchDog;
import com.github.libliboom.lbzip.data.ZipInfo;
import com.github.libliboom.lbzip.util.LBIOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Decompress implements Runnable {

    private static final int SIZE_OF_BUFFER = 8 * 1024;

    private ZipInfo zipInfo;
    private CountDownWatchDog watchDog;
    private DecompressListener callback;

    public Decompress(ZipInfo zipInfo, CountDownWatchDog watchDog, DecompressListener callback) {
        this.zipInfo = zipInfo;
        this.watchDog = watchDog;
        this.callback = callback;
    }

    public ZipInfo getZipInfo() {
        return zipInfo;
    }

    @Override
    public void run() {
        final String targetDir = zipInfo.getTargetPath();
        final ZipEntry entry = zipInfo.getEntry();
        final ZipFile zfile = zipInfo.getZfile();

        String entryName = entry.getName();
        File destFile = new File(targetDir, entryName);
        File destinationParent = destFile.getParentFile();

        System.out.println("entry: " + entryName);

        // create the parent directory structure if needed
        destinationParent.mkdirs();

        if (!entry.isDirectory()) {
            try {
                unzipEntry(zfile, entry, destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        watchDog.countDownLatch.countDown();
        int percentage = getPercentage();
        callback.onDecompressing(percentage);
    }

    // Could you explain how compress and decompress works?
    private void unzipEntry(ZipFile zfile, ZipEntry entry, File dfile)
            throws IOException {
        final byte[] buf = new byte[SIZE_OF_BUFFER];
        InputStream is = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            is = zfile.getInputStream(entry);
            bis = new BufferedInputStream(is);

            fos = new FileOutputStream(dfile);
            bos = new BufferedOutputStream(fos, buf.length);

            int nbytes;
            while ((nbytes = bis.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, nbytes);
            }
            bos.flush();
        } finally {
            LBIOUtils.close(bos);
            LBIOUtils.close(fos);
            LBIOUtils.close(bis);
            LBIOUtils.close(is);
        }
    }

    private int getPercentage() {
        return watchDog.getRemainedPercentage();
    }
}
