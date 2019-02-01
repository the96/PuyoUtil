package puyoutil;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;

public class TemplateMatching {
    private Mat img;
    int match_x = 0, match_y = 0; // debug
    int img_width, img_height;
    private int width, height;
    private static final int MATCHING_X = 600;
    private static final int MATCHING_Y = 400;
    private static final int MATCHING_WIDTH = 650;
    private static final int MATCHING_HEIGHT = 250;
    private static final Rect MATCHING_RECT = new Rect(MATCHING_X, MATCHING_Y, MATCHING_WIDTH, MATCHING_HEIGHT);

    TemplateMatching(String baseImgPath, int capWidth, int capHeight) {
        img = Imgcodecs.imread(baseImgPath, Imgcodecs.IMREAD_COLOR);
        this.width = capWidth;
        this.height = capHeight;
        double widthMagnification = (double) this.width / (double) Main.BASE_WIDTH;
        double heightMagnification = (double) this.height / (double) Main.BASE_HEIGHT;
        Size resizeSize;
        if (Main.MATCHING_RESIZE_OPTION) {
            resizeSize = new Size(img.width() * widthMagnification * Main.MATCHING_RESIZE_MAGNIFICATION, img.height() * heightMagnification * Main.MATCHING_RESIZE_MAGNIFICATION);
        } else {
            resizeSize = new Size(img.width() * widthMagnification, img.height() * heightMagnification);
        }
        img_width = (int) (resizeSize.width);
        img_height = (int) (resizeSize.height);
        Imgproc.resize(img, img, resizeSize);
    }

    boolean find(Mat target) {
        // 結果を格納する行列
        Mat result = new Mat(target.rows() - img.rows(), target.cols() - img.cols(), CvType.CV_32FC1);
        Imgproc.matchTemplate(target, img, result, Imgproc.TM_CCOEFF_NORMED);
        Imgproc.threshold(result, result, Main.THRESH, 1.0, Imgproc.THRESH_TOZERO);
        double max = Main.THRESH;
        for (int y = 0; y < result.rows(); y++) {
            for (int x = 0; x < result.cols(); x++) {
                double match = result.get(y, x)[0];
                if (max < match) {
                    match_x = x;
                    match_y = y;
                    max = match;
                }
            }
        }
        return max > Main.THRESH;
    }

    static Mat BufferedImageToMat(BufferedImage image) {
        int x, y, w, h;
        if (Main.MATCHING_RANGE_OPTIMIZE_OPTION) {
            x = MATCHING_X;
            y = MATCHING_Y;
            w = MATCHING_WIDTH;
            h = MATCHING_HEIGHT;
        } else {
            x = 0;
            y = 0;
            w = image.getWidth();
            h = image.getHeight();
        }
        int[] pixels = new int[w * h * 3];
        image.getRaster().getPixels(x, y, w, h, pixels);
        byte[] bytes = new byte[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
//            TODO: 比較元imgのRとBを入れ替えれば済む話
//            RGBがそれぞれ配列に格納されている
//            何故かRとBの順番がMatとBufferedImageで逆になってるぽいので、
//            0番目と2番目の要素を入れ替えている

//            bytes[i] = (byte) pixels[i + (i % 3 - 1) * -2];
            bytes[i] = (byte) pixels[i];
        }
        Mat mat = new Mat(h,w,CvType.CV_8UC3);
        mat.put(0,0,bytes);
        if (Main.MATCHING_RESIZE_OPTION) {
            Imgproc.resize(mat, mat,
                    new Size(mat.width() * Main.MATCHING_RESIZE_MAGNIFICATION, mat.height() * Main.MATCHING_RESIZE_MAGNIFICATION));
        }
        return mat;
    }
}