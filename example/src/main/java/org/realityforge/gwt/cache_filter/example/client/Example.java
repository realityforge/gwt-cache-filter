package org.realityforge.gwt.cache_filter.example.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class Example
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    RootPanel.getBodyElement().setInnerHTML( "<h1>Html from GWT</h1>" );
  }
}
