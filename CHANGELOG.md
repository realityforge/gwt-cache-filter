
## 0.4:
 Added GWTGzipControlFilter, which optionally redirects any files to their .gz version, if it
 exists, for serving gzip files. Works best with com.google.gwt.precompress.Precompress prelinker,
 which generates .gz versions of all static files. By Ali Akhtar, ali.rac200@gmail.com

## 0.3:

* Remove no-store from the Cache-control header as it disables offline application
  storage on Firefox.

## 0.2:

* Add web fragment so that the filter is automatically added when added to war files.

## 0.1:

* Initial release
