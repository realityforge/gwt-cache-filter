package org.realityforge.gwt.cache_filter;

import java.io.File;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Filter to redirect all files to their corresponding .gz file, if it exists, and serve
 * it with the proper encoding / content type. Works best if called after GWTCacheControlFilter
 */
public class GWTGzipControlFilter
  extends AbstractFilter
{
  private static String gzExt = ".gz";

  public static void setGzipExtension( String newExt )
  {
    if ( newExt != null )
    {
      gzExt = newExt;
    }
  }

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
      final String reqUrl = request.getRequestURI();
      final String realPath = request.getServletContext().getRealPath( reqUrl );
      final File file = null == realPath ? null : new File( realPath );
      if ( null == file || !file.exists() || file.isDirectory() )
      {
        filterChain.doFilter( request, response );
      }
      else if ( reqUrl.endsWith( gzExt ) )
      {
        final String mimeType = getMimeType( request, reqUrl );
        /***
         Just using response.setContentType() doesn't work, it gets reset
         when filterChain.doFilter() is called. A custom response wrapper has
         to be made, which would force the content type. See:
         http://stackoverflow.com/a/24846284/49153
         ****/
        if ( null != mimeType )
        {
          ForcableContentTypeWrapper newResponse = new ForcableContentTypeWrapper( response );
          newResponse.setHeader( "Content-Encoding", "gzip" );
          newResponse.forceContentType( mimeType );
          filterChain.doFilter( request, newResponse );
        }
        else
        {
          filterChain.doFilter( request, response );
        }
      }
      else
      {
        final String gzippedPath = realPath + gzExt;
        final File gzippedFile = new File( gzippedPath );

        if ( !gzippedFile.exists() || gzippedFile.isDirectory() )
        {
          filterChain.doFilter( request, servletResponse );
        }
        else
        {
          final String gzippedUrl = reqUrl + gzExt;
          response.sendRedirect( gzippedUrl );
          filterChain.doFilter( request, response );
        }
      }
    }
  }

  public static boolean acceptsGzip( final HttpServletRequest request )
  {
    final String header = request.getHeader( "Accept-Encoding" );
    return null != header && header.contains( "gzip" );
  }

  public static String getMimeType( final HttpServletRequest request, String filePath )
  {
    filePath = filePath.substring( 0, filePath.length() - gzExt.length() );
    final String ext = filePath.substring( filePath.lastIndexOf( "." ), filePath.length() );
    return request.getServletContext().getMimeType( ext );
  }

  private class ForcableContentTypeWrapper
    extends HttpServletResponseWrapper
  {
    public ForcableContentTypeWrapper( final HttpServletResponse response )
    {
      super( response );
    }

    @Override
    public void setContentType( final String type )
    {
    }

    public void forceContentType( final String type )
    {
      super.setContentType( type );
    }
  }
}
