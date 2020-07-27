<%--
    Copyright (c) 2020, Erik C. Thauvin (erik@thauvin.net)
    All rights reserved.

    See https://opensource.org/licenses/BSD-3-Clause
--%>
<%@tag body-content="scriptless" import="net.thauvin.erik.readingtime.ReadingTime" trimDirectiveWhitespaces="true"%>
<%@attribute name="debug" type="java.lang.Boolean"%>
<%@attribute name="excludeImages" type="java.lang.Boolean"%>
<%@attribute name="plural"%>
<%@attribute name="postfix"%>
<%@attribute name="wpm" type="java.lang.Integer"%>
<jsp:doBody var="body" scope="page"/>
<%
    final Boolean debug = (Boolean) getJspContext().getAttribute("debug");
    final Boolean excludeImages = (Boolean) getJspContext().getAttribute("excludeImages");
    final Integer wpm = (Integer) getJspContext().getAttribute("wpm");
    final String body = (String) getJspContext().getAttribute("body");;
    final String plural = (String) getJspContext().getAttribute("plural");
    final String postfix = (String) getJspContext().getAttribute("postfix");

    if (body != null) {
        final ReadingTime rt = new ReadingTime(body);
        if (excludeImages != null) rt.setExcludeImages(excludeImages);
        if (plural != null) rt.setPlural(plural);
        if (postfix != null) rt.setPostfix(postfix);
        if (wpm != null) rt.setWpm(wpm);
        out.write(rt.calcReadingTime());
        if (debug != null && debug) {
            out.write("<!--\n" + "body: " + body + "\n");
            out.write("wpm: " + wpm + " (" + rt.getWpm() + ")\n");
            out.write("postfix: " + postfix + " (" + rt.getPostfix() + ")\n");
            out.write("plural: " + plural + " (" + rt.getPlural() + ")\n");
            out.write("excludeImages: " + excludeImages + " (" + rt.getExcludeImages() + ") -->");
        }
    }
%>
