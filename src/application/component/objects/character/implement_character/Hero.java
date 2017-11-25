package application.component.objects.character.implement_character;

import application.component.objects.ImageManager;
import application.component.objects.RectangleCollisionObject;
import application.component.objects.character.PlayableCharacter;
import application.component.objects.system_object.imprement_system_onject.CharacterHeart;
import application.component.system.GameEnvironment;
import application.component.system.GameManager;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.awt.*;


public class Hero extends PlayableCharacter {
    private static final String WAIT_IMAGE = "/images/player/wait.png";
    private static final String LEFT_IMAGE = "/images/player/left.png";
    private static final String RIGHT_IMAGE = "/images/player/right.png";
    private static final String JUMP_IMAGE = "/images/player/jump.png";
    private static final String DEAD_IMAGE = "/images/enemy/dead.png";

    public static final int DEFAULT_SPEED = 10;
    public static final int JUMP_SPEED = -14;
    public static final int MAX_SPEED = 100;
    public static final int DEFAULT_HP = 100;

    private Point collisionRelativeDistance;

    private CharacterHeart heart;  // 体力表示

    private Group playerImage;

    public Hero(Point pos) {
        super(pos);
        this.setOnGround(false);

        hp = DEFAULT_HP;

        //===  イメージ関連
        Image waitImage = new Image(WAIT_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        Image leftImage = new Image(LEFT_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        Image rightImage = new Image(RIGHT_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        Image jumpImage = new Image(JUMP_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        Image deadImage = new Image(DEAD_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        
        imageManager.addImage(ImageManager.ObjectStatus.WAIT, waitImage);
        imageManager.addImage(ImageManager.ObjectStatus.IMG_LEFT, leftImage);
        imageManager.addImage(ImageManager.ObjectStatus.IMG_RIGHT, rightImage);
        imageManager.addImage(ImageManager.ObjectStatus.JUMP, jumpImage);
        imageManager.addImage(ImageManager.ObjectStatus.DEAD, deadImage);
        
        imageManager.showImage(ImageManager.ObjectStatus.WAIT);

        //=== 衝突物体の相対距離算出
        collisionRelativeDistance = new Point(-(int)(waitImage.getWidth() / 2), -(int)(waitImage.getHeight() / 2));

        //=== 衝突物体関連
        RectangleCollisionObject rectCO = new RectangleCollisionObject(position.x + collisionRelativeDistance.x,
                position.y + collisionRelativeDistance.y, (int)waitImage.getWidth(), (int) waitImage.getHeight());
        collisionObject = rectCO;

        moveImage();

        //=== 体力表示
        heart = new CharacterHeart(pos, this);
        GameManager.addGameObject(heart);
    }

    @Override
    public void move() {
        position.setLocation(position.x + speed.x, position.y + speed.y);
        collisionObject.transfer( position.x + collisionRelativeDistance.x, position.y + collisionRelativeDistance.y);

        heart.getPosition().setLocation(position.x, position.y);
        heart.updateImage(); // 体力画像の更新

        moveImage();
    }
    
    // ジャンプ
    public void jump() {
        // 接地している状態の時
        if (this.isOnGround() && this.getYSpeed() == GameEnvironment.getGravity()) {
            this.setSpeed(this.getXSpeed(), JUMP_SPEED);
            // 接地状態をfalseにする
            this.setOnGround(false);
        }
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
        if (this.hp <= 0) {
            imageManager.showImage(ImageManager.ObjectStatus.DEAD);
        }
    }

    @Override
    public void beforeDelete() {
        GameManager.removeGameObject(heart);
    }

    @Override
    protected void moveImage() {
        super.moveImage();
    }
}
