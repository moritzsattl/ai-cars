package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Car extends Rectangle {
   private Vector3 acc = new Vector3();
   private Vector3 vel = new Vector3();

    public Vector3 getAcc() {
        return acc;
    }

    public Vector3 getVel() {
        return vel;
    }

    public void setAcc(Vector3 acc) {
        this.acc = acc;
    }

    public void alterAcc(float inc){
        setAcc(new Vector3(getVel().x * inc, getVel().y * inc,0));
    }

    public void alterVel(Vector3 delta){
        setVel(getVel().add(delta));
    }

    public void alterPos(Vector3 delta){
        setPosition(this.x + delta.x,this.y + delta.y);
    }

    public void setVel(Vector3 vel) {
        this.vel = vel;
    }

    @Override
    public String toString() {
        return "Car{" +
                "acc=" + acc +
                ", vel=" + vel +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
