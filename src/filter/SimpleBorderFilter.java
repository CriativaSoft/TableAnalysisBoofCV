package filter;

import boofcv.alg.misc.ImageStatistics;
import boofcv.struct.image.ImageFloat32;

/**
 * The logic of this filter is: analyze the pixels at the edges of the image and check for X black pixels, 
 * if any it substitutes for white (in simplified form is this).
 * @author Ricardo JL Rufino (ricardo@criativasoft.com.br) 
 */
public class SimpleBorderFilter{
    
        private int maxBorder = 3;
        private float threshold = 10;
        private int maxIgnore = 10;
        private int replace = 255;
        
        public SimpleBorderFilter(int maxBorder, float threshold, int maxIgnore) {
            super();
            this.maxBorder = maxBorder;
            this.threshold = threshold;
            this.maxIgnore = maxIgnore;
        }

        public ImageFloat32 apply( ImageFloat32 input, ImageFloat32 output){
            double mean = ImageStatistics.mean(input);
            System.out.println("mean : " + mean);
            
            // top.
            for (int y = 0; y < maxBorder; y++) {
                if(!clearLine(input, output, y, true)) break;
            }
            
            // bottom.
            for (int y = input.getHeight() - 1; y > (input.getHeight() - maxBorder - 1); y--) {
                if(!clearLine(input, output, y, true)) break;
            }
            
            // left.
            for (int x = 0; x < maxBorder; x++) {
                if(!clearLine(input, output, x, false)) break;
            }
 
            // right
            for (int x = input.getWidth() - 1; x > (input.getWidth() - maxBorder - 1); x--) {
                if (!clearLine(input, output, x, false)) break;
            }
                   
            return output;
        } 
        
        private boolean clearLine(ImageFloat32 input, ImageFloat32 output , int current, boolean horizontal){
            int last = 0;
            int count = 0;
            boolean found = false;
            float value = 0;
            int maxLength = (horizontal ? input.getWidth() : input.getHeight());
            
            for (int i = 0; i < maxLength; i++) {
                
                if(horizontal)
                    value = input.get(i, current);
                else
                    value = input.get(current, i);
                     
                // stop for now and process segment
                if(value <= threshold){
                    count++;
                    found = true;     
                }else{
                    if(count >= maxIgnore || (i+1 == maxLength && count > 0)){
                        clear(output, last, i, current, horizontal);
                        count = 0;
                        last = i;
                    }
                    count = 0;
                    last = i;
                }
            }
            
            if(count >= maxIgnore){
                clear(output, last, maxLength, current, horizontal);
            }
            
            // System.out.println("count: " + count);
            
            return found;
        }

        private void clear( ImageFloat32 output , int start, int end , int current, boolean horizontal) {
            // System.out.println("\nClear: (" + start + "," + end + ")");
            if(horizontal){
                for (; start < end; start++) {
                    output.set(start, current, replace);
                }
            }else{
                   for (; start < end; start++) {
                    output.set(current, start, replace);
                }             
            }
        }
        
    }