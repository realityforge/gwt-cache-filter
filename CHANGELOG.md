## 0.5:

* Ensure that the GWTGzipFilter sets the Content-Type, otherwise browsers guess
  the content type and some (IE9) get it wrong.

## 0.4:

* Add GWTGzipFilter to serve compressed gwt artifacts. Submitted by aliakhtar.

## 0.3:

* Remove no-store from the Cache-control header as it disables offline application
  storage on Firefox.

## 0.2:

* Add web fragment so that the filter is automatically added when added to war files.

## 0.1:

* Initial release
