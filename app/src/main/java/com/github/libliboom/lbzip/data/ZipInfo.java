package com.github.libliboom.lbzip.data;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipInfo {
    private String targetPath;
    private ZipFile zfile;
    private ZipEntry entry;

    public ZipInfo(String targetPath, ZipFile zfile, ZipEntry entry) {
        this.targetPath = targetPath;
        this.zfile = zfile;
        this.entry = entry;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public ZipFile getZfile() {
        return zfile;
    }

    public ZipEntry getEntry() {
        return entry;
    }
}
