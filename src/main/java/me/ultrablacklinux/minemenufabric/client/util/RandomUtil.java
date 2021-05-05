package me.ultrablacklinux.minemenufabric.client.util;

import com.mojang.authlib.GameProfile;
import me.shedaniel.math.Color;
import me.ultrablacklinux.minemenufabric.client.MineMenuFabricClient;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RandomUtil {
    public static me.shedaniel.math.Color getColor(String inp) {
        long colorLong = Long.decode(inp);
        float f = (float) (colorLong >> 24 & 0xff) / 255F;
        float f1 = (float) (colorLong >> 16 & 0xff) / 255F;
        float f2 = (float) (colorLong >> 8 & 0xff) / 255F;
        float f3 = (float) (colorLong & 0xff) / 255F;
        return Color.ofRGBA(f, f1, f2, f3);
    }

    public static ItemStack iconify(Consumer<ItemStack> consumer, String iconItem, boolean enchanted, String skullowner) {
        ItemStack out;
        try {
            out = itemStackFromString(iconItem);
            try {
                if (enchanted) {
                    Map<Enchantment, Integer> e = new HashMap<>();
                    e.put(Enchantment.byRawId(1), 1);
                    EnchantmentHelper.set(e, out);
                }

                if (!skullowner.equals("") && isSkullItem(out)) {
                    ItemStack finalOut = out;
                    Thread nbTater = new Thread(() -> {
                        CompoundTag tag = finalOut.getOrCreateTag();
                        GameProfile gameProfile = new GameProfile((UUID)null, skullowner);
                        gameProfile = SkullBlockEntity.loadProperties(gameProfile);
                        tag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
                        consumer.accept(finalOut);
                    });
                    nbTater.start();
                    out = null;

                } else out.removeSubTag("SkullOwner");

            } catch (Exception ignore) {ignore.printStackTrace();}
        } catch (InvalidIdentifierException e) {
            out = new ItemStack(Items.AIR);
        }
        return out;
    }

    public static ItemStack itemStackFromString(String itemStack) {
        return Registry.ITEM.get(new Identifier(itemStack)).getDefaultStack();
    }

    public static boolean isSkullItem(ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((net.minecraft.item.BlockItem)
                stack.getItem()).getBlock() instanceof AbstractSkullBlock;
    }
}
