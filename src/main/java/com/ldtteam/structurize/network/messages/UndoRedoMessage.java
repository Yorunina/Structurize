package com.ldtteam.structurize.network.messages;

import com.ldtteam.common.network.AbstractServerPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import com.ldtteam.structurize.api.constants.Constants;
import com.ldtteam.structurize.management.Manager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Message class which handles undoing a change to the world.
 */
public class UndoRedoMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "undo_redo", UndoRedoMessage::new);

    private final int     id;
    private final boolean undo;

    /**
     * Empty public constructor.
     */
    public UndoRedoMessage(final int id, final boolean undo)
    {
        super(TYPE);
        this.undo = undo;
        this.id = id;
    }

    protected UndoRedoMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        this.id = buf.readInt();
        this.undo = buf.readBoolean();
    }

    @Override
    protected void toBytes(final RegistryFriendlyByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeBoolean(undo);
    }

    @Override
    protected void onExecute(final IPayloadContext context, final ServerPlayer player)
    {
        if (!player.isCreative())
        {
            return;
        }

        if (undo)
        {
            Manager.undo(player, id);
        }
        else
        {
            Manager.redo(player, id);
        }
    }
}
