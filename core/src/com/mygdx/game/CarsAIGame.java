package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import sun.tools.jconsole.JConsole;

public class CarsAIGame extends ApplicationAdapter {

	private final float ACCELERATION_FACTOR = 20f;
	private Texture carImage;
	private Car car;

	private OrthographicCamera camera;
	private SpriteBatch batch;


	private float lastUpdateTime = 0;


	@Override
	public void create () {
		// load the images for the droplet and the bucket, 64x64 pixels each
		carImage = new Texture(Gdx.files.internal("minimalist-car.png"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		batch = new SpriteBatch();

		car = new Car();
		car.x = 800 / 2 - 64 / 2;
		car.y = 20;
		car.width = 64;
		car.height = 64;


	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();


		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(carImage, car.x, car.y,64,64);
		batch.end();

		checkKeyPressed();





		if(TimeUtils.nanoTime() - lastUpdateTime > 1000000000){
			lastUpdateTime = TimeUtils.nanoTime();

			car.alterPos(car.getVel());
			car.alterVel(car.getAcc());
			//car.alterAcc(.99f);
			System.out.println(car);
		}

		if(car.x < 0) car.x = 0;
		if(car.x > 800 - 64) car.x = 800 - 64;

	}

	private void checkKeyPressed() {
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			System.out.println("Key UP pressed");
			car.getAcc().y -= ACCELERATION_FACTOR;
		}

	}

	@Override
	public void dispose () {
		carImage.dispose();
		batch.dispose();
	}

}
