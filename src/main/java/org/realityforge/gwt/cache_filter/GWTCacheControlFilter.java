package org.realityforge.gwt.cache_filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter to disable caching of generated GWT files to ensure that the correct files get cached.
 */
@WebFilter( filterName = "GWTCacheControlFilter", urlPatterns = "/*", asyncSupported = true )
public class GWTCacheControlFilter
  extends AbstractFilter
{
  public static final int YEAR_IN_MINUTES = 365 * 24 * 60 * 60;

  @Override
  public void doFilter( final ServletRequest request,
                        final ServletResponse response,
                        final FilterChain filterChain )
    throws IOException, ServletException
  {

    final HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpServletResponse httpResponse = (HttpServletResponse) response;
    final String requestURI = httpRequest.getRequestURI();

    if ( requestURI.contains( ".nocache." ) )
    {
      final Date now = new Date();
      // set create date to current timestamp
      httpResponse.setDateHeader( "Date", now.getTime() );
      // set modify date to current timestamp
      httpResponse.setDateHeader( "Last-Modified", now.getTime() );
      // set expiry to back in the past (makes us a bad candidate for caching)
      httpResponse.setDateHeader( "Expires", 0 );
      // HTTP 1.0 (disable caching)
      httpResponse.setHeader( "Pragma", "no-cache" );
      // HTTP 1.1 (disable caching of any kind)
      // HTTP 1.1 'pre-check=0, post-check=0' => (Internet Explorer should always check)
      //Note: no-store is not included here as it will disable offline application storage on Firefox
      httpResponse.setHeader( "Cache-control", "no-cache, must-revalidate, pre-check=0, post-check=0" );
    }
    else if ( requestURI.contains( ".cache." ) )
    {
      // set expiry to back in the past (makes us a bad candidate for caching)
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime( new Date() );
      calendar.add( Calendar.YEAR, 1 );
      httpResponse.setDateHeader( "Expires", calendar.getTime().getTime() );
      //Note: immutable tells firefox to never revalidate as data will never change
      httpResponse.setHeader( "Cache-control", "max-age=" + YEAR_IN_MINUTES + ", public, immutable" );
      httpResponse.setHeader( "Pragma", "" );
    }

    filterChain.doFilter( request, response );
  }
}
