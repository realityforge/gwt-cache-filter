## 0.7:

* Add immutable parameter to Cache-Control to indicate cached resources should
  never be re-validated. This is a significant optimization for Firefox users.

## 0.6:

* Set the asyncSupported flag to true for the filters.

## 0.5:

* Ensure that the GWTGzipFilter sets the Content-Type, otherwise browsers guess
  the content type and some (IE9) get it wrong.

## 0.4:

* Add GWTGzipFilter to serve compressed gwt artifacts. The filter optionally
  redirects any files to their .gz version, if it exists, for serving gzip files.
  Works best with com.google.gwt.precompress.Precompress prelinker, which
  generates .gz versions of all static files. Submitted by Ali Akhtar:

## 0.3:

* Remove no-store from the Cache-control header as it disables offline application
  storage on Firefox.

## 0.2:

* Add web fragment so that the filter is automatically added when added to war files.

## 0.1:

* Initial release
