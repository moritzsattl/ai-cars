package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CarBody extends Actor {

    final float C_DRAG = 3f;
    float C_ROLLING_RESISTANCE = C_DRAG * 80;
    final int CAR_MASS = 1400; // in kg
    final float C_ENGINE = 18000f * 40; // in Newton
    final float C_BREAKING = -12000f * 40; // in Newton

    private final ShapeRenderer shapeRenderer;
    private Texture carImage;
    Rectangle carFrame;
    Rectangle car;

    int carSize;

    boolean pushingAccelerator;
    boolean isBreaking;
    boolean turnRight;
    boolean turnLeft;
    float motorForce;

    float initialOrientation;
    float carOrientation;
    Vector2 acceleration = new Vector2();
    Vector2 velocity = new Vector2();



    public CarBody(int size){
        carSize = size;
        shapeRenderer = new ShapeRenderer();

        carImage = new Texture(Gdx.files.internal("simple-car.png"));
        carFrame = new Rectangle(0, 0, size/2f ,size);
        car = new Rectangle(0,0,size/2f - 2,size - 2);

        initialOrientation = 90;
        carOrientation = initialOrientation;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(new TextureRegion(carImage), carFrame.x, carFrame.y,carSize/2f,carSize);
        checkUserInput();
        drawBody(batch);
    }


    private void checkUserInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            pushingAccelerator = true;
            motorForce = C_ENGINE;
        }else{
            pushingAccelerator = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            isBreaking = true;
            motorForce = C_BREAKING;
        }else{
            isBreaking = false;
        }

        if(!pushingAccelerator && !isBreaking){
            motorForce = 0;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            turnRight = true;
        }else{
            turnRight = false;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            turnLeft = true;
        }else{
            turnLeft = false;
        }


    }

    public float speed(){
        return velocity.len();
    }

    private void drawBody(Batch batch) {
        batch.end();

        // Begin the debug rendering
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(1, 0, 1, 1); // Red color
        shapeRenderer.rect(car.x, car.y,car.width, car.height);

        shapeRenderer.setColor(1, 0, 0, 1); // Red color
        shapeRenderer.rect(carFrame.x, carFrame.y,carFrame.width, carFrame.height);

        // End the rendering
        shapeRenderer.end();
        batch.begin();
    }
}
