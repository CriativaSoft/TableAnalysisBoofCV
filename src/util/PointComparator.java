package util;

import georegression.struct.point.Point2D_F32;

import java.util.Comparator;

public class PointComparator implements Comparator<Point2D_F32> {

        int minYProx = 0;

        public PointComparator() {
        }

        /**
         * @param minYProx - Increasing this value helps in prdenação if the points are misaligned horizontally
         */
        public PointComparator(int minYProx) {
            this.minYProx = minYProx;
        }

        @Override
        public int compare( Point2D_F32 p1 , Point2D_F32 p2 ) {
            boolean eq = Math.abs(p1.getY() - p2.getY()) <= minYProx;
            if (p1.getY() < p2.getY() && !eq) return -1;
            if (p1.getY() > p2.getY() && !eq) return 1;
            if (p1.getX() < p2.getX()) return -1;
            if (p1.getX() > p2.getX()) return 1;
            return 0;
        }
    }