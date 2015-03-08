package boofcv;

import georegression.struct.line.LineParametric2D_F32;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

import boofcv.abst.feature.detect.line.DetectLineHoughFoot;
import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.line.ConfigHoughFoot;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.feature.ImageLinePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;

/**
 * Computer Vision with Java and BoofCV without JNI.
 * Sources: https://github.com/CriativaSoft/TableAnalysisBoofCV
 * @author Ricardo JL Rufino (ricardo@criativasoft.com.br)
 */
public class Step1 {

    public static void main( String args[] ) {

        float edgeThreshold = 30;
        int maxLines = 25;

        BufferedImage image = UtilImageIO.loadImage("data/frequencia.png");

        ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
        ImageFloat32 out = new ImageFloat32(image.getWidth(), image.getHeight());
        GBlurImageOps.gaussian(input, out, -1, 2, null);

        DetectLineHoughFoot<ImageFloat32, ImageFloat32> alg = FactoryDetectLineAlgs.houghFoot(
                new ConfigHoughFoot(6, 12, 5, edgeThreshold, maxLines), ImageFloat32.class, ImageFloat32.class);

        List<LineParametric2D_F32> lines = alg.detect(out);

        ImageLinePanel gui = new ImageLinePanel();
        gui.setBackground(image);
        gui.setLines(lines);
        gui.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        BufferedImage renderedBinary = VisualizeBinaryData.renderBinary(alg.getBinary(), null);

        ShowImages.showWindow(renderedBinary, "Detected Edges");
        ShowImages.showWindow(gui, "Detected Lines");
    }
}