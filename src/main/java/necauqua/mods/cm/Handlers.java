/*
 * This mod adds a possibility for one to become much smaller then they
 * are, which is useful for example when dealing with mods such as
 * Chisel & Bits and so on.
 * Copyright (C) 2016 necauqua
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this mod.  If not, see <http://www.gnu.org/licenses/>.
 */

package necauqua.mods.cm;

import net.minecraft.block.BlockBed;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

import static necauqua.mods.cm.EntitySizeManager.getSize;

/** This class holds misc event handlers. **/
public final class Handlers {

    private Handlers() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Handlers());
        if(Config.changeBedAABB) {
            fixBedAABB();
        }
    }

    private static void fixBedAABB() {
        try {
            Field f = ReflectionHelper.findField(BlockBed.class, "field_185513_c", "BED_AABB");
            AxisAlignedBB aabb = new AxisAlignedBB(0.0, 0.1875, 0.0, 1.0, 0.5625, 1.0);
            EnumHelper.setFailsafeFieldValue(f, null, aabb); // this can set final non-primitive fields
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(EntityInteractSpecific e) {
        if(getSize(e.getEntity()) != getSize(e.getTarget())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent e) { // todo this is temp, remove after riding fix (not soon)
        if(e.isMounting() && (getSize(e.getEntityMounting()) != 1.0F || getSize(e.getEntityBeingMounted()) != 1.0F)) {
            e.setCanceled(true);
        }
    }

    private static final SleepResult TOO_SMALL = EnumHelper.addStatus("TOO_SMALL");
    private static final SleepResult TOO_BIG = EnumHelper.addStatus("TOO_BIG");

    @SubscribeEvent
    public void onPlayerSleepInBed(PlayerSleepInBedEvent e) {
        EntityPlayer player = e.getEntityPlayer();
        float size = getSize(player);
        if(size < 1.0F) {
            e.setResult(TOO_SMALL);
            player.addChatComponentMessage(new TextComponentTranslation("chiseled_me.bed.too_small"));
        }else if(size > 1.0F) {
            e.setResult(TOO_BIG);
            player.addChatComponentMessage(new TextComponentTranslation("chiseled_me.bed.too_big"));
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent e) {
        float size = getSize(e.getEntity());
        if(size != 1.0F) {
            for(EntityItem item : e.getDrops()) {
                EntitySizeManager.setSize(item, size, false);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDrop(ItemTossEvent e) {
        float size = getSize(e.getPlayer());
        if(size != 1.0F) {
            EntitySizeManager.setSize(e.getEntityItem(), size, false);
        }
    }

    @SubscribeEvent
    public void onPlayerBreak(BlockEvent.HarvestDropsEvent e) {
        EntityPlayer player = e.getHarvester();
        float size;
        if(player != null && (size = getSize(player)) < 1.0) {
            for(ItemStack stack : e.getDrops()) {
                NBTTagCompound nbt = stack.getTagCompound();
                if(nbt == null) {
                    nbt = new NBTTagCompound();
                }
                nbt.setFloat("chiseled_me:size", size);
                stack.setTagCompound(nbt);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerBreakSpeed(PlayerEvent.BreakSpeed e) {
        e.setNewSpeed(e.getNewSpeed() * getSize(e.getEntity()));
    }
}