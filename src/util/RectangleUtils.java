package util;

import georegression.struct.point.Point2D_F32;
import georegression.struct.shapes.RectangleLength2D_F32;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class RectangleUtils {

    private RectangleUtils() {
    }

    /**
     * Based on the intersection points creates rectangles (Table cells)
     * @param points - Ordered intersection points
     */
    public static List<RectangleLength2D_F32> find( List<Point2D_F32> points , int vlines , int margin ) {
        List<RectangleLength2D_F32> rectangles = new LinkedList<RectangleLength2D_F32>();

        for (int i = 0; i < points.size(); i++) {

            // next row !
            if (i > 0 && ((i + 1) % vlines) == 0) {
                continue;
            }

            // last row.
            if (i + vlines >= points.size()) break;

            RectangleLength2D_F32 rectangle = new RectangleLength2D_F32();
            Point2D_F32 left = points.get(i);
            rectangle.setX(left.x);
            rectangle.setY(left.y);

            Point2D_F32 right = points.get(i + 1);
            rectangle.setWidth(right.x - left.x);

            Point2D_F32 down_left = points.get(i + vlines);
            rectangle.setHeight(down_left.y - left.y);

            if (margin > 0) {
                rectangle.x0 = rectangle.x0 + margin;
                rectangle.y0 = rectangle.y0 + margin;
                rectangle.width = rectangle.width - (margin * 2);
                rectangle.height = rectangle.height - (margin * 2);
            }

            rectangles.add(rectangle);
        }

        return rectangles;
    }

    public static List<BufferedImage> splitImages( BufferedImage image , List<RectangleLength2D_F32> retancles ) {

        List<BufferedImage> images = new LinkedList<BufferedImage>();

        for (RectangleLength2D_F32 rect : retancles) {
            BufferedImage sub = image.getSubimage((int) rect.x0, (int) rect.y0, (int) rect.width, (int) rect.height);
            images.add(sub);
        }

        return images;
    }

}
