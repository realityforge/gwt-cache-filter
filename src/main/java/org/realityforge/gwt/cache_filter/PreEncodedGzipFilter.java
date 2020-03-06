package org.realityforge.gwt.cache_filter;

import javax.servlet.annotation.WebFilter;

/**
 * Filter to serve a .gz file using Content-Encoding gzip if the file exists and content encoding is supported.
 */
@WebFilter( filterName = "PreEncodedGzipFilter", urlPatterns = "/*", asyncSupported = true )
public class PreEncodedGzipFilter
  extends AbstractPreEncodedFilter
{
  public PreEncodedGzipFilter()
  {
    super( ".gz", "gzip" );
  }
}
