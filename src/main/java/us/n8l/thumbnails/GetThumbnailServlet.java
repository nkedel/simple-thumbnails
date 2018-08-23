package us.n8l.thumbnails;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GetThumbnailServlet extends HttpServlet {
  Logger log = LoggerFactory.getLogger(getClass());

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Capture capture = Bootstrap.getCapture();
    String urlString = request.getParameter("url");
    if (StringUtils.isBlank(urlString)) {
      log.warn("Blank short url.");
      response.sendError(400, "You must provide a non-blank URL.");
      return;
    }
    log.info("Got thumbnail request for : " + urlString);
    CompletableFuture<BufferedImage> future = capture.loadURL(urlString);
    try {
      response.setContentType("image/png");
      BufferedImage bi = future.get();
      ImageIO.write(bi, "png", response.getOutputStream());
    } catch (InterruptedException e) {
      log.warn("Interrupted", e);
    }
      catch (ExecutionException e) {
        log.warn("Execution failed", e);
    }
  }
}