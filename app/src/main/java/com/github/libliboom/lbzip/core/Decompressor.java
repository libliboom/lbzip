package com.github.libliboom.lbzip.core;

import com.github.libliboom.lbzip.callback.DecompressListener;
import com.github.libliboom.lbzip.data.ZipInfo;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Decompressor {
    // Is there a reason of fixing size of array to 100?
    private static final int SIZE_OF_ARRAY = 100;

    private final int nthreads;
    private final DecompressListener callback;

    private int nentries;
    private BlockingQueue<Decompress> entriesQueue;

    {
        entriesQueue = new ArrayBlockingQueue<>(SIZE_OF_ARRAY);
    }
    //What if People doesn't know how many threads are suitable for decompressing?
    //Can it be provided as default value?
    public Decompressor(int nthreads, DecompressListener callback) {
        this.nthreads = nthreads;
        this.callback = callback;

        if(this.callback == null)
            throw new RuntimeException("ERROR: YOU MUST SET DECOMPRESSLISTENER OBJECT FOR DECOMPRESSOR");
    }

    public void unzip(String targetPath, String zfilePath) {

        callback.onStarted(); // or listener.onReStarted();

        CountDownLatch countDownLatch = null;

        try {
            File file = new File(zfilePath);
            int count = getCountOfEntries(file);
            nentries = count;
            countDownLatch = new CountDownLatch(count);
            enqueue(file, targetPath, new CountDownWatchDog(nentries, countDownLatch));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ExecutorService executor = Executors.newFixedThreadPool(nthreads);
            while(entriesQueue.size() > 0) {
                Decompress d = entriesQueue.take();
                executor.execute(d);
                System.out.println("decompress: " + d.getZipInfo().getEntry().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            countDownLatch.await();
            callback.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int getCountOfEntries(File file)
            throws IOException {
        ZipFile zfile = new ZipFile(file);
        Enumeration entries = zfile.entries();

        int count = 0;
        while (entries.hasMoreElements()) {
            ++count;
            entries.nextElement();
        }
        return count;
    }

    private void enqueue(File file, String targetPath, CountDownWatchDog countDownLatch)
            throws IOException {
        ZipFile zfile = new ZipFile(file);
        Enumeration entries = zfile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            ZipInfo zipInfo = new ZipInfo(targetPath, zfile, entry);
            Decompress decompress = new Decompress(zipInfo, countDownLatch, callback);
            entriesQueue.add(decompress);
        }
    }

    /*package*/ class CountDownWatchDog {
        /*package*/ float total;
        /*package*/ CountDownLatch countDownLatch;

        public CountDownWatchDog(float total, CountDownLatch countDownLatch) {
            this.total = total;
            this.countDownLatch = countDownLatch;
        }

        /*package*/ int getRemainedPercentage() {
            return (int)(100-(countDownLatch.getCount()/total*100));
        }
    }
}

