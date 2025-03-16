<%--
  ~ Copyright (c) 2020-2025, Erik C. Thauvin (erik@thauvin.net)
  ~ All rights reserved.
  ~
  ~ Redistribution and use in source and binary forms, with or without
  ~ modification, are permitted provided that the following conditions are met:
  ~
  ~   Redistributions of source code must retain the above copyright notice, this
  ~   list of conditions and the following disclaimer.
  ~
  ~   Redistributions in binary form must reproduce the above copyright notice,
  ~   this list of conditions and the following disclaimer in the documentation
  ~   and/or other materials provided with the distribution.
  ~
  ~   Neither the name of this project nor the names of its contributors may be
  ~   used to endorse or promote products derived from this software without
  ~   specific prior written permission.
  ~
  ~ THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  ~ AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  ~ IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  ~ DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  ~ FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  ~ DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  ~ SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  ~ CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  ~ OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  ~ OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  --%>
<%@tag body-content="scriptless" import="net.thauvin.erik.readingtime.ReadingTime" trimDirectiveWhitespaces="true" %>
<%@attribute name="debug" type="java.lang.Boolean" %>
<%@attribute name="excludeImages" type="java.lang.Boolean" %>
<%@attribute name="extra" type="java.lang.Integer" %>
<%@attribute name="plural" %>
<%@attribute name="postfix" %>
<%@attribute name="wpm" type="java.lang.Integer" %>
<jsp:doBody var="body" scope="page"/>
<%
    final Boolean debug = (Boolean) getJspContext().getAttribute("debug");
    final Boolean excludeImages = (Boolean) getJspContext().getAttribute("excludeImages");
    final Integer extra = (Integer) getJspContext().getAttribute("extra");
    final Integer wpm = (Integer) getJspContext().getAttribute("wpm");
    final String body = (String) getJspContext().getAttribute("body");
    final String plural = (String) getJspContext().getAttribute("plural");
    final String postfix = (String) getJspContext().getAttribute("postfix");

    if (body != null) {
        final ReadingTime rt = new ReadingTime(body);
        if (excludeImages != null) rt.setExcludeImages(excludeImages);
        if (extra != null) rt.setExtra(extra);
        if (plural != null) rt.setPlural(plural);
        if (postfix != null) rt.setPostfix(postfix);
        if (wpm != null) rt.setWpm(wpm);
        out.write(rt.calcReadingTime());
        if (debug != null && debug) {
            out.write("<!--\n" + "body: " + body + "\n");
            out.write("wpm: " + wpm + " (" + rt.getWpm() + ")\n");
            out.write("postfix: " + postfix + " (" + rt.getPostfix() + ")\n");
            out.write("plural: " + plural + " (" + rt.getPlural() + ")\n");
            out.write("excludeImages: " + excludeImages + " (" + rt.getExcludeImages() + ")\n");
            out.write("extra: " + extra + " (" + rt.getExtra() + ")\n-->");
        }
    }
%>
