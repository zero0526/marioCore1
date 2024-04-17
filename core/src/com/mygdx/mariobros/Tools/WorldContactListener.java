package com.mygdx.mariobros.Tools;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mariobros.MarioBros;
import com.mygdx.mariobros.enemies.Enemy;
import com.mygdx.mariobros.Sprites.InteractiveTileObject;
import com.mygdx.mariobros.Sprites.Mario;
import com.mygdx.mariobros.items.Item;

public class WorldContactListener implements ContactListener {//duoc goi tu
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA=contact.getFixtureA();
        Fixture fixB= contact.getFixtureB();

        int cDef=fixA.getFilterData().categoryBits|fixB.getFilterData().categoryBits;

//        if(fixA.getUserData()=="head"||fixB.getUserData()=="head"){
//            Fixture head=fixA.getUserData()=="head"?fixA:fixB;
//            Fixture object=fixA.getUserData()!="head"?fixA:fixB;
//
//            if(object.getUserData()!=null&& InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())){
//                ((InteractiveTileObject)object.getUserData()).onHeadHit();
//            }
//        }
        switch(cDef){
            case MarioBros.MARIO_HEAD_BIT|MarioBros.BRICK_BIT:
            case MarioBros.MARIO_HEAD_BIT|MarioBros.COIN_BIT:
                if(fixA.getFilterData().categoryBits==MarioBros.MARIO_HEAD_BIT){
                    ((InteractiveTileObject)fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                }
                else{
                    ((InteractiveTileObject)fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                }
                break;
            case MarioBros.HEAD_ENENY_BIT| MarioBros.MARIO_BIT:
                if(fixA.getFilterData().categoryBits==MarioBros.HEAD_ENENY_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else if(fixB.getFilterData().categoryBits==MarioBros.HEAD_ENENY_BIT)
                    ((Enemy)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            case MarioBros.ENEMY_BIT|MarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits==MarioBros.ENEMY_BIT){
                    ((Enemy)fixA.getUserData()).reverseVelocity(true,false);
                }
                else{
                    ((Enemy)fixB.getUserData()).reverseVelocity(true,false);
                }
                break;
            case MarioBros.ENEMY_BIT|MarioBros.ENEMY_BIT:

                    ((Enemy)fixA.getUserData()).onEnemyHit((Enemy)fixB.getUserData());
                    ((Enemy)fixB.getUserData()).onEnemyHit((Enemy)fixA.getUserData());

                break;
            case MarioBros.ITEM_BIT|MarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits==MarioBros.ITEM_BIT){
                    ((Item)fixA.getUserData()).reverseVelocity(true,false);
                }
                else{
                    ((Item)fixB.getUserData()).reverseVelocity(true,false);
                }
                break;
            case MarioBros.ITEM_BIT|MarioBros.MARIO_BIT:
                if(fixA.getFilterData().categoryBits==MarioBros.ITEM_BIT){
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                }
                else{
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                }
                break;
            case MarioBros.MARIO_BIT|MarioBros.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits==MarioBros.MARIO_BIT){
                    ((Mario) fixA.getUserData()).hit (((Enemy)fixB.getUserData()));
                }
                else{
                   ((Mario) fixB.getUserData()).hit(((Enemy)fixA.getUserData()));
                }
                break;
        }
        }
    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }


}
