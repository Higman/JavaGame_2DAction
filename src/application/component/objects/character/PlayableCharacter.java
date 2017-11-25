package application.component.objects.character;

import application.component.objects.CollisionEvent;
import application.component.objects.CollisionObject;
import application.component.objects.GameObject;
import application.component.system.character.controller.Player;
import application.component.system.character.factory.PlayerFactory;

import java.awt.*;
import java.util.Optional;

public abstract class PlayableCharacter extends Character implements MovableObject, OffensiveObject {

    protected Point speed = new Point();  // キャラクタの移動スピード
    public final int DEFAULT_SPEED = 10;   // 標準のスピード
    public final int DEFAULT_RANGE = 200;  // 標準索敵範囲
    public static final int DEFAULT_HP = 100;  // 標準体力
    protected boolean onGround;              // 接地しているかどうか

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public PlayableCharacter(Point pos) {
        super(pos);
        onGround = false;
    }
    
    public int getXSpeed() {
        return speed.x;
    }
    
    public int getYSpeed() {
        return speed.y;
    }

    /**
     * 画像の移動
     */
    protected void moveImage() {
        Point relDist = getCollisionRelativeDistance();
        imageManager.transfer(position.x + relDist.x, position.y + relDist.y);
    }
    
    // ジャンプ処理
    public abstract void jump();

    /**
     * キャラクターの基準位置とイメージの左上端位置の相対距離を取得
     *
     * @return
     */
    protected abstract Point getCollisionRelativeDistance();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Position: (%d, %d)\n", position.x, position.y));
        sb.append(String.format("CollisionPosition: %s\n", collisionObject.toString()));

        return new String(sb);
    }

    /**
     * 索敵範囲を取得
     *
     * @return
     */
    public int getRange() {
        return DEFAULT_RANGE;
    }

    /**
     * キャラクターの状態を無効にする
     */
    public void disable() {
        // まだ、寿命が設定されていないとき
        if ( !lifeTime.isPresent() ) {
            // このインスタンスがゲームから排除される時間の設定
            lifeTime = Optional.of(System.currentTimeMillis() + DISABLE_STAY_TIME);
        }
    }

    /**
     * キャラクターを即無効にする
     */
    public void kill() {
        lifeTime = Optional.of(0l);
    }

    /**
     * プレイヤーのキャラクターがダメージを受ける衝突イベント
     */
    protected class PlayerDamageCollision implements CollisionEvent {
        private final int damageVal;
        public PlayerDamageCollision(int damage) {
            this.damageVal = damage;
        }

        @Override
        public void ignite(CollisionObject collidedObj, GameObject gameObject, CollisionObject collidingObj) {
            Optional<Player> player = PlayerFactory.getPlayerCharacterController();
            if ( player.isPresent() && gameObject == player.get().getCharacter() ) {
                player.get().getCharacter().damage(damageVal);
            }
        }
    }
}
