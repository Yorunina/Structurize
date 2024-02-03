package com.ldtteam.structurize.network.messages;

import com.ldtteam.common.network.AbstractClientPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import com.ldtteam.structurize.api.util.constant.Constants;
import com.ldtteam.structurize.storage.ClientStructurePackLoader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * Transfer a zipped structure pack to the client.
 */
public class TransferStructurePackToClient extends AbstractClientPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forClient(Constants.MOD_ID, "transfer_structure_pack_to_client", TransferStructurePackToClient::new);

    /**
     * Payload of the message (to transfer to client).
     */
    private final ByteBuf payload;

    /**
     * The name of the structure pack.
     */
    private final String packname;

    /**
     * If the last message.
     */
    private final boolean eol;

    /**
     * Public standard constructor.
     */
    public TransferStructurePackToClient(final FriendlyByteBuf buf)
    {
        super(buf, TYPE);
        this.packname = buf.readUtf(32767);
        this.eol = buf.readBoolean();
        this.payload = Unpooled.wrappedBuffer(buf.readByteArray());
    }

    /**
     * Transfer a zipped structure pack to the client.
     * @param packName the name of the structure pack.
     * @param payload the payload.
     * @param eol if last message to client.
     */
    public TransferStructurePackToClient(final String packName, final ByteBuf payload, final boolean eol)
    {
        super(TYPE);
        this.packname = packName;
        this.payload = payload;
        this.eol = eol;
    }

    @Override
    public void toBytes(final FriendlyByteBuf buf)
    {
        buf.writeUtf(this.packname);
        buf.writeBoolean(this.eol);
        buf.writeByteArray(this.payload.array());
        this.payload.release();
    }

    @Override
    public void onExecute(final PlayPayloadContext context, final Player player)
    {
        ClientStructurePackLoader.onStructurePackTransfer(this.packname, this.payload, this.eol);
    }
}
