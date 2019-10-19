# lbzip

Decompressor with multi thread

## How to use

You can find latest version [jitpack.io](https://jitpack.io/#libliboom/lbzip/).

implement like the following code in your build.gradle

```java
dependencies {
    implementation 'com.github.libliboom:lbzip:0.0.1'
}
```

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
