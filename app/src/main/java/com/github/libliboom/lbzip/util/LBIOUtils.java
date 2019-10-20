package com.github.libliboom.lbzip.util;

import java.io.Closeable;
import java.io.IOException;

public class LBIOUtils {
//Why create the LBIOUtils class? to use repeatedly close method?
    public static void close(Closeable o) {
        if(o == null) return;

        try {
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
