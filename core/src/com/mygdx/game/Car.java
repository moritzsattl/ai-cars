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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Car extends Actor {
    private ShapeRenderer shapeRenderer;
    private Vector2 acc = new Vector2();
    private Vector2 vel = new Vector2();

    private Texture carImage;

    private Rectangle car;

    Vector2 carCenter;
    Vector2 carHeading;
    float steerAngle;

    Vector2 tractionForce = new Vector2();
    Vector2 dragForce = new Vector2();
    Vector2 rollingResistance = new Vector2();
    Vector2 longitudinalForces = new Vector2();
    Vector2 breakingForce = new Vector2();

    private static final float STEERING_SPEED = 30f;
    private final float C_ENGINE = 6000f * 18; // in Newton

    private final float C_BREAKING = 6000f * 18; // in Newton
    private final float CAR_MASS = 1200; // in kg
    private final float C_DRAG = 1f;
    private final float C_ROLLING_RESISTANCE = C_DRAG * 30;

    private boolean pushingAccelerator;
    private boolean isBreaking;
    private boolean turnRight;
    private boolean turnLeft;


    public Car(){
        // load the images for the droplet and the bucket, 64x64 pixels each
        shapeRenderer = new ShapeRenderer();

        carImage = new Texture(Gdx.files.internal("minimalist-car.png"));


        car = new Rectangle();
        car.x = 800 / 2 - 64 / 2;
        car.y = 20;
        car.width = 64;
        car.height = 64;

        carCenter = new Vector2(car.x + car.width / 2f, car.y + car.height / 2f);
        carHeading = new Vector2(0, 1);


        setBounds(0, 0, car.width, car.height);
        addListener(new InputListener() {

            public boolean keyDown (InputEvent event, int keycode) {
                getEngineForce();
                return true;
            }

            public boolean keyUp (InputEvent event, int keycode) {
                getEngineForce();
                return true;
            }

        });

    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.draw(new TextureRegion(carImage),car.x, car.y, car.width / 2.0f, car.height / 2.0f, car.width, car.height, 1f, 1f, carHeading.angleDeg() - 90);

        // https://asawicki.info/Mirror/Car%20Physics%20for%20Games/Car%20Physics%20for%20Games.html

        // Steering
        if(turnRight) steerAngle -= STEERING_SPEED * Gdx.graphics.getDeltaTime();
        if(turnLeft) steerAngle += STEERING_SPEED * Gdx.graphics.getDeltaTime();


        System.out.println("Steering Angle: " + steerAngle);
        float steeringRadius = (float) ((car.height - 8) / Math.sin(steerAngle * MathUtils.degreesToRadians));
        System.out.println("Steering Radius: " + steeringRadius);
        debugVectors(batch,steeringRadius);

        System.out.println("Velocity:  " + vel);
        Vector2 angularVelocity = vel.cpy().scl(1/steeringRadius);
        System.out.println("AngularVelocity: " + angularVelocity);

        carHeading.setAngleDeg(steerAngle);


        // dragForce = - C_DRAG * vel * |vel|
        dragForce = vel.cpy().scl(-C_DRAG * vel.len());
        //System.out.println("Drag Force: " + dragForce);

        // rollingResistance = - C_ROLLING * v
        rollingResistance = vel.cpy().scl(-C_ROLLING_RESISTANCE);
        //System.out.println("Rolling Resistance: " + rollingResistance);


        if(isBreaking){
            // breakingForce = carHeading * -C_BREAKING,
            breakingForce = carHeading.cpy().scl(-C_BREAKING);

            // longitudinalForces =  breakingForce + dragForce + rollingResistance
            longitudinalForces = breakingForce.cpy().add(dragForce.cpy().add(rollingResistance));
        } else{
            if(pushingAccelerator){
                // tractionForce = carHeading * C_ENGINE,
                tractionForce = carHeading.cpy().scl(C_ENGINE);
            }else{
                tractionForce.set(0,0);
            }

            // longitudinalForces =  tractionForce + dragForce + rollingResistance
            longitudinalForces = tractionForce.cpy().add(dragForce.cpy().add(rollingResistance));
        }

        //System.out.println("Longitudinal Forces: " + longitudinalForces);
        // a = F (THIS MEANS ALL FORCES) / M
        // THIS SHOULD BE CHANGED
        acc = longitudinalForces.cpy().scl(1/CAR_MASS);
        //System.out.println("Accelaration" +  acc);

        // v = v + dt * a
        vel.x += acc.x * Gdx.graphics.getDeltaTime();
        vel.y += acc.y * Gdx.graphics.getDeltaTime();

        // p = p + dt * v
        car.x += vel.x * Gdx.graphics.getDeltaTime();
        car.y += vel.y * Gdx.graphics.getDeltaTime();

        carCenter.set(car.x + car.width / 2f,car.y + car.height / 2f);

    }

    private void debugVectors(Batch batch, float radius) {
        batch.end();


        // Begin the debug rendering
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if(pushingAccelerator){
            shapeRenderer.setColor(0, 1, 0, 1); // Red color
            shapeRenderer.line(carCenter.x,carCenter.y,tractionForce.x,tractionForce.y);
        }

        if(isBreaking){
            shapeRenderer.setColor(1, 0, 0, 1); // Red color
            shapeRenderer.line(carCenter.x,carCenter.y,breakingForce.x,breakingForce.y);
        }


        shapeRenderer.setColor(1, 0, 1, 1); // Red color
        shapeRenderer.line(carCenter.x,carCenter.y,longitudinalForces.x,longitudinalForces.y);


        // End the rendering
        shapeRenderer.end();
        batch.begin();
    }


    private void getEngineForce() {
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            pushingAccelerator = true;
        }else{
            pushingAccelerator = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            isBreaking = true;
        }else{
            isBreaking = false;
            isBreaking = false;
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


}
