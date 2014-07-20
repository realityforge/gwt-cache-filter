package org.realityforge.gwt.cache_filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter to redirect all files to their corresponding .gz file, if it exists, and serve
 * it with the proper encoding / content type. Works best if called after GWTCacheControlFilter
 */
public class GWTGzipControlFilter implements Filter
{
    private static String gzExt = ".gz";
    private static final Map<String, String> mimeTypes = new HashMap<>();

    static
    {
        mimeTypes.put(".txt", "text/plain");
        mimeTypes.put(".html", "text/html");
        mimeTypes.put(".xhtml", "application/xhtml+xml");
        mimeTypes.put(".xml", "application/xml");
        mimeTypes.put(".rss", "application/xml+rss");
        mimeTypes.put(".js", "text/javascript");
        mimeTypes.put(".css", "text/css");
        mimeTypes.put(".png", "image/png");
        mimeTypes.put(".gif", "image/gif");
        mimeTypes.put(".jpeg", "image/jpeg");
        mimeTypes.put(".jpg", "image/jpeg");
        mimeTypes.put(".gz", "application/x-gzip");
    }

    public static void setGzipExtension(String newExt)
    {
        if (newExt != null)
            gzExt = newExt;
    }

    public static void addMimeType(String fileExtWithDot, String mimeType)
    {
        mimeTypes.put(fileExtWithDot, mimeType);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (! acceptsGzip(request))
        {
            filterChain.doFilter(request, response);
            return;
        }

        String reqUrl = request.getRequestURI();
        String reqPath = request.getServletContext().getRealPath( reqUrl );
        File reqFile = new File(reqPath);
        if (! reqFile.exists() || reqFile.isDirectory() )
        {
            filterChain.doFilter(request, response);
            return;
        }

        if (reqUrl.endsWith(gzExt))
        {
            /***
             Just using response.setContentType() doesn't work, it is get reset
             when filterChain.doFilter() is called. A custom response wrapper has
             to be made, which would force the content type. See:
             http://stackoverflow.com/a/24846284/49153
             ****/

            ForcableContentTypeWrapper newResponse = new ForcableContentTypeWrapper(response);
            newResponse.setHeader("Content-Encoding", "gzip");
            newResponse.forceContentType( getMimeType(reqUrl, true) );
            filterChain.doFilter(request, newResponse);
            return;
        }

        String gzippedPath = reqPath + gzExt;
        File gzippedFile = new File(gzippedPath );

        if (! gzippedFile.exists() || gzippedFile.isDirectory() )
        {
            filterChain.doFilter( request, servletResponse );
            return;
        }

        String gzippedUrl = reqUrl + gzExt;

        response.sendRedirect(gzippedUrl);
        filterChain.doFilter(request, response);
    }

    public static boolean acceptsGzip(HttpServletRequest request)
    {
        String header = request.getHeader("Accept-Encoding");
        return ( header != null && header.contains("gzip") );
    }

    public static String getMimeType(String filePath, boolean removeGzExtension)
    {
        if (removeGzExtension)
            filePath = filePath.substring(0, filePath.length() - gzExt.length());

        String ext = filePath.substring( filePath.lastIndexOf("."), filePath.length() );
        return (mimeTypes.containsKey(ext)) ? mimeTypes.get(ext) : mimeTypes.get(gzExt);
    }

    @Override
    public void destroy(){}

    private class ForcableContentTypeWrapper extends HttpServletResponseWrapper
    {
        public ForcableContentTypeWrapper(HttpServletResponse response)
        {
            super(response);
        }

        @Override
        public void setContentType(String type)
        {
        }
        public void forceContentType(String type)
        {
            super.setContentType(type);
        }
    }
}