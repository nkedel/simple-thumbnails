package us.n8l.thumbnails;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Capture {
  private JFrame frame;
  private WebEngine engine = null;
  private WebView view;
  private JFXPanel jfxPanel;
  private ConcurrentLinkedQueue<Pair<String, CompletableFuture<BufferedImage>>> queue = new ConcurrentLinkedQueue<>();
  private AtomicBoolean isIdle = new AtomicBoolean(true);
  private AtomicReference<CompletableFuture<BufferedImage>> nextFuture = new AtomicReference<>();

  Capture() {
    frame = new JFrame();
    jfxPanel = new JFXPanel();
    Platform.runLater(this::initializeBrowser);
    frame.add(jfxPanel);
    frame.setMinimumSize(new Dimension(800, 1200));
    frame.pack();
    EventQueue.invokeLater(() -> {
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setVisible(true);
    });
  }

  @Nullable
  private static String toURL(String str) {
    try {
      return new URL(str).toExternalForm();
    } catch (MalformedURLException exception) {
      return null;
    }
  }

  private void initializeBrowser() {
    view = new WebView();
    setEngine(view.getEngine());
    Scene scene = new Scene(view);
    jfxPanel.setScene(scene);
  }

  private void handleWebLoaded(ObservableValue<? extends Worker.State> observable,
                               Worker.State oldValue, Worker.State newValue) {
    if (newValue != Worker.State.SUCCEEDED) {
      return;
    }
    CompletableFuture<BufferedImage> future = nextFuture.getAndSet(null);
    if (future == null) {
      if (!isIdle.get()) {
        Platform.runLater(this::getNextUrl);
      }
      return;
    }
    WritableImage image = view.snapshot(null, null);
    JFrame newFrame = new JFrame();
    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
    future.complete(bufferedImage);
    Platform.runLater(this::getNextUrl);
  }

  private void setEngine(WebEngine engine) {
    this.engine = engine;
  }

  CompletableFuture<BufferedImage> loadURL(final String url) {
    String tmp = fixupUrl(url);
    if (tmp != null) {
      CompletableFuture<BufferedImage> future = new CompletableFuture<>();
      queue.offer(Pair.of(tmp, future));
      if (isIdle.getAndSet(false)) {
        Platform.runLater(this::getNextUrl);
      }
      return future;
    }
    return null;
  }

  private void getNextUrl() {
    Pair<String, CompletableFuture<BufferedImage>> poll = queue.poll();
    if (poll == null) {
      isIdle.set(true);
      return;
    }
    nextFuture.set(poll.getRight());
    engine.getLoadWorker()
        .stateProperty()
        .addListener(this::handleWebLoaded);
    engine.load(poll.getLeft());
  }

  private String fixupUrl(String url) {
    String tmp = toURL(url);

    if (tmp == null) {
      tmp = toURL("http://" + url);
    }
    return tmp;
  }
}
