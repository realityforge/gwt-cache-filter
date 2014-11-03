package org.realityforge.gwt.cache_filter;

import java.io.File;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter to serve a .gz file using Content-Encoding gzip if the file exists and content encoding is supported.
 *
 * For each request, this filter checks to see if:
 * 1) The browser accepts gzip encoding (via the Accept-Encoding header)
 *
 * 2) A file with the request url + .gz exists and is readable.
 *
 * 3) If those conditions match, it then sets the 'Content Encoding' header to gzip, and dispatches
 * the gzipped file instead of the non-gzipped version.
 *
 * Example: user goes to example.com/foo.js. If example.com/foo.js.gz exists, then
 * example.com/foo.js.gz is served instead of foo.js
 */
@WebFilter( filterName = "GWTGzipFilter", urlPatterns = "/*", asyncSupported = true )
public class GWTGzipFilter
  extends AbstractFilter
{
  private static final String GZIP_EXTENSION = ".gz";

  @Override
  public void doFilter( final ServletRequest servletRequest,
                        final ServletResponse servletResponse,
                        final FilterChain filterChain )
    throws IOException, ServletException
  {
    final HttpServletRequest request = (HttpServletRequest) servletRequest;
    final HttpServletResponse response = (HttpServletResponse) servletResponse;

    if ( !acceptsGzip( request ) )
    {
      filterChain.doFilter( request, response );
    }
    else
    {
      final String resourcePath = request.getServletPath();
      final String realPath = request.getServletContext().getRealPath( resourcePath );
      final File file = null == realPath ? null : new File( realPath );
      if ( null == file ||
           resourcePath.endsWith( GZIP_EXTENSION ) ||
           !file.isFile() )
      {
        filterChain.doFilter( request, response );
      }
      else
      {
        final String gzippedPath = realPath + GZIP_EXTENSION;
        final File gzippedFile = new File( gzippedPath );

        if ( !gzippedFile.isFile() )
        {
          filterChain.doFilter( request, servletResponse );
        }
        else
        {
          final RequestDispatcher dispatcher =
            request.getServletContext().getRequestDispatcher( resourcePath + GZIP_EXTENSION );
          response.setHeader( "Content-Encoding", "gzip" );
          final String mimeType = servletRequest.getServletContext().getMimeType( resourcePath );
          if ( null != mimeType )
          {
            response.setHeader( "Content-Type", mimeType );
          }
          dispatcher.include( request, response );
        }
      }
    }
  }

  private boolean acceptsGzip( final HttpServletRequest request )
  {
    final String header = request.getHeader( "Accept-Encoding" );
    return null != header && header.contains( "gzip" );
  }
}
