package application.component.objects.system_object.imprement_system_onject;

import application.component.objects.DammyCollisionObject;
import application.component.objects.GameObject;
import application.component.objects.ImageManager;
import application.component.objects.character.MovableObject;
import application.component.objects.character.PlayableCharacter;
import application.component.system.GameEnvironment;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;

import java.awt.*;

public class CharacterHeart extends GameObject {
    private static final String HEART_IMAGE = "/images/heart.png";

    public static final int HEART_DEFAULT_SIZE = 10;

    private ColorAdjust screenLight = new ColorAdjust();  // ハート画像の色を変えるエフェクト

    private PlayableCharacter character;  // 体力を表示するキャラクター

    public CharacterHeart(Point pos, PlayableCharacter character) {
        super(pos);

        // 衝突物体関連
        collisionObject = new DammyCollisionObject();

        // 体力関連
        Image heart = new Image(HEART_IMAGE, HEART_DEFAULT_SIZE, HEART_DEFAULT_SIZE, true, true);;

        imageManager.addImage(ImageManager.ObjectStatus.WAIT, heart);
        imageManager.showImage(ImageManager.ObjectStatus.WAIT);
        imageManager.getImageView().setEffect(screenLight);

        this.character = character;
    }

    @Override
    public Node getImage() {
        return imageManager.getImageView();
    }

    @Override
    public void updateImage() {
        screenLight.setBrightness( character.getHp() / (double)character.DEFAULT_HP - 1);
        moveImage();
    }

    @Override
    public void beforeDelete() {
    }

    @Override
    protected void moveImage() {
        imageManager.transfer(position.x - HEART_DEFAULT_SIZE / 2, position.y - GameEnvironment.getBlockScale() / 2 - HEART_DEFAULT_SIZE);
    }
}
