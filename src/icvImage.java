import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class icvImage {

    private final String filename;
    private final BufferedImage img;

    public icvImage(String filename) throws IOException {
        this.filename = filename;
        this.img = ImageIO.read(new File(filename));
    }

    public BufferedImage getBufferedImage() {
        return this.img;
    }
}
