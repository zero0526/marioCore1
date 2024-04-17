package com.mygdx.mariobros.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.MarioBros;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Mario;

public class Turtle extends Enemy{
    public static final int KICK_LEFT_SPEED=-2;
    public static final int KICK_RIGHT_SPEED=2;
    public enum State{WALKING,STANDING_SHELL,MOVING_SHELL,DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private boolean setToDestroy;
    private float deadRotationDegrees;
    private boolean destroyed;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames=new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),0,0,16,24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),16,0,16,24));
        shell=new TextureRegion(screen.getAtlas().findRegion("turtle"),64,0,16,24);
        walkAnimation=new Animation(0.2f,frames);
        currentState=previousState=State.WALKING;
        deadRotationDegrees=0f;
        setBounds(getX(),getY(),16/ MarioBros.PPM,24/MarioBros.PPM);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef=new BodyDef();
        bdef.type=BodyDef.BodyType.DynamicBody;
        bdef.position.set(getX(),getY() );
        b2body= world.createBody(bdef);

        FixtureDef fdef=new FixtureDef();
        CircleShape shape=new CircleShape();
        shape.setRadius(5/MarioBros.PPM);
        fdef.shape=shape;
        fdef.filter.categoryBits=MarioBros.ENEMY_BIT;//vat the
        fdef.filter.maskBits=MarioBros.GROUND_BIT |MarioBros.BRICK_BIT|MarioBros.COIN_BIT|MarioBros.ENEMY_BIT|MarioBros.OBJECT_BIT|MarioBros.MARIO_BIT;//cac vat the co the va cham

        b2body.createFixture(fdef).setUserData(this);

        //create the head dof w=enemy
        PolygonShape head =new PolygonShape();
        Vector2[] vertice=new Vector2[4];
        vertice[0]=new Vector2(-4,8).scl(1/MarioBros.PPM);
        vertice[1]=new Vector2(4,8).scl(1/MarioBros.PPM);
        vertice[2]=new Vector2(-3,2).scl(1/MarioBros.PPM);
        vertice[3]=new Vector2(3,2).scl(1/MarioBros.PPM);
        head.set(vertice);

        fdef.shape=head;
        fdef.restitution=1.5f;//do dan hoi
        fdef.filter.categoryBits=MarioBros.HEAD_ENENY_BIT;
        b2body.createFixture(fdef).setUserData(this);

    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle){
            if(((Turtle)enemy).currentState==State.MOVING_SHELL&&currentState!=State.MOVING_SHELL){//2 con rua collision
                killed();
            }
            else if( currentState==State.MOVING_SHELL&&((Turtle)enemy).currentState==State.WALKING)return;
            else reverseVelocity(true,false);
        }
        else if( currentState!=State.MOVING_SHELL){
            reverseVelocity(true,false);
        }
    }

    public TextureRegion getFrame(float dt){
        TextureRegion region;

        switch(currentState){
            case STANDING_SHELL:
            case MOVING_SHELL:
                region=shell;
                break;
            case WALKING:
            default:
                region=walkAnimation.getKeyFrame(stateTime,true);
                break;
        }
        if(velocity.x>0&& region.isFlipX()==false){
            region.flip(true,false);
        }
        if(velocity.x<0&& region.isFlipX()==true){
            region.flip(true,false);
        }
        stateTime=currentState==previousState?stateTime+dt:0;//neu trang thai truoc do trung voi trsng thai hien tai thi tang stateTimer de ham getKeyFrame lay anh tiep ko thi return ve 0
        previousState=currentState;
        return region;
    }
    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if(currentState==State.STANDING_SHELL&&stateTime>5){
            currentState=State.WALKING;
            velocity.x=1;
        }

        setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2);
        if(currentState==State.DEAD){
            deadRotationDegrees+=3;
            rotate(deadRotationDegrees);//method of interface sprite
            if(stateTime>5&&!destroyed){//
                world.destroyBody(b2body);
                destroyed=true;
            }
        }
        else
            b2body.setLinearVelocity(velocity);
    }

    @Override
    public void hitOnHead(Mario mario) {
        if(currentState!=State.STANDING_SHELL){
            currentState=State.STANDING_SHELL;
            velocity.x=0;
        }
        else kick(mario.getX()<=this.getX()?KICK_RIGHT_SPEED:KICK_LEFT_SPEED);
    }
    public void kick(int speed){//goi khi va cham voi thang dau hoac thang than
        velocity.x=speed;
        currentState=State.MOVING_SHELL;
    }
    public State getCurrentState(){
        return currentState;
    }
    public void draw(Batch batch){
        if(!destroyed){
            super.draw(batch);
        }
    }
    public void killed(){
        currentState=State.DEAD;
        Filter filter=new Filter();
        filter.maskBits=MarioBros.NOTHING_BIT;

        for(Fixture fixture: b2body.getFixtureList()){
            fixture.setFilterData(filter);
            b2body.applyLinearImpulse(new Vector2(0,5f),b2body.getWorldCenter(),true);
        }
    }
}
