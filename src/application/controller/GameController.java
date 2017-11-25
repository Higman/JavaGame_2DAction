package application.controller;

import application.Main;
import application.component.system.GameManager;
import application.component.system.InputManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML protected AnchorPane root;
    @FXML protected Pane drawPane;
    @FXML protected Text timeTextField;

    protected static final GameController gc;  // GameControllerインスタンス
    protected static final Scene SCENE;
    protected GameManager gameManager;

    static {
        FXMLLoader fxmlLoader = null;
        fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("fxml/GameController.fxml"));

        try {
            fxmlLoader.load();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        Parent parent = fxmlLoader.getRoot();
        Scene scene = new Scene(parent);

        SCENE = scene;
        gc = fxmlLoader.getController();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // drawPaneにフォーカスを移す
        drawPane.setFocusTraversable(true);
        drawPane.requestFocus();

        drawPane.setEffect(ConfigController.getScreenLight());
    }

    public static GameController getInstance(int num) {
        gc.gameManager = GameManager.getInstance(gc.drawPane, num, gc.timeTextField);
        gc.gameManager.start();
        return gc;
    }

    public void show() {
        Main.primaryStage.setScene(SCENE);                 // 画面遷移を確認するために一時的に変更
    }

    /**
     * キーボードのキーが押された時の処理
     *
     * @param event the event
     */
    @FXML
    public void onKeyPressed(KeyEvent event) {
        changeKeyState(event, true);
    }

    /**
     * キーボードのキーが離された時の処理
     *
     * @param event the event
     */
    @FXML
    public void onKeyReleased(KeyEvent event) {
        changeKeyState(event, false);
    }

    @FXML
    public void pauseAction(ActionEvent event) {

    }

    /**
     * キーの状態更新メソッド
     *
     * @param event
     */
    protected void changeKeyState(KeyEvent event, boolean state) {
        switch ( event.getCode() ) {
            case UP:
                GameManager.setKeyState(InputManager.KindOfPushedKey.UP_KEY, state);
                break;
            case DOWN:
                GameManager.setKeyState(InputManager.KindOfPushedKey.DOWN_KEY, state);
                break;
            case LEFT:
                GameManager.setKeyState(InputManager.KindOfPushedKey.LEFT_KEY, state);
                break;
            case RIGHT:
                GameManager.setKeyState(InputManager.KindOfPushedKey.RIGHT_KEY, state);
                break;
            case SPACE:
                GameManager.setKeyState(InputManager.KindOfPushedKey.SPACE_KEY, state);
            default:
                break;
        }
    }

    /**
     * シーンの幅を取得
     *
     * @return the scene width
     */
    public static int getSceneWidth() {
        return (int) SCENE.getWidth();
    }

    /**
     * シーンの高さを取得
     *
     * @return the scene height
     */
    public static int getSceneHeight() {
        return (int) SCENE.getHeight();
    }
}
