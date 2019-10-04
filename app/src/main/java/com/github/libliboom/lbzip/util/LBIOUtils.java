package com.github.libliboom.lbzip.util;

import java.io.Closeable;
import java.io.IOException;

public class LBIOUtils {

    public static void close(Closeable o) {
        if(o == null) return;

        try {
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
