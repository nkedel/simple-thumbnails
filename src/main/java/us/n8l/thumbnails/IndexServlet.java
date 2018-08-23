package us.n8l.thumbnails;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class IndexServlet extends HttpServlet {
    Logger log = LoggerFactory.getLogger(getClass());

    // language=HTML
    public static final String FORM = "<html><head>Links Test</head><body>" +
            "<form method=\"post\" action=\"%s\">\n" +
            "  URL: \n <input type=\"text\" name=\"url\"><br>\n" +
            "  <input type=\"checkbox\" name=\"duplicates\" value=\"true\"> If checked, return the same code if URL was seen before.<br/>" +
            "  <input type=\"submit\" value=\"Submit\">\n" +
            "</form> " +
            "</body>";

    // language=HTML
    public static final String RETURN_TEMPLATE = "<html><head>Links Test</head><body>Your new short URL : <pre>%sl/%s</pre></body>";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Got base URL GET request.");
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();
        pw.println(String.format(FORM, Bootstrap.getBaseUrl()));
    }
}
