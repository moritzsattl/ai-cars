package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

public class RacingCar extends Group {

    final float wheelBase;
    Vector2 position;
    CarBody carBody;
    Wheel[] wheels;

    public RacingCar(int centerX, int centerY, float rotation, int size) {
        position = new Vector2(centerX,centerY);
        carBody = new CarBody(size);
        wheelBase = (float) (size - size * 0.2);
        wheels = new Wheel[]{new Wheel(new Vector2( 0, wheelBase /  2f),carBody, true, false), new Wheel(new Vector2(0, - wheelBase / 2f), carBody,false, true)};
        
        this.setPosition(centerX - carBody.car.width / 2f,centerY - carBody.car.height / 2f);
        this.setOrigin(carBody.car.width / 2f, carBody.car.height / 2f);
        this.setRotation(rotation);

        this.addActor(carBody);
        for (Wheel wheel: wheels) {
            this.addActor(wheel);
        }
    }

    public void update(float delta) {
        Vector2 deltaAcc = new Vector2();

        for (Wheel wheel : wheels) {
            deltaAcc.add(wheel.getLongitudinalForce());
            //System.out.println(wheel.getLongitudinalForce().len());
            deltaAcc.sub(wheel.getSideForce());
            //System.out.println(wheel.getSideForce().len());
        }

        // Calculate acceleration: a = F (sum of all forces) / M
        carBody.acceleration.add(deltaAcc).scl(1f / carBody.CAR_MASS);

        // Calculate velocity: v = v + dt * a
        carBody.velocity.x += carBody.acceleration.x * delta;
        carBody.velocity.y += carBody.acceleration.y * delta;

        // Calculate position: p = p + dt * v
        position.x += carBody.velocity.x * delta;
        position.y += carBody.velocity.y * delta;


        this.setPosition(position.x, position.y);


        // Calculate the rotation of the car based on its angular velocity
        float steeringRadius = (float) (wheelBase / Math.sin(wheels[0].rotationAngle * MathUtils.degreesToRadians));
        float angularVelocity = carBody.velocity.len() / steeringRadius;
        this.rotateBy( angularVelocity * MathUtils.radiansToDegrees * delta);

        carBody.carOrientation = this.getRotation() + carBody.initialOrientation;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void reset(int x, int y, int deg) {
        position = new Vector2(x,y);
        carBody.acceleration = new Vector2();
        carBody.velocity = new Vector2();
        this.setRotation(deg);
    }
}
