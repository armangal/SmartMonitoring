/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.clevermore.monitor.client.utils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class CssLinkLoader {

    public static interface OnLoad {

        void loaded();

    }

    private static final String SCREEN_CSS_SERVER_FOLDER = "css/";

    /**
     * All screen related CSS files are kept in one folder on the server. This is a convenience method which
     * knows in which folder the those CSS files are located. When referencing images and other resources from
     * the CSS use paths relative to this CSS file.
     * 
     * @param cssFileName
     */
    public static void loadScreenCss(String cssFileName) {
        String screenCssFileName = SCREEN_CSS_SERVER_FOLDER + cssFileName;
        if (Document.get().getElementById(screenCssFileName) == null) {
            loadCss(screenCssFileName);
        }
    }

    public static void unloadCss(String cssFileName) {
        Element cssElement = Document.get().getElementById(cssFileName);
        if (cssElement != null) {
            cssElement.removeFromParent();
        }
    }

    private static native Boolean isCssLinkLoaded(String cssFileName) 
    /*-{
          var link = $doc.getElementById(cssFileName);
          try {
              if (link.sheet && link.sheet.cssRules.length > 0) {
                  return true;
              } else if (link.styleSheet && link.styleSheet.cssText.length > 0) {
                  return true;
              } else if (link.innerHTML && link.innerHTML.length > 0) {
                  return true;
              }
          } catch (ex) {
          }
          return false;
    }-*/;

    private static native void loadCss(String url)
    /*-{
       var l = $doc.createElement("link");
       l.setAttribute("id", url);
       l.setAttribute("rel", "stylesheet");
       l.setAttribute("type", "text/css");
       l.setAttribute("href", url + "?v=1"); // Make sure this request is not cached
       $doc.getElementsByTagName("head")[0].appendChild(l);
   }-*/;

}
