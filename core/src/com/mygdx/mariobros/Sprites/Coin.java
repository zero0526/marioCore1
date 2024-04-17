package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.mariobros.MarioBros;
import com.mygdx.mariobros.Scenes.Hud;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.InteractiveTileObject;
import com.mygdx.mariobros.items.ItemDef;
import com.mygdx.mariobros.items.Mushroom;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileset;
    private final int BLANK_COIN=28;
    public  Coin(PlayScreen screen, MapObject object){
        super(screen,object);
        tileset=map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCatergoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Coin","Collision");
        if(getCell().getTile().getId()==BLANK_COIN){
            MarioBros.manager.get("audio/sound/bump.wav", Sound.class).play();
        }
        else {
           if(object.getProperties().containsKey("mushroom")) {//truyen vao kiem tra xem no co chua mushroom ko
               screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
               MarioBros.manager.get("audio/sound/powerup_spawn.wav",Sound.class).play();
           }
            MarioBros.manager.get("audio/sound/coin.wav", Sound.class).play();
        }
        getCell().setTile(tileset.getTile(BLANK_COIN));
        Hud.addScore(100);
    }
}
