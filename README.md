gwt-cache-filter
================

[![Build Status](https://secure.travis-ci.org/realityforge/gwt-cache-filter.png?branch=master)](http://travis-ci.org/realityforge/gwt-cache-filter)

The project consists of two filters. The `GWTCacheControlFilter` adds the appropriate
caching attributes to GWT generated files based on *.cache.* and *.nocache.* naming
patterns. The `GWTGzipFilter` will serve a gzipped variant of a static file
if one is present on the file system and the request specifies the "Accept-Encoding"
http header to include "gzip". The gzipped variant of the static file should exist
on the file system with he same name as the resource but with ".gz" suffix.

Quick Start
===========

The simplest way to use the library is to add the following dependency
into the build system. i.e.

```xml
<dependency>
    <groupId>org.realityforge.gwt.cache-filter</groupId>
    <artifactId>gwt-cache-filter</artifactId>
    <version>0.4</version>
</dependency>
```

This will apply a filter across your entire application. Almost always you will want
to restrict the filter so that it only covers the gwt part of the application in which
case you should add the following snippet to web.xml;

```xml
  <filter>
    <filter-name>GWTCacheControlFilter</filter-name>
    <filter-class>org.realityforge.gwt.cache_filter.GWTCacheControlFilter</filter-class>
  </filter>
  <filter>
    <filter-name>GWTGzipFilter</filter-name>
    <filter-class>org.realityforge.gwt.cache_filter.GWTGzipFilter</filter-class>
  </filter>
  <filter-mapping>
    <filter-name>GWTCacheControlFilter</filter-name>
    <url-pattern>/example/*</url-pattern>
  </filter-mapping>
  <filter-mapping>
    <filter-name>GWTGzipFilter</filter-name>
    <url-pattern>/example/*</url-pattern>
  </filter-mapping>
```

To get the GWT compiler to generate the gzipped files for you, the easiest way to add
the following inherits to the ".gwt.xml" file.

```xml
  <inherits name="com.google.gwt.precompress.Precompress"/>
```

A very simple example of this code is available in the
[gwt-cache-filter-example](https://github.com/realityforge/gwt-cache-filter-example)
project.
