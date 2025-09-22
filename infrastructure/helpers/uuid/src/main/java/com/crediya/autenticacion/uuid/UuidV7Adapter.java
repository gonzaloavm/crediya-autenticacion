package com.crediya.autenticacion.uuid;

import com.crediya.autenticacion.port.UuidProviderPort;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.UUID;

@Service
public class UuidV7Adapter implements UuidProviderPort {

    @Override
    public UUID generate() {
        return UuidCreator.getTimeOrderedEpoch();
    }

    @Override
    public byte[] toBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    @Override
    public UUID fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long most = buffer.getLong();
        long least = buffer.getLong();
        return new UUID(most, least);
    }

    @Override
    public byte[] fromString(String uuidString) {
        return toBytes(UUID.fromString(uuidString));
    }

    @Override
    public String toString(byte[] bytes) {
        return fromBytes(bytes).toString();
    }


}
