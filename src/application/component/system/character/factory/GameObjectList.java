package application.component.system.character.factory;

import application.component.objects.GameObject;
import application.component.objects.character.implement_character.Hero;
import application.component.objects.character.implement_character.Monster;
import application.component.objects.stage.implement_stage.GoalBlock;
import application.component.objects.stage.implement_stage.RectangleStageBlock;

import java.awt.*;
import java.util.Optional;

/**
 * 生成されるゲームオブジェクトリスト
 */
public enum GameObjectList {
    // オブジェクト一覧
    Hero('0', pos -> { return new Hero(pos); }),
    Monster('1', pos -> { return new Monster(pos); }),
    Goal('G', pos -> { return new GoalBlock(pos); }),
    RectStage('@', pos -> { return new RectangleStageBlock(pos); });

    private char identificationChar;     // 識別用文字列

    public char getIdentificationChar() {
        return identificationChar;
    }

    private CharacterCreator createProcess;  // 生成用

    GameObjectList(char identificationChar, CharacterCreator cc) {
        this.identificationChar = identificationChar;
        this.createProcess = cc;
    }

    /**
     * 指定の識別文字列に対応するゲームオブジェクトを生成する
     *
     * @param iChar 識別文字列
     * @param pos  生成位置
     * @return game object
     */
    public static GameObject createOf(char iChar, Point pos) {
        GameObjectList gol = getOf(iChar).orElse(RectStage);
        return gol.createProcess.getInstance(pos);
    }

    /**
     * 指定の識別文字列に対応する要素を取得
     *
     * @param iChar 識別文字列
     * @return of
     */
    public static Optional<GameObjectList> getOf(char iChar) {
        for ( GameObjectList goList : GameObjectList.values() ) {
            if ( goList.equals(iChar) ) { return Optional.of(goList); }
        }

        return Optional.empty();
    }

    /**
     * 識別文字列による等価判定
     *
     * @param iChar 判別したい識別用文字列
     * @return
     */
    public boolean equals(char iChar) {
        return identificationChar == iChar;
    }

    /**
     * インスタンスの取得
     *
     * @param pos キャラクターの位置
     * @return the instance
     */
    public GameObject getInstance(Point pos) {
        return this.createProcess.getInstance(pos);
    }

    /**
     * ゲームオブジェクト生成用のSAMインタフェース
     */
    public interface CharacterCreator {
        GameObject getInstance(Point createPos);
    }
}
