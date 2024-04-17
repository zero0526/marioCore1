package com.mygdx.mariobros.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.mariobros.Screens.PlayScreen;


public class fix extends Sprite {
    public TextureRegion tr;
    public fix(PlayScreen screen){
        super(screen.getAtlas().findRegion("mushroom"));
        tr=new TextureRegion(getTexture(),0,0,16,16);
    }
}
