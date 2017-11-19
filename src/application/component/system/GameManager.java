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
import application.controller.GameController;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import lib.TupleUtil;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static application.component.system.GameManager.GameProcessState.*;

/**
 * ゲームの管理クラス (シングルトン)
 */
public class GameManager {
    private static final GameManager ourInstance = new GameManager();
    private static final int PROCESS_INTERVAL_MILLISECOND = 30;           // 処理の間隔
    private static final InputManager inputManager = new InputManager();  // 入力キー管理

    private int stageNum;     // 現在選択肢ているステージ番号
    private Pane drawPane;    // 使用するパネル

    private GameProcessState gameState = GameProcessState.STOP;  // ゲームの状態
    private GameProcessTask gpt;    // ゲームプロセス
    private Timer gameTimer;        // ゲームプロセス用タイマー
    private Player player;          // プレイヤーコントローラ

    private DrawPanelManager dpm;   // パネル管理

    /**
     * インスタンスの取得
     *
     * @param drawPane the draw pane
     * @param stageNum the stage num
     * @return the instance
     */
    public static GameManager getInstance(Pane drawPane, int stageNum) {
        ourInstance.stageNum = stageNum;
        ourInstance.drawPane = drawPane;
        return ourInstance;
    }

    public static void setKeyState(InputManager.KindOfPushedKey key, boolean state) {
        inputManager.set(key, state);
    }

    public static boolean getKeyState(InputManager.KindOfPushedKey key) {
        return inputManager.get(key);
    }

    /**
     * ゲーム情報、描画画面へのゲームオブジェクトの追加
     *
     * @param go
     */
    public static void addGameObject(GameObject go) {
        ourInstance.dpm.inputGameObject(go);
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
         return Optional.of(ourInstance.player);
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
        }
    }

    /**
     * 一時停止メソッド
     */
    public void pause() {
        // ゲームが実行中であれば、停止
        if ( gameState == RUN ) {
            gameTimer.cancel();  // 停止
            gameTimer.purge();   // タスクの消去
            gameTimer = null;    // 破棄
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
        gameTimer = new Timer("game_timer", true);
        gpt = new GameProcessTask();
        gameTimer.scheduleAtFixedRate(gpt, 0, PROCESS_INTERVAL_MILLISECOND);  // 実行
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
                player = ply.get();
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
    private class GameProcessTask extends TimerTask {
        @Override
        public void run() {
            // TODO 各ゲームプロセスの実装
            //== ファクトリーの更新
            dpm.getFactoryList().stream().forEach(cf -> cf.create());

            //== キャラクターの更新
            dpm.getFactoryList().stream().forEach(cf -> cf.updateAll());

            //== 衝突オブジェクトの反映
            CollisionObject.checkCollisions(dpm.getGameMap().getGameObjects(), player.getCharacter());

            //== 移動オブジェクトの更新
            MovableObject.moveMovableObjects(dpm.getGameMap().getMovableObjects());

            //== 攻撃オブジェクトの更新
            OffensiveObject.attackOffensiveObjects(dpm.getGameMap().getOffensiveObjects());

            //== 描画パネル(drawPane)の移動
            moveDrawPanel();

            //== 無効キャラクターの削除
            //== ゲーム終了判定
        }

        /**
         * 描画パネルの移動
         */
        private void moveDrawPanel() {
            // 座標の取得と算出
            int drawPaneHalfWidth = GameController.getSceneWidth() / 2;
            int drawPaneHalfHeight = GameController.getSceneHeight() / 2;

            // プレイヤーの位置による縫製
            Point characterPos = new Point();
            getPlayerCharacterController().ifPresent(ply -> {
                characterPos.setLocation(ply.getCharacter().getPosition());
            });

            int drawPaneX =  drawPaneHalfWidth - characterPos.x;
            int drawPaneY =  drawPaneHalfHeight - characterPos.y;

            // 移動
            Platform.runLater(() -> dpm.transfer(drawPaneX, drawPaneY));
        }
    }
}
