package com.mygdx.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mariobros.MarioBros;
import com.mygdx.mariobros.Scenes.Hud;
import com.mygdx.mariobros.enemies.Enemy;
import com.mygdx.mariobros.Sprites.Mario;
import com.mygdx.mariobros.Tools.B2WorldCreator;
import com.mygdx.mariobros.Tools.WorldContactListener;
import com.mygdx.mariobros.items.Item;
import com.mygdx.mariobros.items.ItemDef;
import com.mygdx.mariobros.items.Mushroom;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    private MarioBros game;
    private TextureAtlas atlas;
    public static boolean alreadyDestroyed=false;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //box 2d
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprite
    private Mario player;
    private Music music;
   // private Goomba goomba;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;//use linkBlockingQueue because it can deal with theads

    public PlayScreen(MarioBros game){
        atlas=new TextureAtlas("animation/Mario_And_Enemies.pack");

        this.game=game;
        //create a fitViewport to maintain vortual aspect ratio
        gamecam=new OrthographicCamera();
        gamePort=new FitViewport(MarioBros.V_WIDTH/MarioBros.PPM,MarioBros.V_HEIGHT/MarioBros.PPM,gamecam);
         hud=new Hud(game.batch);
//load map
         maploader=new TmxMapLoader();
         map=maploader.load("maps/level1.tmx") ;
        renderer=new OrthogonalTiledMapRenderer(map,1/MarioBros.PPM);
        //set posiotion cho cam
        gamecam.position.set(MarioBros.V_WIDTH/2/MarioBros.PPM,MarioBros.V_HEIGHT/2/MarioBros.PPM,0);

        //set up world
        world=new World(new Vector2(0,-10),true);
        b2dr=new Box2DDebugRenderer();


        BodyDef bdef=new BodyDef();
        PolygonShape shape=new PolygonShape();
        FixtureDef fdef=new FixtureDef();
        Body body;
        //creato box
        creator=new B2WorldCreator(this);

        player=new Mario(this);


        world.setContactListener(new WorldContactListener());

        music=MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        //music.setLooping(true);
        //music.play();
        //goomba=new Goomba(this,5.64f,.64f);

        items=new Array<Item>();
        itemsToSpawn= new LinkedBlockingQueue<ItemDef>();
    }
    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }
    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef=itemsToSpawn.poll();//like pop
            if(idef.type== Mushroom.class){
                items.add(new Mushroom(this,idef.position.x,idef.position.y));
            }
        }
    }
    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }
    public void handleInput(float dt){
        if(player.currentState!= Mario.State.DEAD) {
            if(player.currentState!=Mario.State.JUMPING)
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {//input.keys.up dai dien mui ten di len
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            }
//              applyLinearIMpulse()//dung de ap dung 1 luc len vat the
//              newvector2 vector luc
//              player.b2body.getworldcenter diem dat luc o trong tam vat
//              true danh thuc vat
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            }
        }
    }
    public void update (float dt){
        handleInput(dt);
        handleSpawningItems();
        //take 1 step in the Physics simulation 60 time per second
        world.step(1/60f,6,2);
        //update player
        player.update(dt);
        // update gooba
       // goomba.update(dt);
        for(Enemy enemy:creator.getEnemies()){
            enemy.update(dt);
            if(enemy.getX()<player.getX()+14*16/MarioBros.PPM){
                enemy.b2body.setActive(true);//wake up
            }
        }


        for(Item item:items){
            item.update(dt);
        }
        //set time in class hud
        hud.update(dt);

        //cho cam di chuyen cung playerre
        if(player.currentState!=Mario.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }

        gamecam.update();
        renderer.setView(gamecam);//chi render phan man hinbh thoi khonng render ca the gioi

    }
    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(1,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();//redermap
        //render box
        b2dr.render(world,gamecam.combined);
        //chieu gamcam neu khong thi cai vi tri cam khong duoc update qua method position.set
        game.batch.setProjectionMatrix(gamecam.combined);

        game.batch.begin();
       // game.batch.draw(texture,0,0); ve 1 texture ra ngoai man
        player.draw(game.batch);
        //goomba
        //goomba.draw(game.batch);
        for(Enemy enemy:creator.getEnemies()){
            enemy.draw(game.batch);
        }
        for(Item item:items){
            item.draw(game.batch);
        }
        game.batch.end();
        //draw menu hud
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);//chieu len game batch
        hud.stage.draw();
        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public boolean gameOver(){
        if(player.currentState==Mario.State.DEAD&&player.getStateTimer()>3){
            return true;
        }
        return false;
    }
    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }
    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.getLayers();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
