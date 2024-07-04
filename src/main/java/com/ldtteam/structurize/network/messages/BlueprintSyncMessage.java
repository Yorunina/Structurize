package com.ldtteam.structurize.network.messages;

import com.ldtteam.common.network.AbstractServerPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import com.ldtteam.structurize.api.constants.Constants;
import com.ldtteam.structurize.storage.BlueprintPlacementHandling;
import com.ldtteam.structurize.api.RotationMirror;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.commons.io.FilenameUtils;

/**
 * Sends a blueprint from the client to the server.
 */
public class BlueprintSyncMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "blueprint_sync", BlueprintSyncMessage::new);

    /**
     * Structure placement info.
     */
    public final BuildToolPlacementMessage.HandlerType type;
    public final String handlerId;
    public       String structurePackId;
    public final String blueprintPath;
    public final BlockPos pos;
    public final RotationMirror rotationMirror;

    /**
     * Blueprint data future.
     */
    public final byte[] blueprintData;

    /**
     * Buffer reading message constructor.
     */
    protected BlueprintSyncMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        this.type = BuildToolPlacementMessage.HandlerType.values()[buf.readInt()];
        this.handlerId = buf.readUtf(32767);

        this.structurePackId = buf.readUtf(32767);
        this.blueprintPath = FilenameUtils.normalize(buf.readUtf(32767));
        this.pos = buf.readBlockPos();
        this.rotationMirror = RotationMirror.values()[buf.readInt()];

        this.blueprintData = buf.readByteArray();
    }

    /**
     * Send requested data from the client.
     *
     * @param msg the request message to get most data from.
     * @param blueprintData the blueprint data.
     */
    public BlueprintSyncMessage(
      final ClientBlueprintRequestMessage msg,
      final byte[] blueprintData)
    {
        super(TYPE);
        this.type = msg.type;
        this.handlerId = msg.handlerId;

        this.structurePackId = msg.structurePackId;
        this.blueprintPath = msg.blueprintPath;
        this.pos = msg.pos;
        this.rotationMirror = msg.rotationMirror;
        this.blueprintData = blueprintData;
    }

    @Override
    protected void toBytes(final RegistryFriendlyByteBuf buf)
    {
        buf.writeInt(this.type.ordinal());
        buf.writeUtf(this.handlerId);

        buf.writeUtf(this.structurePackId);
        buf.writeUtf(this.blueprintPath);
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.rotationMirror.ordinal());

        buf.writeByteArray(this.blueprintData);
    }

    @Override
    protected void onExecute(final IPayloadContext context, final ServerPlayer player)
    {
        BlueprintPlacementHandling.handlePlacement(this, player);
    }
}
