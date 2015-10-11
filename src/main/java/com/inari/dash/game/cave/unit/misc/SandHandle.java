package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.entity.EntityPrefabSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.view.ViewSystem;

public final class SandHandle extends UnitHandle {
    
    public static final AssetNameKey SAND_SPRITE_ASSET_KEY = new AssetNameKey( "units", "sand" );
    public static final String SAND_PREFAB_NAME = "SAND_PREFAB";
    
    private EntitySystem entitySystem;
    private EntityPrefabSystem prefabSystem;
    private int prefabId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        prefabSystem = context.getComponent( EntityPrefabSystem.CONTEXT_KEY );
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        
        super.caveAssetsToReload.add( SAND_SPRITE_ASSET_KEY );
        assetSystem.getAssetBuilder( SpriteAsset.class )
            .set( SpriteAsset.NAME, SAND_SPRITE_ASSET_KEY.name )
            .set( SpriteAsset.ASSET_GROUP, SAND_SPRITE_ASSET_KEY.group )
            .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetId( CaveService.GAME_UNIT_TEXTURE_KEY ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 32, 7 * 32, 32, 32 ) )
        .build();
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .set( EntityPrefab.NAME, SAND_PREFAB_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.CAVE_VIEW_NAME ) )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETile.GRID_X_POSITION, 0 )
            .set( ETile.GRID_Y_POSITION, 0 )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetId( SAND_SPRITE_ASSET_KEY ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( UnitAspect.DESTRUCTIBLE, UnitAspect.CONSUMABLE ) )
        .build().getId();
        prefabSystem.cacheComponents( prefabId, 100 );
        
        initialized = true;
    }

    @Override
    public final void dispose( FFContext context ) {
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        prefabSystem.deletePrefab( SAND_PREFAB_NAME );
        assetSystem.disposeAsset( SAND_SPRITE_ASSET_KEY );
    }

    @Override
    public final UnitType type() {
        return UnitType.SAND;
    }

    @Override
    public void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( ".", type() );
    }

    @Override
    public final void createOne( int xGridPos, int yGridPos ) {
        Entity entity = prefabSystem.buildOne( prefabId );
        ETile tile = entitySystem.getComponent(  entity.getId(), ETile.class );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        entitySystem.activate( entity.getId() );
    }

}
