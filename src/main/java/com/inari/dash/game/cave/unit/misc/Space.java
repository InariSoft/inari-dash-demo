package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;

public final class Space extends UnitHandle {
    
    public static final String SPACE_NAME = "space";
    
    private int spaceEntityId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        initialized = true;
    }

    @Override
    public void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        assetSystem.getAssetBuilder()
            .set( SpriteAsset.NAME, SPACE_NAME )
            .set( SpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 5 * 32, 0, 32, 32 ) )
        .activate( SpriteAsset.class );

        spaceEntityId = entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETile.MULTI_POSITION, true )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetInstanceId( SPACE_NAME ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE, 
                UnitAspect.CONSUMABLE, 
                UnitAspect.WALKABLE 
            ) )
        .activate();
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        entitySystem.delete( spaceEntityId );
        spaceEntityId = -1;
        assetSystem.deleteAsset( SPACE_NAME );
    }

    @Override
    public final void dispose( FFContext context ) {
        assetSystem.disposeAsset( SPACE_NAME );
    }

    @Override
    public final UnitType type() {
        return UnitType.SPACE;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( " ", type() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        ETile tile = entitySystem.getComponent( spaceEntityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        caveService.setEntityId( spaceEntityId, xGridPos, yGridPos );
        return spaceEntityId;
    }

    @Override
    public final int getEntityId() {
        return spaceEntityId;
    }

}
