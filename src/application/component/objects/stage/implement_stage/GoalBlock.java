package application.component.objects.stage.implement_stage;

import application.component.objects.ImageManager;
import application.component.objects.RectangleCollisionObject;
import application.component.objects.stage.StageObject;
import application.component.system.GameEnvironment;
import application.component.system.GameManager;
import application.component.system.character.factory.PlayerFactory;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.awt.*;

public class GoalBlock extends StageObject {
    private static final String WAIT_IMAGE = "/images/goal.png";
    private static final int COLLISION_MARGIN = 8;  // 当たり判定補正用

    public GoalBlock(Point pos) {
        super(pos);

        //=== イメージ関連
        Image waitImage = new Image(WAIT_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        imageManager.addImage(ImageManager.ObjectStatus.WAIT, waitImage);
        imageManager.showImage(ImageManager.ObjectStatus.WAIT);

        //=== 衝突物体関連
        int width = (int) waitImage.getWidth() - COLLISION_MARGIN * 2;
        int height = (int) waitImage.getHeight();
        RectangleCollisionObject rectCO = new RectangleCollisionObject(position.x - width / 2,
                position.y - height / 2, width, height);

        //= 衝突イベント関連
        // ゴールリクエストの発火イベント
        rectCO.addEvent((ed, go, ing) -> {
            PlayerFactory.getPlayerCharacterController().ifPresent(player -> {
                if ( go == player.getCharacter() ) {
                    GameManager.requestGoal();
                }
            });
        });

        collisionObject = rectCO;  // 登録

        moveImage();
    }

    @Override
    protected void moveImage() {
        int posX = (int)(position.x - imageManager.getImageView().getImage().getWidth()/2);
        int posY = (int)(position.y - imageManager.getImageView().getImage().getHeight()/2);

        imageManager.transfer(posX, posY);
    }

    @Override
    public void beforeDelete() {

    }

    @Override
    public Node getImage() {
        return imageManager.getImageView();
    }
    
    public void updateImage() {
        
    }
}
