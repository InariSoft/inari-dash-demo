package com.inari.dash.game.cave.action;

import com.inari.commons.lang.indexed.IndexedObject;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.action.Action;

public enum ActionType implements IndexedObject {
    MOVE( MoveAction.class ),
    HIT( null ),
    EXPLODE( ExplosionAction.class ),
    COLLECT_DIAMOND( null )
    ;
    
    private final int index;
    private final Class<? extends Action> actionTypeClass;
    private ActionType( Class<? extends Action> actionTypeClass ) {
        index = Indexer.getIndexedObjectSize( Action.class ) + ordinal();
        this.actionTypeClass = actionTypeClass;
    }

    @Override
    public final int index() {
        return index;
    }
    
    public final int type() {
        return index;
    }

    public final Class<? extends Action> getActionTypeClass() {
        return actionTypeClass;
    }

    @Override
    public final Class<ActionType> indexedObjectType() {
        return ActionType.class;
    } 
}