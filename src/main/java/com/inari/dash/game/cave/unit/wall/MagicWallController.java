package com.inari.dash.game.cave.unit.wall;

import com.inari.commons.geom.Direction;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.wall.MagicWallAnimationController.State;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEvent.Type;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;
import com.inari.firefly.system.FFTimer.UpdateScheduler;

public final class MagicWallController extends UnitController {
    
    private int activTime = 0;
    private int activDuration;
    
    private UpdateScheduler secondTimer;
    private MagicWallAnimationController magicWallAnimationController;

    protected MagicWallController( int id, FFContext context ) {
        super( id, context );
        activDuration = caveService.getMagicWallTime();
        secondTimer = context.getTimer().createUpdateScheduler( 1 );
    }
    
    void setMagicWallAnimationController( MagicWallAnimationController magicWallAnimationController ) {
        this.magicWallAnimationController = magicWallAnimationController;
    }

    @Override
    protected final void update( FFTimer timer, int entityId ) {
        if ( activTime > activDuration ) {
            return;
        }
        
        if ( activTime > 0 ) {
            if ( secondTimer.needsUpdate() ) {
                activTime++;
                if ( activTime > activDuration ) {
                    magicWallAnimationController.setMagicWallState( State.INACTIVE );
                    context .notify( new SoundEvent( UnitType.MAGIC_WALL.handler.getSoundId(), Type.STOP_PLAYING ) ); 
                    return;
                }
            }
        }

        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        int x = tile.getGridXPos();
        int y = tile.getGridYPos();
        
        if ( unit.isHit() ) {
            unit.setHit( false );
            if ( activTime == 0 ) {
                activTime++;
                secondTimer.getTick();
                magicWallAnimationController.setMagicWallState( State.ACTIVE );
                context.notify( new SoundEvent( UnitType.MAGIC_WALL.handler.getSoundId(), Type.PLAY_SOUND ) ); 
            }

            int aboveEntityId = caveService.getEntityId( x, y, Direction.NORTH );
            EUnit aboveUnit = context.getEntityComponent( aboveEntityId, EUnit.TYPE_KEY );
            UnitType changeTo = aboveUnit.getChangeTo();
            
            if ( changeTo != null ) {
                unit.setChangeTo( changeTo );
                caveService.deleteUnit( x, y, Direction.NORTH );
                caveService.createOne( x, y - 1, UnitType.SPACE );
            }
            return;
        }
        
        UnitType changeTo = unit.getChangeTo();
        if ( changeTo != null ) {
            if ( caveService.getUnitType( x, y, Direction.SOUTH ) == UnitType.SPACE ) {
                caveService.createOne( x, y + 1, changeTo );
            }
            unit.setChangeTo( null );
        }
    }


}
