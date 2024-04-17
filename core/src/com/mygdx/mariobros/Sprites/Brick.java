package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mariobros.MarioBros;
import com.mygdx.mariobros.Scenes.Hud;
import com.mygdx.mariobros.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {
    public  Brick(PlayScreen screen, MapObject object){
        super(screen ,object);
        fixture.setUserData(this);//dung trong class worldContactListener
        setCatergoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Brick","Collision");
        if(mario.isBig()) {
            setCatergoryFilter(MarioBros.ENEMY_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MarioBros.manager.get("audio/sound/breakblock.wav", Sound.class).play();
        }
        MarioBros.manager.get("audio/sound/bump.wav", Sound.class).play();
    }
}
