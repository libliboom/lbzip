package com.github.libliboom.lbzip.callback;

public interface DecompressListener {
    void onStarted();
    void onReStarted();
    void onDecompressing(int percentage);
    void onCanceled();
    void onCompleted();
}
