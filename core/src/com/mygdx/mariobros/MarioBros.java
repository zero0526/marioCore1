package com.mygdx.mariobros;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.mariobros.Screens.PlayScreen;

public class MarioBros extends Game{//game chiu trach nhiem quan li man hinh va cung cap 1 so phuong thuc
	public  static final int V_WIDTH=400;
	public static final int V_HEIGHT=208;
	public static final float PPM=100;//pixel per meter
////mau cua bon no
	public static  final short NOTHING_BIT=0;
	public static final short GROUND_BIT =1;
	public static final short MARIO_BIT=2;
	public static final short BRICK_BIT=4;
	public static final short COIN_BIT=8;

	public static final short ENEMY_BIT =16;
	public static final short DESTROYED_BIT=64;
	public static final short OBJECT_BIT=32;
	public static final short HEAD_ENENY_BIT=128;
	public static final short 	ITEM_BIT=256;
	public static final short MARIO_HEAD_BIT=512;

	public SpriteBatch batch;//access la public vi ca project chi dung 1 batch
	//warning using assetManager in a static way can cause issues,specailly on android
	// instead you may want to pass Assetmanager to those the classes that need it
	//we will use it in the static context to save time for now
	public static AssetManager manager;
	@Override
	public void create () {//ham nay duoc goi khi chuong trinh bat dau chay
		batch = new SpriteBatch();
		manager=new AssetManager();
		manager.load("audio/music/mario_music.ogg", Music.class);
		manager.load("audio/sound/coin.wav", Sound.class);
		manager.load("audio/sound/bump.wav", Sound.class);
		manager.load("audio/sound/breakblock.wav", Sound.class);
		manager.load("audio/sound/powerup_spawn.wav",Sound.class);
		manager.load("audio/sound/powerup.wav",Sound.class);
		manager.load("audio/sound/powerdown.wav",Sound.class);
		manager.load("audio/sound/stomp.wav",Sound.class);
		manager.load("audio/sound/mariodie.wav",Sound.class);
		manager.finishLoading();

		setScreen(new PlayScreen(this));//dung de chuyen doi cac man hinh
	}

	@Override
	public void render () {//goi lien tuc 60 lan 1s
		super.render();
//		if(manager.update()){
//
//		}
	}

	@Override
	public void dispose () {//goi khi tat chuong trinh
		super.dispose();
		batch.dispose();
		manager.dispose();

	}
}
