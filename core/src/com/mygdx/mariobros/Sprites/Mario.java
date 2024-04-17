package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.MarioBros;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.enemies.Enemy;
import com.mygdx.mariobros.enemies.Turtle;

public class Mario extends Sprite {
    public enum State {FALLING ,JUMPING, STANDING ,RUNNING,GROWING,DEAD}
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineBigMario;
    private boolean marioIsDead;
    public Mario( PlayScreen screen){
       // super(screen.getAtlas().findRegion("little_mario"));//lay anh litlle mario  ra tu textureatlas thanh texture region
        this.world=screen.getWorld();
        currentState=State.STANDING;
        previousState=State.STANDING;

        stateTimer=0;
        runningRight=true;

        Array<TextureRegion> frames=new Array<TextureRegion>();
        for(int i=1;i<4;i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),16*i,0,16,16));
        }
        marioRun=new Animation(0.1f,frames);
        frames.clear();
        //big  mario
        for(int i=1;i<4;i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),16*i,0,16,32));
        }
        bigMarioRun=new Animation(0.1f,frames);
        frames.clear();

        //mario growing up
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        growMario=new Animation(0.2f,frames);
        //clear
        //mario jump
        marioJump=new TextureRegion(screen.getAtlas().findRegion("little_mario"),80,0,16,16);
        bigMarioJump=new TextureRegion(screen.getAtlas().findRegion("big_mario"),80,0,16,32);


        marioStand=new TextureRegion(screen.getAtlas().findRegion("little_mario"),0,0,16,16);
        //load big mario
        bigMarioStand=new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32);
        //mario dead
        marioDead=new TextureRegion(screen.getAtlas().findRegion("little_mario"),96,0,16,16);
        defineMario();
        setBounds(0,0,16/MarioBros.PPM,16/MarioBros.PPM);
       // setRegion(marioStand);
    }
    public void defineBigMario(){
        Vector2 currentPosition=b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef=new BodyDef();
        bdef.type=BodyDef.BodyType.DynamicBody;
        bdef.position.set(currentPosition.add(0,10/MarioBros.PPM));
        b2body= world.createBody(bdef);

        FixtureDef fdef=new FixtureDef();
        CircleShape shape=new CircleShape();
        shape.setRadius(5/MarioBros.PPM);
        fdef.shape=shape;
        fdef.filter.categoryBits=MarioBros.MARIO_BIT;//vat the
        fdef.filter.maskBits=MarioBros.GROUND_BIT |MarioBros.BRICK_BIT|MarioBros.COIN_BIT
                |MarioBros.OBJECT_BIT|MarioBros.ENEMY_BIT|MarioBros.HEAD_ENENY_BIT|MarioBros.ITEM_BIT;//cac vat the co the va cham

        b2body.createFixture(fdef).setUserData(this);

        shape.setPosition(new Vector2(0,-14/MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head=new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits=MarioBros.MARIO_HEAD_BIT;
        fdef.shape=head;
        fdef.isSensor=true;//dat lam cam bien .cam bien dung de xac dinh doi tuong di qua s1 dia diem nao do ma khong lam thay doi chuyen dong cua doi tuong

        b2body.createFixture(fdef).setUserData(this);//dat du lieu nguoi dung la head
        timeToDefineBigMario=false;
    }
    public void update(float dt){//attach with b2body
        if(marioIsBig){
            setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2-6/MarioBros.PPM);
        }
        else
        setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2);
        setRegion(getFrame(dt));//load frame moi len man
        if(timeToDefineBigMario){
            defineBigMario();
        }
        if(timeToRedefineBigMario){
            redefineMario();
        }
    }
    public TextureRegion getFrame(float dt){
        currentState=getState();

        TextureRegion region;
        switch(currentState) {
            case DEAD:
                region=marioDead;
                break;
            case GROWING:
                region=growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)){
                    runGrowAnimation=false;
                }
                break;
            case JUMPING:
                region = marioIsBig?bigMarioJump:marioJump;
                break;
            case RUNNING:
                region = marioIsBig?bigMarioRun.getKeyFrame(stateTimer, true):marioRun.getKeyFrame(stateTimer, true);//true de xac dinh xem loat anh co dang lap khong
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig?bigMarioStand:marioStand;
                break;
        }
        if((b2body.getLinearVelocity().x<0||!runningRight)&&!region.isFlipX()){//neu van toc am ma anh huong ve phia phai thi dao no ve phia trai
            region.flip(true,false);//dao thang  x con thang y thi kong
            runningRight=false;//chay sang ben phai r haha
        }
        else if((b2body.getLinearVelocity().x>0||runningRight)&&region.isFlipX()){
                region.flip(true,false);
                runningRight=true;
        }
        stateTimer=currentState==previousState?stateTimer+dt:0;//neu trang thai truoc do trung voi trsng thai hien tai thi tang stateTimer len khong thi rest
        previousState=currentState;
        return region;
    }
    public State getState(){
        if(marioIsDead){
            return State.DEAD;
        }
        else if(runGrowAnimation){
            return State.GROWING;
        }
        else if(b2body.getLinearVelocity().y>0||(b2body.getLinearVelocity().y<0&&previousState==State.JUMPING)){//jumpping
            return State.JUMPING;
        }
       else if(b2body.getLinearVelocity().y<0){//falling
            return State.FALLING;
        }
       else if(b2body.getLinearVelocity().x==0){
           return State.STANDING;
        }
       else return State.RUNNING;
    }
    public void grow(){
        runGrowAnimation=true;
        marioIsBig=true;
        timeToDefineBigMario=true;
        setBounds(getX(),getY(),getWidth(),getHeight()*2);
        MarioBros.manager.get("audio/sound/powerup.wav", Sound.class).play();
    }
    public  float getStateTimer(){
        return stateTimer;
    }
    public void defineMario(){
        BodyDef bdef=new BodyDef();
        bdef.type=BodyDef.BodyType.DynamicBody;
        bdef.position.set(32/ MarioBros.PPM,32/MarioBros.PPM);
        b2body= world.createBody(bdef);

        FixtureDef fdef=new FixtureDef();
        CircleShape shape=new CircleShape();
        shape.setRadius(5/MarioBros.PPM);
        fdef.shape=shape;
        fdef.filter.categoryBits=MarioBros.MARIO_BIT;//vat the
        fdef.filter.maskBits=MarioBros.GROUND_BIT |MarioBros.BRICK_BIT|MarioBros.COIN_BIT
                |MarioBros.OBJECT_BIT|MarioBros.ENEMY_BIT|MarioBros.HEAD_ENENY_BIT|MarioBros.ITEM_BIT;//cac vat the co the va cham

        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head=new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits=MarioBros.MARIO_HEAD_BIT;
        fdef.shape=head;
        fdef.isSensor=true;//dat lam cam bien .cam bien dung de xac dinh doi tuong di qua s1 dia diem nao do ma khong lam thay doi chuyen dong cua doi tuong

        b2body.createFixture(fdef).setUserData(this);//dat du lieu nguoi dung la head

    }
    public void hit(Enemy enemy){
        if(enemy instanceof Turtle &&((Turtle)enemy).getCurrentState()==Turtle.State.STANDING_SHELL){
            ((Turtle)enemy).kick(this.getX()<=enemy.getX()?Turtle.KICK_RIGHT_SPEED:Turtle.KICK_LEFT_SPEED);
        }
        else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineBigMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBros.manager.get("audio/sound/powerdown.wav", Sound.class).play();
            } else {
                MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
                MarioBros.manager.get("audio/sound/mariodie.wav", Sound.class).play();
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList()) {
                    fixture.setFilterData(filter);
                }
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), false);
            }
        }
    }
    public void redefineMario(){
        Vector2 position=b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef=new BodyDef();
        bdef.type=BodyDef.BodyType.DynamicBody;
        bdef.position.set(position);
        b2body= world.createBody(bdef);

        FixtureDef fdef=new FixtureDef();
        CircleShape shape=new CircleShape();
        shape.setRadius(5/MarioBros.PPM);
        fdef.shape=shape;
        fdef.filter.categoryBits=MarioBros.MARIO_BIT;//vat the
        fdef.filter.maskBits=MarioBros.GROUND_BIT |MarioBros.BRICK_BIT|MarioBros.COIN_BIT
                |MarioBros.OBJECT_BIT|MarioBros.ENEMY_BIT|MarioBros.HEAD_ENENY_BIT|MarioBros.ITEM_BIT;//cac vat the co the va cham

        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head=new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits=MarioBros.MARIO_HEAD_BIT;
        fdef.shape=head;
        fdef.isSensor=true;//dat lam cam bien .cam bien dung de xac dinh doi tuong di qua s1 dia diem nao do ma khong lam thay doi chuyen dong cua doi tuong

        b2body.createFixture(fdef).setUserData(this);//dat du lieu nguoi dung la head
        timeToRedefineBigMario=false;
    }
    public boolean isBig() {
        return marioIsBig;
    }
    public boolean isDead(){
        return marioIsDead;
    }
}
