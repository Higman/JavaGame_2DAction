package lib;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 画像に関するメソッドライブラリ
 */
public class CaluculationUtil {

    /**
     * 与えたサイズを基準となるサイズ比に補正したサイズを算出
     *
     * @param width   元の幅
     * @param height  元の高さ
     * @param referenceWidth  基準となる幅
     * @param referenceHeight 基準となる高さ
     * @return
     */
    public static TupleUtil.Tuple2<Integer, Integer> calcSize(int width, int height, int referenceWidth, int referenceHeight) {
        if ( width < height ) {
            double ratio = (double) height / referenceHeight;  // 比

            width *= ratio;
        } else {
            double ratio = (double) width / referenceWidth;  // 比

            height *= ratio;
        }

        return new TupleUtil.Tuple2<>(width, height);
    }

    public static String timeFormat(long startTime, long endTime) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        Calendar result = Calendar.getInstance();

        start.setTimeInMillis(startTime);
        end.setTimeInMillis(endTime);

        long sa = end.getTimeInMillis() - start.getTimeInMillis() - result.getTimeZone().getRawOffset();

        result.setTimeInMillis(sa);

        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");

        return sdf.format(result.getTime());
    }
}
