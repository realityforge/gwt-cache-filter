package org.realityforge.gwt.cache_filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Checks to see if a gzipped file for the given request url exists, and serves that file if
 * it does (and if its isn't present in its ignore list). This filter doesn't call chain.doFilter
 * if a gzipped file is found. It is best placed AFTER GWTCacheControlFilter in your web.xml,
 * so any cache headers would be set before the gzipped file is served.
 *
 * For each request, this filter checks to see if:
 * 1) The browser accepts gzip encoding (via the Accept-Encoding header)
 *
 * 2) A file with the request url + .gz exists and is readable, and isn't in the ignore list,
 *   and has a mime type available for it. (The .gz extension is configurable, see below).
 *
 * 3) If those conditions match, it then sets the 'Content Encoding' header to gzip, and serves
 * the gzipped file instead of the non-gzipped version.
 *
 * 4) This filter does not call chain.doFilter() if the above conditions match. It should be
 * placed at the end of all other filters in your web.xml.
 *
 * Example: user goes to example.com/foo.js. If example.com/foo.js.gz exists, then
 * example.com/foo.js.gz is served instead of foo.js
 *
 * IMPORTANT: This filter does not call chain.doFilter() if a gzipped file is matched. It simply
 * serves that file.
 *
 * IMPORTANT: Make sure to add any urls that you don't want to be served (but which have
 * a .gz file available) to the ignore list. This can be done by passing them as an
 * init parameter, as a comma separated string.
 *
 * <init-param>
 *     <param-name>ignoredList</param-name>
 *    <param-value>foo/bar/baz.html, bar*</param-value>
 * </init-param>
 *
 * In the above case, any urls matching /foo/bar/baz.html would be ignored, even
 * if the file /foo/bar/baz.html.gz exists. Similarly, any urls that start with bar will not
 * be served. E.g /bar1.js, /bar2.png, /bar3/foo/1.html, etc.
 *
 * WEB-INF* is added to the ignored list by default.
 *
 * The default extension for gzipped files is ".gz", it can be changed via this parameter:
 *
 *<init-param>
 *     <param-name>gzipExtension</param-name>
 *     <param-value>.foo</param-value>
 *</init-param>
 *
 * TODO: Check for the 304 header in the incoming request, and use file.lastModified() to
 * see if the file has modified since the last time it was viewed. If the file hasn't
 * been modified, send a 'not modified' header, otherwise serve the file.
 *
 */

public class GWTGzipControlFilter implements Filter
{
    private static String gzExt = ".gz";
    private static final Map<String, String> mimeTypes = new HashMap<>();
    private static final List<String> ignoredUrls = new ArrayList<>();

    static
    {
        ignoredUrls.add("WEB-INF/*");

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

    public static boolean addIgnoredUrl(String url)
    {
        url = url.trim().toLowerCase();
        if (url.isEmpty() )
            return false;

        if (! url.startsWith("/"))
            url = "/" + url;

        ignoredUrls.add(url.toLowerCase() );
        return true;
    }

    @Override
    public void init(FilterConfig config) throws ServletException
    {
        setGzipExtension( config.getInitParameter("gzipExtension")  );
        String[] ignoredUrls = parseByComma( config.getInitParameter("ignoredUrls") );
        if (ignoredUrls != null && ignoredUrls.length > 0)
        {
            for (String ignoredUrl : ignoredUrls)
            {
                addIgnoredUrl(ignoredUrl);
            }
        }
    }

    private String[] parseByComma(String param)
    {
        if (param == null)
            return null;

        if (param.isEmpty() )
            return null;

        return param.split(",");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException
    {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String reqUrl = request.getRequestURI();

        if (! acceptsGzip(request) || isIgnored(reqUrl))
        {
            filterChain.doFilter(request, response);
            return;
        }

        String reqPath = request.getServletContext().getRealPath( reqUrl );
        File reqFile = new File(reqPath);
        if (! reqFile.exists() || reqFile.isDirectory() )
        {
            filterChain.doFilter(request, response);
            return;
        }

        String gzippedPath = reqPath + gzExt;
        File gzippedFile = new File(gzippedPath );

        if (! gzippedFile.exists() || gzippedFile.isDirectory() || ! gzippedFile.canRead() )
        {
            filterChain.doFilter( request, servletResponse );
            return;
        }
        String mime = getMimeType(reqUrl);
        if (mime != null)
            sendGzipped(request, response, mime, gzippedFile);
        else
            filterChain.doFilter(request, response);
    }

    private boolean isIgnored(String url)
    {
        url = url.toLowerCase();
        for (String ignoredUrl : ignoredUrls)
        {
            boolean hasWildCard = ignoredUrl.endsWith("*");
            if (! hasWildCard && url.equals(ignoredUrl) )
                return true;

            if (! hasWildCard)
                continue;

            String cleanedIgnored = ignoredUrl.substring(0, ignoredUrl.length() - 1);
            if ( url.startsWith(cleanedIgnored) )
                return true;
        }
        return false;
    }

    private void sendGzipped(HttpServletRequest request, HttpServletResponse response,
                             String mimeType, File gzippedFile)
            throws IOException
    {
        response.setStatus(200);
        if ( response.getHeader("Date") == null)
            response.setDateHeader("Date", new Date().getTime() );

        if (response.getHeader("Last-Modified") == null)
            response.setDateHeader("Last-Modified", gzippedFile.lastModified());
        response.setHeader("Content-Encoding", "gzip");
        response.setContentType( mimeType );
        response.setContentLength( (int) gzippedFile.length() );
        readBinaryData(gzippedFile, response);
    }

    private void readBinaryData(File binaryFile, ServletResponse response)
            throws IOException
    {
        FileInputStream is = new FileInputStream(binaryFile);
        OutputStream out = response.getOutputStream();
        byte[] buffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1)
        {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
        is.close();
    }

    public static boolean acceptsGzip(HttpServletRequest request)
    {
        String header = request.getHeader("Accept-Encoding");
        return ( header != null && header.contains("gzip") );
    }

    public static String getMimeType(String filePath)
    {
        String ext = filePath.substring( filePath.lastIndexOf("."), filePath.length() );
        return (mimeTypes.containsKey(ext)) ? mimeTypes.get(ext) : null;
    }

    @Override
    public void destroy(){}
}
