// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

import de.l3s.boilerpipe.sax.ElementAction;
import de.l3s.boilerpipe.labels.DefaultLabels;

public class ElementActionTest extends DomDistillerTestCase {
    private ElementAction getForHtml(String html) {
        Element container = Document.get().createDivElement();
        container.setInnerHTML(html);
        assertEquals(1, container.getChildCount());
        Element e = container.getFirstChildElement();
        return ElementAction.getForElement(e);
    }

    public void testFlush() {
        assertFalse(getForHtml("<span></span>").flush);
        assertTrue(getForHtml("<div></div>").flush);

        assertTrue(getForHtml("<span style=\"display: block\"></span>").flush);
        assertTrue(getForHtml("<span style=\"display: flex\"></span>").flush);

        assertFalse(getForHtml("<div style=\"display: inline\"></div>").flush);
    }

    public void testIsAnchor() {
        assertFalse(getForHtml("<span></span>").isAnchor);
        assertFalse(getForHtml("<div></div>").isAnchor);
        assertFalse(getForHtml("<a></a>").isAnchor);
        assertTrue(getForHtml("<a href=\"http://example.com\"></a>").isAnchor);
    }

    public void testChangesTagLevel() {
        assertFalse(getForHtml("<span></span>").changesTagLevel);
        assertFalse(getForHtml("<font></font>").changesTagLevel);
        assertFalse(getForHtml("<div style=\"display: inline\"></div>").changesTagLevel);

        assertTrue(getForHtml("<a></a>").changesTagLevel);
        assertTrue(getForHtml("<div></div>").changesTagLevel);
        assertTrue(getForHtml("<span style=\"display: inline-block\"></span>").changesTagLevel);
        assertTrue(getForHtml("<span style=\"display: block\"></span>").changesTagLevel);
        assertTrue(getForHtml("<div style=\"display: inline-block\"></div>").changesTagLevel);
        assertTrue(getForHtml("<table></table>").changesTagLevel);
    }

    private boolean hasLabel(ElementAction a, String label) {
        for (int i = 0; i < a.labels.length(); i++) {
            if (a.labels.get(i).equals(label)) {
                  return true;
            }
        }
        return false;
    }

    public void testLabels() {
        assertEquals(0, getForHtml("<span></span>").labels.length());
        assertEquals(0, getForHtml("<div></div>").labels.length());
        assertEquals(0, getForHtml("<p></p>").labels.length());

        assertEquals(2, getForHtml("<h1></h1>").labels.length());
        assertEquals(2, getForHtml("<h2></h2>").labels.length());
        assertTrue(hasLabel(getForHtml("<h1></h1>"),
                    DefaultLabels.H1));
        assertTrue(hasLabel(getForHtml("<h1></h1>"),
                    DefaultLabels.HEADING));

        assertEquals(1, getForHtml("<li></li>").labels.length());

        assertEquals(1, getForHtml("<nav></nav>").labels.length());
        assertTrue(hasLabel(getForHtml("<nav></nav>"),
                    DefaultLabels.STRICTLY_NOT_CONTENT));
        assertEquals(1, getForHtml("<aside></aside>").labels.length());
        assertTrue(hasLabel(getForHtml("<aside></aside>"),
                    DefaultLabels.STRICTLY_NOT_CONTENT));
    }

    public void testCommentsLabels() {
        assertFalse(hasLabel(getForHtml("<span></span>"), DefaultLabels.STRICTLY_NOT_CONTENT));
        assertFalse(hasLabel(getForHtml("<div></div>"), DefaultLabels.STRICTLY_NOT_CONTENT));

        assertTrue(hasLabel(getForHtml("<div class=\" comment \"></div>"),
                    DefaultLabels.STRICTLY_NOT_CONTENT));
        assertTrue(hasLabel(getForHtml("<div class=\"foo.1 comment-thing\"></div>"),
                    DefaultLabels.STRICTLY_NOT_CONTENT));
        assertTrue(hasLabel(getForHtml("<div id=\"comments\"></div>"),
                    DefaultLabels.STRICTLY_NOT_CONTENT));

        assertTrue(hasLabel(getForHtml("<div class=\"user-comments\"></div>"),
                    DefaultLabels.STRICTLY_NOT_CONTENT));
    }


}