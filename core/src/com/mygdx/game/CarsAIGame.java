package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CarsAIGame extends ApplicationAdapter {

	private RacingCar car;

	private Stage stage;

	private RaceCourse raceCourse;

	@Override
	public void create () {

		car = new RacingCar(350, 50, -90, 60);
		raceCourse = new RaceCourse(car);

		stage = new Stage(new FitViewport(1920,1080));
		Gdx.input.setInputProcessor(stage);

		stage.addActor(raceCourse);
		stage.addActor(car);
		stage.setKeyboardFocus(car);


	}

	public void resize (int width, int height) {
		// See below for what true means.
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);


		float delta = Math.min( Gdx.graphics.getDeltaTime(), 1/30f);
		car.update(delta);
		stage.act(delta);
		stage.draw();

		//System.out.println(car.carBody.velocity.len());
	}


	@Override
	public void dispose () {
		stage.dispose();
	}

	static public Vector2 vecFromAngle(float angle){
		return new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
	}

}
