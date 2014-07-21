package org.realityforge.gwt.cache_filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

abstract class AbstractFilter
  implements Filter
{
  @Override
  public void init( final FilterConfig config )
    throws ServletException
  {
  }

  @Override
  public void destroy()
  {
  }
}
