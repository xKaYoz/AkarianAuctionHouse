package net.akarian.auctionhouse.utils;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class IntegerDataType implements PersistentDataType<Integer, Integer> {


    @NotNull
    @Override
    public Class<Integer> getPrimitiveType() {
        return Integer.class;
    }

    @NotNull
    @Override
    public Class<Integer> getComplexType() {
        return Integer.class;
    }

    @NotNull
    @Override
    public Integer toPrimitive(@NotNull Integer complex, @NotNull PersistentDataAdapterContext context) {
        return complex;
    }

    @NotNull
    @Override
    public Integer fromPrimitive(@NotNull Integer primitive, @NotNull PersistentDataAdapterContext context) {
        return primitive;
    }
}
