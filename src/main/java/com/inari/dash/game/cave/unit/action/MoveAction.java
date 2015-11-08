package com.inari.dash.game.cave.unit.action;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.renderer.tile.ETile;

public final class MoveAction extends UnitAction {
    
    protected final int COMPONENT_ID_EUNIT = Indexer.getIndexForType( EUnit.class, EntityComponent.class );
    protected final int COMPONENT_ID_ETILE = Indexer.getIndexForType( ETile.class, EntityComponent.class );

    private Position tmpPos = new Position();

    public MoveAction( int id ) {
        super( id );
    }

    @Override
    public final void performAction( int entityId ) {
        // First we get the EUnit component and its Direction
        EUnit unit = entitySystem.getComponent( entityId, COMPONENT_ID_EUNIT );
        Direction movement = unit.getMovement();
        if ( movement == Direction.NONE ) {
            return;
        }
        
        ETile tile = entitySystem.getComponent( entityId, COMPONENT_ID_ETILE );
        tmpPos.x = tile.getGridXPos();
        tmpPos.y = tile.getGridYPos();
        
        UnitType.SPACE.handler.createOne( tmpPos.x, tmpPos.y );
        GeomUtils.movePositionOnDirection( tmpPos, movement, 1, true );
        int entityOnDirection = caveService.getEntityId( tmpPos.x, tmpPos.y );
        caveService.deleteUnit( entityOnDirection, tmpPos.x, tmpPos.y );
        tile.setGridXPos( tmpPos.x );
        tile.setGridYPos( tmpPos.y );
        caveService.setEntityId( entityId, tmpPos.x, tmpPos.y );
    }

}