package application.component.map;

import application.component.objects.GameObject;
import application.component.system.GameEnvironment;
import application.component.system.GameManager;
import application.component.system.character.factory.GameObjectList;
import com.sun.javafx.geom.Point2D;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapインスタンスの生成ライブラリクラス
 */
public class MapFactory {
    public static final int MIN_MAP_SIZE_ROW = 3;  // マップの最小サイズ(高さ)
    public static final int MIN_MAP_SIZE_COLUMN = 3;  // マップの最小サイズ(幅)

    /**
     * マップの読み込み
     *
     * @param stageNum ステージ番号
     * @return 指定した番号のマップクラスのインスタンス
     * @throws IllegalMapDataException マップデータが不適であるとき、発生
     */
    public static GameMap createMap(int stageNum) throws IllegalMapDataException, IllegalArgumentException {
        //== 引数の判定
        if ( !checkExistMap(stageNum) ) { throw new IllegalArgumentException(); }

        //== 対応するMapInformationの取得
        MapInformation mapInfo = getMapInformation(stageNum);

        //== 読み込み
        char[][] charMapData = readMapDate(mapInfo);

        //== 環境値
        // TODO 環境値に関する処理
        //        GameEnvironment gameEnvironment

        //== Mapインスタンスの生成
        return convertToMapFromCharArray(charMapData);
    }

    /**
     * 指定した番号に対応するMapInformationの取得
     *
     * @param stageNum ステージ番号
     * @return MapInformation
     */
    private static MapInformation getMapInformation(int stageNum) {
        MapInformation mapInfo = null;
        switch ( stageNum ) {
            case 1:
                mapInfo = MapInformation.STAGE1;
        }
        return mapInfo;
    }

    /**
     * 指定したステージ番号が適正かどうか判定
     *
     * @param stageNum ステージ番号
     * @return 真偽値
     */
    public static boolean checkExistMap(int stageNum) {
        return ((0 < stageNum && stageNum <= MapInformation.values().length) ? true : false);
    }

    /**
     * 二次元文字配列からMapを生成する
     *
     * @param charMap 文字のマップデータ
     * @return GameMap
     */
    private static GameMap convertToMapFromCharArray(char[][] charMap) {
        int mapWidth = charMap[0].length * GameManager.DEFAULT_BLOCK_SCALE;  // マップの幅
        int mapHeight = charMap.length * GameManager.DEFAULT_BLOCK_SCALE;    // マップの高さ
        GameMap gameMap = new GameMap(mapWidth, mapHeight);

        for ( int ver = 0; ver < charMap.length; ver++ ) {
            for ( int hol = 0; hol < charMap[0].length; hol++ ) {
                char ch = charMap[ver][hol];
                final int fVer = ver;
                final int fHol = hol;
                GameObjectList.getOf(ch).ifPresent(gol -> {
                    System.out.print(gol.getIdentificationString());
                    Point2D pos = new Point2D(fHol * GameManager.DEFAULT_BLOCK_SCALE, fVer * GameManager.DEFAULT_BLOCK_SCALE);
                    gameMap.addGameObject(gol.getInstance(pos));
                });
            }
            System.out.println();
        }

        return gameMap;
    }

    /**
     * 指定のリソースから読み込んだMapデータを持つ配列を生成
     *
     * @param mapInfo リソースURLを持つMap情報
     * @return Mapデータを持つ配列
     * @throws IllegalMapDataException
     */
    private static char[][] readMapDate(MapInformation mapInfo) throws IllegalMapDataException {
        char[][] mapData;
        String resource = mapInfo.getURL().getPath();  // リソース位置

        List<String> inputString = new ArrayList();

        //== 読み込み準備
        inputString = getFileString(resource);

        //== 生成マップの情報作成
        // 縦横サイズの算出
        int mapRow = inputString.size();
        int mapCol = inputString.stream().min((a, b) -> Integer.compare(a.length(), b.length())).orElse("").length();
        // 指定のMapのサイズが適正か判定
        if ( mapRow <= MIN_MAP_SIZE_ROW || mapCol <= MIN_MAP_SIZE_COLUMN ) { throw new IllegalMapDataException(); }

        // マップの読み込み
        mapData = cutOutToCharArrayFromStringArray(inputString, mapRow, mapCol);

        return mapData;
    }

    /**
     * 文字列配列から指定矩形部分を文字配列として切り出す
     *
     * @param inputString 切り出し元の文字列配列
     * @param mapRow      切り出す行数
     * @param mapCol      切り出す列数
     * @return 切り出された二次元文字配列
     */
    private static char[][] cutOutToCharArrayFromStringArray(List<String> inputString, int mapRow, int mapCol) {
        char[][] mapData;

        mapData = new char[mapRow][mapCol];
        int rowIndex = 0;
        for ( String str : inputString ) {
            int columnIndex = 0;
            for ( char chr : str.toCharArray() ) {
                mapData[rowIndex][columnIndex++] = chr;
            }
            rowIndex++;
        }

        return mapData;
    }

    /**
     * 指定URLのファイルの読み込み
     *
     * @param resource 読み込むファイルのURL
     * @return 文字列データ
     */
    private static List<String> getFileString(String resource) {
        //== ファイルの準備
        FileReader fReader = null;
        try {
            fReader = new FileReader(resource);
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }

        BufferedReader bReader = new BufferedReader(fReader);

        //== ファイル情報の読み込み
        List<String> inputString = bReader.lines().collect(Collectors.toList());
        return inputString;
    }

    /**
     * マップデータが不適切なときの例外
     */
    public static class IllegalMapDataException extends Exception {
        private static final long serialVersionUID = 1L;

        public IllegalMapDataException() {
            System.out.println("不適切なMapデータです");
        }

        public IllegalMapDataException(String message) {
            super(message);
        }
    }
}
