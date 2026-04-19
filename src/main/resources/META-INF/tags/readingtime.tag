<%@ tag body-content="scriptless" import="net.thauvin.erik.readingtime.ReadingTime" %>
<%@ attribute name="wpm" type="java.lang.Integer" required="false" rtexprvalue="true" %>
<%@ attribute name="suffix" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="pluralSuffix" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="excludeImages" type="java.lang.Boolean" required="false" rtexprvalue="true" %>
<%@ attribute name="extraSeconds" type="java.lang.Integer" required="false" rtexprvalue="true" %>
<%@ attribute name="debug" type="java.lang.Boolean" required="false" rtexprvalue="true" %>
<%--
  ~ Copyright (c) 2020-2026, Erik C. Thauvin (erik@thauvin.net)
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
<jsp:doBody var="body" scope="page"/>
<%
    final Boolean debug = (Boolean) getJspContext().getAttribute("debug");
    final Boolean excludeImages = (Boolean) getJspContext().getAttribute("excludeImages");
    final Integer extraSeconds = (Integer) getJspContext().getAttribute("extraSeconds");
    final Integer wpm = (Integer) getJspContext().getAttribute("wpm");
    final String body = (String) getJspContext().getAttribute("body");
    final String pluralSuffix = (String) getJspContext().getAttribute("pluralSuffix");
    final String suffix = (String) getJspContext().getAttribute("suffix");

    if (body != null) {
        final ReadingTime rt = new ReadingTime(body);
        if (excludeImages != null) {
            rt.setExcludeImages(excludeImages);
        }
        if (extraSeconds != null) {
            rt.setExtraSeconds(extraSeconds);
        }
        if (pluralSuffix != null) {
            rt.setPluralSuffix(pluralSuffix);
        }
        if (suffix != null) {
            rt.setSuffix(suffix);
        }
        if (wpm != null) {
            rt.setWpm(wpm);
        }

        out.write(rt.calcReadingTime());

        if (debug != null && debug) {
            out.write("<!--\n");
            out.write("body: " + body + "\n");
            out.write("wpm: " + wpm + " (" + rt.getWpm() + ")\n");
            out.write("suffix: " + suffix + " (" + rt.getSuffix() + ")\n");
            out.write("pluralSuffix: " + pluralSuffix + " (" + rt.getPluralSuffix() + ")\n");
            out.write("excludeImages: " + excludeImages + " (" + rt.getExcludeImages() + ")\n");
            out.write("extraSeconds: " + extraSeconds + " (" + rt.getExtraSeconds() + ")\n");
            out.write("-->");
        }
    }
%>