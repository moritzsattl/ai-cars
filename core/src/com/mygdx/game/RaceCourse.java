package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class RaceCourse extends Actor {

    public static Texture backgroundTexture;

    private Pixmap pixmap;

    private ShapeRenderer shapeRenderer;

    private RacingCar car;
    public RaceCourse(RacingCar racingCar) {
        this.car = racingCar;
        backgroundTexture = new Texture(Gdx.files.internal("race-course.png"));

        TextureData textureData = backgroundTexture.getTextureData();
        if (!textureData.isPrepared()) {
            textureData.prepare();
        }


        pixmap = textureData.consumePixmap();

        shapeRenderer = new ShapeRenderer();
    }

    public void renderBackground(Batch batch) {

        batch.draw(backgroundTexture,0,0);

        Color centerColor = new Color(pixmap.getPixel((int) (car.getX() + car.carBody.car.getWidth()/2f), (int) (backgroundTexture.getHeight() - car.getY() - car.carBody.car.getHeight()/2f )));

        System.out.println(centerColor.toString());
        // Check if car center is on grass
        if(centerColor.toString().equals("34ff93ff")){
            System.out.println("On Grass");
            car.carBody.C_ROLLING_RESISTANCE = car.carBody.C_DRAG * 1000;
        }else if(centerColor.toString().equals("836767ff") || centerColor.toString().equals("00000000")){
            System.out.println("Bumping in to wall");
            car.reset(250,120,-90);
        }else{
            car.carBody.C_ROLLING_RESISTANCE = car.carBody.C_DRAG * 80;
        }

        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(centerColor);
        shapeRenderer.rect(20,20,20,20);


        //shapeRenderer.setColor(Color.BLACK);
        //shapeRenderer.line(0,0,car.getX() + car.carBody.car.getWidth()/2f,car.getY() + car.carBody.car.getHeight()/2f);

        // End the rendering
        shapeRenderer.end();
        batch.begin();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        renderBackground(batch);
        super.draw(batch, parentAlpha);
    }
}
