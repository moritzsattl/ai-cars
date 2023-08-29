package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.mygdx.game.CarsAIGame.vecFromAngle;

public class Wheel extends Actor {

    private static final float STEERING_SPEED = 85f;
    private static final float MAX_STEERING_ANGLE = 120;

    // This is just important for tire grip and does not limit car's speed
    private static final float  MAX_SPEED = 305;

    private ShapeRenderer shapeRenderer;
    private Vector2 posFromCenter;
    private CarBody racingCar;
    Rectangle box;

    float rotationAngle; // -MAX_STEERING_ANGLE/2 <= 0 <= MAX_STEERING_ANGLE/2
    private boolean isSteerable;
    private boolean isDriveable;

    private final float width = 10;
    private final float height = 20;

    private Vector2 longitudinalForce = new Vector2();
    private Vector2 sideForce = new Vector2();
    private Vector2 tractionForce = new Vector2();



    public Wheel(Vector2 posFromCenter, CarBody carBody, boolean isSteerable, boolean isDriveable) {
        this.isSteerable = isSteerable;
        this.isDriveable = isDriveable;
        this.racingCar = carBody;
        this.posFromCenter = posFromCenter;
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        update();
        //debugDrawing(batch);
    }

    private void debugDrawing(Batch batch) {
        this.box = new Rectangle(racingCar.car.width / 2f + posFromCenter.x - width / 2f,racingCar.car.height / 2f + posFromCenter.y - height / 2f, width, height);
        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        drawWheel();
        drawForces();

        // End the rendering
        shapeRenderer.end();
        batch.begin();
    }

    private void drawForces() {

        Vector2 wheelCenter = new Vector2(this.box.x + box.width / 2f, this.box.y + box.height / 2f);

        // This angle is added due to the fact that the whole CarBody is rotated, so to draw the real direction of the whole, it must be subtracted
        float addedAngleInRad = (float) (racingCar.carOrientation * MathUtils.degreesToRadians);

        float initialOrientationRad = racingCar.initialOrientation * MathUtils.degreesToRadians;

        // traction force
        /*
        Vector2 tempTractionForce = new Vector2((float) Math.cos(this.rotationAngle * MathUtils.degreesToRadians + Math.PI/2f), (float) Math.sin(this.rotationAngle * MathUtils.degreesToRadians + Math.PI/2f) );
        if(isDriveable) {
            tempTractionForce = tempTractionForce.scl(racingCar.motorForce * scaleForces);
        }

        shapeRenderer.setColor(0,1,0,1);
        shapeRenderer.line(wheelCenter,tempTractionForce.add(wheelCenter));
        */

        // drag force
        /*
        Vector2 tempDragForceAngle = new Vector2((float) Math.cos(racingCar.velocity.angleRad() - racingCar.carOrientation * MathUtils.degreesToRadians - Math.PI/2f), (float) Math.sin(racingCar.velocity.angleRad() - racingCar.carOrientation * MathUtils.degreesToRadians - Math.PI/2f));
        tempDragForceAngle.scl(-racingCar.C_DRAG * racingCar.speed() * racingCar.speed() * scaleForces);

        shapeRenderer.setColor(1f,0,0,1);
        shapeRenderer.line(wheelCenter,tempDragForceAngle.add(wheelCenter));

        // rolling resistance
        Vector2 tempRollingResistanceAngle = new Vector2((float) Math.cos(racingCar.velocity.angleRad() - racingCar.carOrientation * MathUtils.degreesToRadians - Math.PI/2f), (float) Math.sin(racingCar.velocity.angleRad() - racingCar.carOrientation * MathUtils.degreesToRadians - Math.PI/2f));
        tempRollingResistanceAngle.scl(-racingCar.C_ROLLING_RESISTANCE * racingCar.speed() * scaleForces);

        shapeRenderer.setColor(0.5f,0,0.5f,1);
        shapeRenderer.line(wheelCenter,tempRollingResistanceAngle.add(wheelCenter));
        */

        // longitudinal force

        /*
        Vector2 tempLongitudinalForce = new Vector2((float) Math.cos(rotationAngle * MathUtils.degreesToRadians + Math.PI/2f), (float) Math.sin(rotationAngle * MathUtils.degreesToRadians + Math.PI/2f));
        tempLongitudinalForce.scl(longitudinalForce.len() * scaleForces);
        shapeRenderer.setColor(0,0,1,1);
        shapeRenderer.line(wheelCenter,tempLongitudinalForce.add(wheelCenter));
        */

        /*
        // Velocity
        Vector2 velTemp = vecFromAngle(racingCar.velocity.angleRad() - addedAngleInRad + initialOrientationRad);
        velTemp.scl(racingCar.velocity.len());

        shapeRenderer.setColor(1,0,0,1);
        shapeRenderer.line(wheelCenter,velTemp.add(wheelCenter));


        Vector2 velSideForce = vecFromAngle(sideForce.angleRad() - addedAngleInRad + initialOrientationRad);
        velSideForce.scl(sideForce.len());
        shapeRenderer.setColor(0,0,1,1);
        shapeRenderer.line(wheelCenter,velSideForce.add(wheelCenter));


        */
    }

    private void update() {
        if(isSteerable) steering(racingCar, Gdx.graphics.getDeltaTime());
        longitudinalForce();
        sideForce();
    }

    private void longitudinalForce() {
        // Converting angle to vector
        Vector2 rotationAngle = vecFromAngle(this.rotationAngle * MathUtils.degreesToRadians + racingCar.carOrientation * MathUtils.degreesToRadians);

        tractionForce = new Vector2();
        if(isDriveable) {
            tractionForce = rotationAngle.scl(racingCar.motorForce);
        }

        // dragForce = - C_DRAG * vel * |vel|
        Vector2 dragForce = racingCar.velocity.cpy().scl(-racingCar.C_DRAG * racingCar.speed());

        // rollingResistance = - C_ROLLING * v
        Vector2 rollingResistance = racingCar.velocity.cpy().scl(-racingCar.C_ROLLING_RESISTANCE);

        // longitudinalForce =  tractionForce/breakingForce + dragForce + rollingResistance
        longitudinalForce = tractionForce.add(dragForce).add(rollingResistance);
    }

    private void sideForce(){
        // Project velocity vector on to wheel side

        // This is the equivalent to a, because a is the derivative of v
        Vector2 u = racingCar.velocity.cpy();

        Vector2 s = vecFromAngle((float) (racingCar.carOrientation * MathUtils.degreesToRadians + (Math.PI / 2f)));

        sideForce = s.scl(s.dot(u)/s.len2());


        // Lookup table for tire grip
        float tireGripPercentage = tireGripPercentage(racingCar.velocity.len());
        // F = a * m
        sideForce.scl(racingCar.CAR_MASS).scl(tireGripPercentage);
    }

    private float tireGripPercentage(float cSpeed) {
        float per = cSpeed/MAX_SPEED;
        if (per < 0.0f) {
            return 1.0f; // Ensure output is within the range 0.0 - 1.0
        } else if (per > 1.0f) {
            return 0.0f; // Ensure output is within the range 0.0 - 1.0
        }else {
            float tireSlipPercentage;
            if(isSteerable){
                tireSlipPercentage = 1.0f - (per * per * per) + 0.5f;
            }else{
                tireSlipPercentage = 1.0f - (per * per * per) + 0.3f;
            }

            return Math.min(tireSlipPercentage, 1.0f);
        }
    }

    private void steering(CarBody racingCar, double deltaTime) {
        if(racingCar.turnLeft){
            if(rotationAngle < MAX_STEERING_ANGLE / 2f) rotationAngle += STEERING_SPEED * deltaTime;
        }

        if(racingCar.turnRight){
            if(rotationAngle > -MAX_STEERING_ANGLE / 2f) rotationAngle -= STEERING_SPEED * deltaTime;
        }

        if(!racingCar.turnLeft && !racingCar.turnRight){
            if(rotationAngle > 0){
                rotationAngle -= STEERING_SPEED * deltaTime;
            }else if(rotationAngle < 0){
                rotationAngle += STEERING_SPEED * deltaTime;
            }
            if(rotationAngle < 10 & rotationAngle > -10){
                rotationAngle = 0;
            }
        }
    }

    public Vector2 getLongitudinalForce() {
        return longitudinalForce;
    }

    public Vector2 getSideForce(){
        return sideForce;
    }

    private void drawWheel() {
        shapeRenderer.setColor(1, 0, 1, 1); // Red color
        shapeRenderer.rect(box.x, box.y,box.width / 2f, box.height / 2f, box.width, box.height,1f,1f, rotationAngle);
    }

    public static float angleBetween(Vector2 a, Vector2 b){
        return (float) Math.acos(a.dot(b)/a.len()*b.len());
    }
}
