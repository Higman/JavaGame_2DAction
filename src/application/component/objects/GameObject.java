package application.component.objects;

import javafx.scene.Node;
import java.awt.Point;
import java.util.Optional;

public abstract class GameObject {
    public static final int DISABLE_STAY_TIME = 2000;     // 状態を無効にした後に、留まる時間(ms)

    protected Point position;
    protected CollisionObject collisionObject;
    protected ImageManager imageManager = new ImageManager();
    protected Optional<Long> lifeTime = Optional.empty();  // オブジェクトの有効期間

    public GameObject(Point pos) {
        position = pos;
    }

    public abstract Node getImage();
    
    public Point getPosition() {
        return position;
    }

    public Optional<Long> getLifeTime() {
        return lifeTime;
    }

    public CollisionObject getCollisionObject() {
        return collisionObject;
    }

    /**
     * 画像状態の更新
     */
    public abstract void updateImage();

    /**
     * 画像の移動
     */
    protected abstract void moveImage();

    /**
     * 削除時呼び出される動作
     */
    public abstract void beforeDelete();
}
