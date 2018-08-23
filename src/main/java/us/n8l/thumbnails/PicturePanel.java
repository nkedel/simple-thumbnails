package us.n8l.thumbnails;

import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

class PicturePanel extends JPanel {
  BufferedImage image = new BufferedImage(600, 800, BufferedImage.TYPE_INT_RGB);

  void setImage(BufferedImage image) {
    this.image = image;
  }

  @Override
  protected void paintComponent(java.awt.Graphics oG) {
    super.paintComponent(oG);
    Graphics2D g = (Graphics2D) oG;
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
  }

}
