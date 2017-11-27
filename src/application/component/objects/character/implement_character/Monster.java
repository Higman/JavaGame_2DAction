package application.component.objects.character.implement_character;

import application.component.objects.ImageManager;
import application.component.objects.RectangleCollisionObject;
import application.component.objects.character.PlayableCharacter;
import application.component.system.GameEnvironment;
import application.component.system.character.controller.CharacterController;
import application.component.system.character.controller.Player;
import application.component.system.character.factory.PlayerFactory;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.util.Optional;

public class Monster extends PlayableCharacter {
    private static String WAIT_IMAGE = "/images/enemy/wait.png";
    private static String LEFT_IMAGE = "/images/enemy/left.png";
    private static String RIGHT_IMAGE = "/images/enemy/right.png";
    private static String JUMP_IMAGE = "/images/enemy/enemy_move2.png";
    private static String DEAD_IMAGE = "/images/enemy/dead.png";
    public static int DEFAULT_SPEED = 10;
    public static int MAX_SPEED = 100;
    public static int DEFAULT_RANGE = 200;
    public static int DEFAULT_HP = 10;

    public static int DEFAULT_DAMAGE_VALUE = 1;

    private Point collisionRelativeDistance;

    private Circle range = new Circle(DEFAULT_RANGE);

    public Monster(Point pos) {
        super(pos);

        hp = DEFAULT_HP;

        //== イメージ関連
        Image waitImage = new Image(WAIT_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        Image leftImage = new Image(LEFT_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        Image rightImage = new Image(RIGHT_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        Image jumpImage = new Image(JUMP_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        Image deadImage = new Image(DEAD_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        
        imageManager.addImage(ImageManager.ObjectStatus.IMG_LEFT, leftImage);
        imageManager.addImage(ImageManager.ObjectStatus.IMG_RIGHT, rightImage);
        imageManager.addImage(ImageManager.ObjectStatus.JUMP, jumpImage);
        imageManager.addImage(ImageManager.ObjectStatus.DEAD, deadImage);
        imageManager.addImage(ImageManager.ObjectStatus.WAIT, waitImage);
        
        imageManager.showImage(ImageManager.ObjectStatus.WAIT);

        //= 索敵範囲関連
        range.setStroke(Color.color(0, 0, 0.8, 0.5));
        range.setFill(Color.TRANSPARENT);
        range.setCenterX(pos.x);
        range.setCenterY(pos.y);

        collisionRelativeDistance = new Point(-(int)(waitImage.getWidth() / 2), -(int)(waitImage.getHeight() / 2));

        //== 当たり判定関連
        RectangleCollisionObject rectCO = new RectangleCollisionObject(position.x + collisionRelativeDistance.x,
                position.y + collisionRelativeDistance.y, (int)waitImage.getWidth(), (int) waitImage.getHeight());
        //= 衝突イベントの追加
        rectCO.addEvent(new PlayerDamageCollision(DEFAULT_DAMAGE_VALUE));

        collisionObject = rectCO;

        moveImage();
    }

    @Override
    public void move() {
        position.setLocation(position.x + speed.x, position.y + speed.y);
        collisionObject.transfer( position.x + collisionRelativeDistance.x, position.y + collisionRelativeDistance.y);
        range.setCenterX(position.x);
        range.setCenterY(position.y);
        moveImage();
    }
    
    public void jump() {
        
    }

    @Override
    public void attack() {

    }

    @Override
    public Node getImage() {
        return imageManager.getImageView();
    }

    @Override
    public int getDefaultSpeed() {
        return DEFAULT_SPEED;
    }

    @Override
    public int getMaxSpeed() {
        return MAX_SPEED;
    }

    @Override
    public Point getSpeed() {
        return speed;
    }

    @Override
    public int getRange() {
        return Monster.DEFAULT_RANGE;
    }

    @Override
    public void beforeDelete() {

    }

    @Override
    protected Point getCollisionRelativeDistance() {
        return collisionRelativeDistance;
    }
    
 // 進行方向ごとの画像の切り替え
    public void updateImage() {
        // スピードの方向で進行方向を判断
        // y方向
        if (speed.y < 0) {
            // ジャンプ
            imageManager.showImage(ImageManager.ObjectStatus.JUMP);
        }
        
        // x方向
        if (speed.x < 0) {
            // 左方向に移動
            imageManager.showImage(ImageManager.ObjectStatus.IMG_LEFT);
        } else if (speed.x > 0) {
            // 右方向に移動
            imageManager.showImage(ImageManager.ObjectStatus.IMG_RIGHT);
        } else {
            // 待機状態
            imageManager.showImage(ImageManager.ObjectStatus.WAIT);
        }
        
        // 死んだ状態
        if (this.hp == 0) {
            imageManager.showImage(ImageManager.ObjectStatus.DEAD);
        }
    }
}
