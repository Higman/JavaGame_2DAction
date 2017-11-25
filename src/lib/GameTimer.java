package lib;

/**
 * ゲーム用のタイマー
 */
public class GameTimer {
    private long startTime = -1l;
    private long pauseStartTimer = 0l;

    /**
     * 初期化
     */
    public void init() {
        startTime = -1l;
        pauseStartTimer = 0l;
    }

    /**
     * 計測開始
     */
    public void start() {
        // タイマーが初期値のとき
        if ( startTime < 0 ) {
            startTime = System.currentTimeMillis();
        }

        // ポーズ開示時間が1以上のとき
        if ( pauseStartTimer > 0 ) {
            startTime += System.currentTimeMillis() - pauseStartTimer;
            pauseStartTimer = 0l;  // 初期化
        }
    }

    /**
     * ポーズ
     */
    public void pause() {
        pauseStartTimer = System.currentTimeMillis();
    }

    /**
     * 時間の取得
     */
    public String getTimeString() {
        return CaluculationUtil.timeFormat(startTime, System.currentTimeMillis());
    }
}
