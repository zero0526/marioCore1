package com.mygdx.mariobros.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.mariobros.MarioBros;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Mario;

public class Mushroom extends  Item {
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"),0,0,16,16);
        velocity=new Vector2(0.7f,0);
        //defineItem();
       // setBounds(0,0,16/MarioBros.PPM,16/MarioBros.PPM);
    }

    @Override
    public void defineItem() {
        BodyDef bdef=new BodyDef();
        bdef.type=BodyDef.BodyType.DynamicBody;
        bdef.position.set(getX(),getY() );
        body= world.createBody(bdef);

        FixtureDef fdef=new FixtureDef();
        CircleShape shape=new CircleShape();
        shape.setRadius(5/MarioBros.PPM);
        fdef.filter.categoryBits=MarioBros.ITEM_BIT;
        fdef.filter.maskBits=MarioBros.MARIO_BIT|
            MarioBros.OBJECT_BIT|
            MarioBros.GROUND_BIT|
            MarioBros.COIN_BIT|
            MarioBros.BRICK_BIT;


        fdef.shape=shape;
        body.createFixture(fdef).setUserData(this);

    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y-getHeight()/2);
        velocity.y=body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }

    @Override
    public void use(Mario mario) {
        destroy();//va cham goi destroy set destroyed=true then update function is gonna destroy
        mario.grow();
        setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y-getHeight()/2);
        body.setLinearVelocity(velocity);
    }
    public void reverseVelocity(boolean x,boolean y){
        if(x)velocity.x=-velocity.x;
        if(y)velocity.y=-velocity.y;;
    }

}
