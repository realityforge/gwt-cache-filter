package org.realityforge.gwt.cache_filter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter to serve a pre-encoded file if the request matches appropriate Content-Encoding if the pre-encoded file exists and content encoding is supported.
 *
 * For each request, this filter checks to see if:
 * 1) The browser accepts encoding (via the Accept-Encoding header)
 *
 * 2) A file with the request url + [extension] exists and is readable.
 *
 * 3) If those conditions match, it then sets the 'Content Encoding' header to [encoding], and dispatches
 * the encoded file instead of the non-encoded version.
 *
 * Example: Assuming a brotli encoding 'br' with extension '.br'. The user goes to example.com/foo.js.
 * If example.com/foo.js.br exists, then example.com/foo.js.br is served instead of foo.js
 */
public abstract class AbstractPreEncodedFilter
  extends AbstractFilter
{
  private final String _extension;
  private final String _encoding;

  protected AbstractPreEncodedFilter( final String extension, final String encoding )
  {
    _extension = Objects.requireNonNull( extension );
    _encoding = Objects.requireNonNull( encoding );
  }

  @Override
  public void doFilter( final ServletRequest servletRequest,
                        final ServletResponse servletResponse,
                        final FilterChain filterChain )
    throws IOException, ServletException
  {
    final HttpServletRequest request = (HttpServletRequest) servletRequest;
    final HttpServletResponse response = (HttpServletResponse) servletResponse;

    if ( !acceptsEncoding( request ) )
    {
      filterChain.doFilter( request, response );
    }
    else
    {
      final String resourcePath = request.getServletPath();
      final String realPath = request.getServletContext().getRealPath( resourcePath );
      final File file = null == realPath ? null : new File( realPath );
      if ( null == file || resourcePath.endsWith( _extension ) || !file.isFile() )
      {
        filterChain.doFilter( request, response );
      }
      else
      {
        final String encodedPath = realPath + _extension;
        final File encodedFile = new File( encodedPath );

        if ( !encodedFile.isFile() )
        {
          filterChain.doFilter( request, servletResponse );
        }
        else
        {
          final RequestDispatcher dispatcher =
            request.getServletContext().getRequestDispatcher( resourcePath + _extension );
          response.setHeader( "Content-Encoding", _encoding );
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

  private boolean acceptsEncoding( final HttpServletRequest request )
  {
    final String header = request.getHeader( "Accept-Encoding" );
    return null != header && header.contains( _encoding );
  }
}
