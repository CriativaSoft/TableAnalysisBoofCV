package boofcv;

import georegression.metric.Intersection2D_F32;
import georegression.metric.UtilAngle;
import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.line.LineSegment2D_F32;
import georegression.struct.point.Point2D_F32;
import georegression.struct.shapes.RectangleLength2D_F32;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import util.PointComparator;
import util.RectangleUtils;
import boofcv.abst.feature.detect.line.DetectLineHoughFoot;
import boofcv.abst.feature.detect.line.DetectLineSegmentsGridRansac;
import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.line.ConfigHoughFoot;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;

/**
 * Computer Vision with Java and BoofCV without JNI.
 * Sources: https://github.com/CriativaSoft/TableAnalysisBoofCV
 * @author Ricardo JL Rufino (ricardo@criativasoft.com.br)
 */
public class Step5 {

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
        List<LineParametric2D_F32> hlines = new LinkedList<LineParametric2D_F32>();
        List<LineParametric2D_F32> vlines = new LinkedList<LineParametric2D_F32>();

        for (LineParametric2D_F32 pline : lines) {
            long angle = Math.abs(Math.round(UtilAngle.radianToDegree(pline.getAngle())));
            if (angle == 0 || angle == 180) {
                hlines.add(pline);
            } else if (angle > 80 && angle <= 100) {
                vlines.add(pline);
            }
        }

        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setColor(Color.RED);

        List<Point2D_F32> intersectionPoints = new ArrayList<Point2D_F32>();
        for (LineParametric2D_F32 hline : hlines) {
            for (LineParametric2D_F32 vline : vlines) {
                Point2D_F32 intersection = Intersection2D_F32.intersection(hline, vline, null);
                if (intersection.x > 0) {
                    intersectionPoints.add(intersection);
                    g2.fillRect((int) intersection.x, (int) intersection.y, 4, 4);
                }
            }
        }

        Collections.sort(intersectionPoints, new PointComparator(2));

        List<RectangleLength2D_F32> cells = RectangleUtils.find(intersectionPoints, vlines.size(), 3);

        int start = 19, interval = 5;
        List<RectangleLength2D_F32> assinaturasrect = new LinkedList<RectangleLength2D_F32>();
        for (int i = start; i < cells.size(); i = i + interval) {
            assinaturasrect.add(cells.get(i));
        }

        List<BufferedImage> assinaturas = RectangleUtils.splitImages(image, assinaturasrect);
        System.out.println("Total de Imagens: " + assinaturas.size());

        ListDisplayPanel panel = new ListDisplayPanel();
        int count = 0;

        for (BufferedImage cellimage : assinaturas) {
            long length = detectSegments(cellimage);
            panel.addImage(cellimage, count + " : " + length);
            count++;
        }

        ShowImages.showWindow(panel, "Imagens");
        
    }
    
    public static long detectSegments( BufferedImage image ) {
        
        ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
        
        // Config A:
        // DetectLineSegmentsGridRansac<ImageFloat32, ImageFloat32> detector = FactoryDetectLineAlgs.lineRansac(20, 50, 2.36, true, ImageFloat32.class, ImageFloat32.class);

        // Config B:
        DetectLineSegmentsGridRansac<ImageFloat32, ImageFloat32> detector = FactoryDetectLineAlgs.lineRansac(5, 50, 90, true, ImageFloat32.class, ImageFloat32.class);
        
        List<LineSegment2D_F32> lines = detector.detect(input);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setStroke(new BasicStroke(3));
        int scale = 1;
        long length = 0;
        for (LineSegment2D_F32 s : lines) {
            length += s.getLength();
            g2.setColor(Color.RED);
            g2.drawLine((int) (scale * s.a.x), (int) (scale * s.a.y), (int) (scale * s.b.x), (int) (scale * s.b.y));
            g2.setColor(Color.BLUE);
            g2.fillOval((int) (scale * s.a.x) - 1, (int) (scale * s.a.y) - 1, 3, 3);
            g2.fillOval((int) (scale * s.b.x) - 1, (int) (scale * s.b.y) - 1, 3, 3);
        }
        
        return length;
    }
}