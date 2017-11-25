package application.component.objects.stage.implement_stage;

import application.component.objects.ImageManager;
import application.component.objects.RectangleCollisionObject;
import application.component.objects.stage.StageObject;
import application.component.system.GameEnvironment;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.awt.*;

public class RectangleStageBlock extends StageObject {
    private static String WAIT_IMAGE = "/images/block/block.png";

    private Rectangle collRect;

    private Point collisionRelativeDistance;

    public RectangleStageBlock(Point pos) {
        super(pos);

        //===  イメージ関連
        Image waitImage = new Image(WAIT_IMAGE, GameEnvironment.getBlockScale(), GameEnvironment.getBlockScale(), true, true);
        imageManager.addImage(ImageManager.ObjectStatus.WAIT, waitImage);
        imageManager.showImage(ImageManager.ObjectStatus.WAIT);

        //=== 衝突物体の相対距離算出
        collisionRelativeDistance = new Point(-(int)(waitImage.getWidth() / 2), -(int)(waitImage.getHeight() / 2));

        //=== 衝突物体関連
        RectangleCollisionObject rectCO = new RectangleCollisionObject(position.x + collisionRelativeDistance.x,
                position.y + collisionRelativeDistance.y,
                (int)waitImage.getWidth(), (int)waitImage.getHeight());
        rectCO.addEvent(new StageCollision());
        collisionObject = rectCO;

        moveImage();
    }

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
//    public Node getImage() {
//        return collRect;
//    }
    
    public void updateImage() {
        
    }
}
