gwt-cache-filter
================

[![Build Status](https://secure.travis-ci.org/realityforge/gwt-cache-filter.png?branch=master)](http://travis-ci.org/realityforge/gwt-cache-filter)

A simple servlet filter that adds the appropriate caching attributes to GWT generated files based on *.cache.* and *.nocache.* naming patterns.


Quick Start
===========

The simplest way to use the library is to add the following dependency
into the build system. i.e.

```xml
<dependency>
    <groupId>org.realityforge.gwt.cache-filter</groupId>
    <artifactId>gwt-cache-filter</artifactId>
    <version>0.3</version>
</dependency>
```

This will apply a filter across your entire application. Sometimes you will want to
restrict the filter so that it only covers the gwt part of the application in which
case you should add the following snippet to web.xml;

```xml
<filter>
  <filter-name>GWTCacheControlFilter</filter-name>
  <filter-class>org.realityforge.gwt.cache_filter.GWTCacheControlFilter</filter-class>
</filter>
<filter-mapping>
  <filter-name>GWTCacheControlFilter</filter-name>
  <url-pattern>/example</url-pattern>
</filter-mapping>
```
