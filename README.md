# lbzip

Decompressor with multi thread

## How to use

```java
public void decompress(String desPath, String zfilePath, DecompressListener listener) {
    int nprocess = Runtime.getRuntime().availableProcessors()/2;
    if(nprocess == 0) nprocess = 1;

    mDecompressor = new Decompressor(nprocess, listener);
    mDecompressor.unzip(desPath, zfilePath);
}
```

## License

lbzip is licensed under the MIT License. You can find the license text in the LICENSE file.
