package application.component.system;

import application.component.map.GameMap;
import application.component.map.MapFactory;
import application.component.map.MapFactory.IllegalMapDataException;
import application.component.objects.CollisionObject;
import application.component.objects.GameObject;
import application.component.objects.character.MovableObject;
import application.component.objects.character.OffensiveObject;
import application.component.system.character.controller.Player;
import application.component.system.character.factory.CharacterFactory;
import application.component.system.character.factory.PlayerFactory;
import application.controller.TitleController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lib.GameTimer;
import lib.TupleUtil;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static application.component.system.GameManager.GameProcessState.*;

/**
 * ゲームの管理クラス (シングルトン)
 */
public class GameManager {
    private static final GameManager ourInstance = new GameManager();
    private static final int PROCESS_INTERVAL_MILLISECOND = 35;           // 処理の間隔

    private int stageNum;     // 現在選択肢ているステージ番号
    private Pane drawPane;    // 使用するパネル

    private GameProcessState gameState = GameProcessState.STOP;  // ゲームの状態
    private Timeline gameTimeline;        // ゲームプロセス用タイマー
    private Player player;          // プレイヤーコントローラ

    private DrawPanelManager dpm;       // パネル管理
    private InputManager inputManager;  // 入力キー管理

    private Text timeTextField;  // 時間表示用
    private GameTimer gameTimer;  // 計測用のタイマー

    /**
     * インスタンスの取得
     *
     * @param drawPane the draw pane
     * @param stageNum the stage num
     * @return the instance
     */
    public static GameManager getInstance(Pane drawPane, int stageNum, Text timeTextField) {
        ourInstance.stageNum = stageNum;
        ourInstance.drawPane = drawPane;
        ourInstance.inputManager = new InputManager();
        ourInstance.timeTextField = timeTextField;
        ourInstance.gameTimer = new GameTimer();

        return ourInstance;
    }

    public static void setKeyState(InputManager.KindOfPushedKey key, boolean state) {
        ourInstance.inputManager.set(key, state);
    }

    public static boolean getKeyState(InputManager.KindOfPushedKey key) {
        return ourInstance.inputManager.get(key);
    }

    /**
     * ゴール処理リクエストメソッド
     */
    public static void requestGoal() {
        ourInstance.stop();  // 処理の停止
        TitleController.getInstance().show();
    }

    /**
     * 操作キャラクターの登録
     *
     * @param player
     */
    public static void registerPlayer(Player player) {
        ourInstance.player = player;
    }

    /**
     * ゲーム情報、描画画面へのゲームオブジェクトの追加
     *
     * @param go
     */
    public static void addGameObject(GameObject go) {
        ourInstance.dpm.inputGameObject(go);
    }

    public static void removeGameObject(GameObject go) {
        go.beforeDelete();
        ourInstance.dpm.removeGameObject(go);
    }

    /**
     * ゲームオブジェクトが有効範囲にあるか判定
     *
     * @param go
     * @return
     */
    public static boolean isValid(GameObject go) {
        return ourInstance.dpm.checkBeingShown(go);
    }

    public static boolean isValid(Point pos) {
        return ourInstance.dpm.checkBeingShown(pos);
    }

    /**
     * プレーヤーコントローラの取得
     */
    public static Optional<Player> getPlayerCharacterController() {
         return Optional.ofNullable(ourInstance.player);
    }

    /**
     * 開始メソッド
     */
    public void start() {
        // 実行中でなければ、開始
        if ( gameState != RUN ) {
            try {
                exec();             // ゲーム実行
            } catch ( NotGameProcessException e ) {
                e.printStackTrace();
            }

            // タイマーの計測開始
            gameTimer.start();
        }
    }

    /**
     * 一時停止メソッド
     */
    public void pause() {
        // ゲームが実行中であれば、停止
        if ( gameState == RUN ) {
            gameTimer.pause();    // タイマーの一時停止
            gameTimeline.stop();  // 停止
            gameTimeline = null;    // 破棄
            gameState = PAUSE;   // 状態の更新
        }
    }

    /**
     * 停止メソッド
     */
    public void stop() {
        pause();  // 停止
        gameState = STOP;  // 状態の更新
    }

    public Pane getDrawPane() {
        return drawPane;
    }

    /**
     * 実行メソッド
     */
    private void exec() throws NotGameProcessException {
        // ゲーム状態の新規作成判定
        if ( gameState == STOP ) {
            initializeGameComponent(stageNum);  // 初期化
        }

        // 実行
        gameTimeline = new Timeline(new KeyFrame(Duration.millis(PROCESS_INTERVAL_MILLISECOND), new GameProcessTask()));
        gameTimeline.setCycleCount(Timeline.INDEFINITE);
        gameTimeline.play();

        gameState = RUN;                    // 状態の更新
    }

    /**
     * ゲーム内要素の初期化
     */
    private void initializeGameComponent(int stageNum) {
        TupleUtil.Tuple3<GameMap, GameEnvironment, List<CharacterFactory>> gameInfo = null;
        try {
            gameInfo = MapFactory.createMap(stageNum);
            inputObjectsToPane(gameInfo._1, gameInfo._2, gameInfo._3);    // 描画パネル関連の初期化
        } catch ( IllegalMapDataException | IllegalArgumentException | NotExistPlayerException e ) {
            e.printStackTrace();
            // TODO 例外が発生した場合のデフォルトデータを用意する。仮マップは、GameFactoryクラスかGameMapクラスのクラスメソッドから取得する
        }
    }

    /**
     * GameMapクラスのインスタンスが持つGaneObjectoのインスタンスのImageViewをdrawPaneに登録する
     */
    private void inputObjectsToPane(GameMap gm, GameEnvironment ge, List<CharacterFactory> factoryList) throws NotExistPlayerException {
        dpm = new DrawPanelManager(gm, factoryList, drawPane);

        //== プレイヤーコントローラの有無のチェックと取得
        for ( CharacterFactory cf : factoryList ) {
            if ( cf instanceof PlayerFactory ) {
                Optional<Player> ply = cf.create();
            }
        }
        //= プレイヤーがマップ上に存在しないとき
        if ( player == null ) {
            throw new NotExistPlayerException();
        }
    }

    /**
     * ゲームの起動状況判別用列挙体
     */
    public enum GameProcessState {
        RUN, STOP, PAUSE
    }

    /**
     * GameProcessクラスのインスタンスが用意されていないとき発生する例外
     */
    private class NotGameProcessException extends Exception {
        public NotGameProcessException() {
            super();
        }
    }

    /**
     * プレイヤーコントローラが存在しないとき発生する例外
     */
    private class NotExistPlayerException extends Exception {

    }

    /**
     * ゲーム部クラス
     */
    private class GameProcessTask implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            //== ゲームUIの更新
            timeTextField.setText(gameTimer.getTimeString());

            //== 有効範囲の更新
            moveRangeOfActivities();

            //== キャラクターの更新
            dpm.getFactoryList().stream().forEach(cf -> cf.updateAll());

            //== 衝突オブジェクトの反映
            reflectCollisions();

            //== 移動オブジェクトの更新
            MovableObject.moveMovableObjects(dpm.getGameMap().getMovableObjects());

            //== 攻撃オブジェクトの更新
            OffensiveObject.attackOffensiveObjects(dpm.getGameMap().getOffensiveObjects());

            //== ファクトリーの更新
            dpm.getFactoryList().stream().forEach(cf -> cf.create());

            //== 描画パネル(drawPane)の移動
            moveDrawPanel();

            //== 無効キャラクターの削除
            dpm.getFactoryList().forEach(fac -> fac.checkLifeTile());
        }

        /**
         * 全ての衝突物体の反映
         */
        private void reflectCollisions() {
            List<GameObject> gol = dpm.getGameMap().getGameObjects();
            for ( int k = 0; k < gol.size(); k++ ) {
                if ( !isValid(gol.get(k)) ) { continue; }
                CollisionObject.checkCollisions(dpm.getGameMap().getGameObjects(), gol.get(k));
            }
        }

        /**
         * 有効範囲の移動
         */
        private void moveRangeOfActivities(){
            // プレイヤーの位置の取得
            Point characterPos = new Point();
            if ( getPlayerCharacterController().isPresent() ) {
                characterPos.setLocation(getPlayerCharacterController().get().getCharacter().getPosition());
            }

            dpm.focusPointForRangeOfActivities(characterPos);
        }

        /**
         * 描画パネルの移動
         */
        private long preUpdateTime = 0;
        private final long UPDATE_INTERVAL = 500;
        private void moveDrawPanel() {
            // プレイヤーの位置の取得
            Point characterPos = new Point();
            if ( getPlayerCharacterController().isPresent() ) {
                characterPos.setLocation(getPlayerCharacterController().get().getCharacter().getPosition());
            }

            // 移動
            dpm.focusPoint(characterPos);
        }
    }
}
