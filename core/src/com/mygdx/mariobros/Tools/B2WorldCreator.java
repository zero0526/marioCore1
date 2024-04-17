package com.mygdx.mariobros.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.MarioBros;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Brick;
import com.mygdx.mariobros.Sprites.Coin;
import com.mygdx.mariobros.enemies.Enemy;
import com.mygdx.mariobros.enemies.Goomba;
import com.mygdx.mariobros.enemies.Turtle;

public class B2WorldCreator {
    private Array<Goomba> goombas;
    private static Array<Turtle> turtles;
    public B2WorldCreator(PlayScreen screen){
        World world=screen.getWorld();
        TiledMap map=screen.getMap();
        //cau hinh cho phan than
        BodyDef bdef=new BodyDef();
        PolygonShape shape=new PolygonShape();
        FixtureDef fdef=new FixtureDef();
        Body body;
//cauhinh mat dat
        for(MapObject object:map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            //map.getLayers().get(2) truy cap vao lop thu 3 cua ban do tinh tu duoi len đem tu 0
            //get object lay tat ca cac doi tuong thuoc lop truy cap
            //getByType(RectangleMapObject.class)dung de locc ra cac doi tuong thuoc lop rectangle..
            Rectangle rect=((RectangleMapObject)object).getRectangle();
            //ep object tu mapObject sáng rectanglemapObject
            //.getrectsngle( chuyen tu rangtanglemapObject sang rectangle
            bdef.type=BodyDef.BodyType.StaticBody;
            //staticbody tao ra vat khong dichuyen khong chiu tac dong boi luc
            //dynamicBody tao ra object di chuyen chiu anh huong boi luc
            //kinemticbody tao ra object di chuyen khong bi tac dong boi luc
            bdef.position.set((rect.getX()+rect.getWidth()/2)/ MarioBros.PPM,(rect.getY()+rect.getHeight()/2)/MarioBros.PPM);//co dinh vat the o cho cu

            body = world.createBody(bdef);//d=them noi dung vao box2d tao them vat the moi voi cac dac tinh cua bodydef
            shape.setAsBox(rect.getWidth()/2/MarioBros.PPM,rect.getHeight()/2/MarioBros.PPM);//chia 2 vi no di theo ca hai huong
            fdef.shape=shape;///dinh nghia hih dang cua vat the
            body.createFixture(fdef);
        }
        //cau hinh pipe
        for(MapObject object:map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect=((RectangleMapObject)object).getRectangle();
            bdef.type=BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX()+rect.getWidth()/2)/MarioBros.PPM,(rect.getY()+rect.getHeight()/2)/MarioBros.PPM);
            body = world.createBody(bdef);
            shape.setAsBox(rect.getWidth()/2/MarioBros.PPM,rect.getHeight()/2/MarioBros.PPM);
            fdef.shape=shape;
            fdef.filter.categoryBits=MarioBros.OBJECT_BIT;
            body.createFixture(fdef);
        }
        //cau hiinh brick
        for(MapObject object:map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen,object);
        }
        //cauh hinh coin
        for(MapObject object:map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen,object);
        }
        //create all goombas
        goombas=new Array<Goomba>();
        for(MapObject object:map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect=((RectangleMapObject)object).getRectangle();
            goombas.add(new Goomba(screen,rect.getX()/MarioBros.PPM,rect.getY()/MarioBros.PPM));
        }
        //create all turtle
        turtles=new Array<Turtle>();
        for(MapObject object:map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect=((RectangleMapObject)object).getRectangle();
            turtles.add(new Turtle(screen,rect.getX()/MarioBros.PPM,rect.getY()/MarioBros.PPM));
        }

    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
    public static void removeTurtle(Turtle turtle){
        turtles.removeValue(turtle,true);
    }
    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);

        return enemies;
    }
}
