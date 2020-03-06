package org.realityforge.gwt.cache_filter;

import javax.servlet.annotation.WebFilter;

/**
 * Filter to serve a .br file using Content-Encoding br if the file exists and content encoding is supported.
 */
@WebFilter( filterName = "PreEncodedBrotliFilter", urlPatterns = "/*", asyncSupported = true )
public class PreEncodedBrotliFilter
  extends AbstractPreEncodedFilter
{
  public PreEncodedBrotliFilter()
  {
    super( ".br", "br" );
  }
}
