# Change Log

### Unreleased

### [v0.7](https://github.com/realityforge/gwt-cache-filter/tree/v0.7) (2017-02-02) · [Full Changelog](https://github.com/realityforge/gwt-cache-filter/compare/v0.6...v0.7)

* Add immutable parameter to Cache-Control to indicate cached resources should
  never be re-validated. This is a significant optimization for Firefox users.

### [v0.6](https://github.com/realityforge/gwt-cache-filter/tree/v0.6) (2014-11-03) · [Full Changelog](https://github.com/realityforge/gwt-cache-filter/compare/v0.5...v0.6)

* Set the asyncSupported flag to true for the filters.

### [v0.5](https://github.com/realityforge/gwt-cache-filter/tree/v0.5) (2014-08-04) · [Full Changelog](https://github.com/realityforge/gwt-cache-filter/compare/v0.4...v0.5)

* Ensure that the GWTGzipFilter sets the Content-Type, otherwise browsers guess
  the content type and some (IE9) get it wrong.

### [v0.4](https://github.com/realityforge/gwt-cache-filter/tree/v0.4) (2014-07-22) · [Full Changelog](https://github.com/realityforge/gwt-cache-filter/compare/v0.3...v0.4)

* Add GWTGzipFilter to serve compressed gwt artifacts. The filter optionally
  redirects any files to their .gz version, if it exists, for serving gzip files.
  Works best with com.google.gwt.precompress.Precompress prelinker, which
  generates .gz versions of all static files. Submitted by Ali Akhtar:

### [v0.3](https://github.com/realityforge/gwt-cache-filter/tree/v0.3) (2013-12-31) · [Full Changelog](https://github.com/realityforge/gwt-cache-filter/compare/v0.2...v0.3)

* Remove no-store from the Cache-control header as it disables offline application
  storage on Firefox.

### [v0.2](https://github.com/realityforge/gwt-cache-filter/tree/v0.2) (2013-07-05) · [Full Changelog](https://github.com/realityforge/gwt-cache-filter/compare/v0.1...v0.2)

* Add web fragment so that the filter is automatically added when added to war files.

### [v0.1](https://github.com/realityforge/gwt-cache-filter/tree/v0.1) (2013-07-04) · [Full Changelog](https://github.com/realityforge/gwt-cache-filter/compare/20f853ffa9c33f3eaee015df60ea66555d9ea9a6...v0.1)

* Initial release
