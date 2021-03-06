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

import necauqua.mods.cm.item.ItemRecalibrator;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import java.util.List;

import static necauqua.mods.cm.item.ItemRecalibrator.RecalibrationEffect.AMPLIFICATION;
import static necauqua.mods.cm.item.ItemRecalibrator.RecalibrationEffect.REDUCTION;

public final class Achievements extends AchievementPage {

    private static final ItemStack red = ItemRecalibrator.create(REDUCTION, (byte) 0);
    private static final ItemStack blue = ItemRecalibrator.create(AMPLIFICATION, (byte) 0);

    public static Achievement BEGINNING = create("beginning", -2, -1, new ItemStack(ChiseledMe.PYM_CONTAINER), null);
    public static Achievement ESSENSE = create("essense", 0, 0, new ItemStack(ChiseledMe.PYM_ESSENSE), BEGINNING);

    public static Achievement RESET = create("reset", 0, -2, ItemRecalibrator.create((byte) 0 /*RESET*/, (byte) 0), ESSENSE);

    public static Achievement CABLEWORK = create("cablework", -1, 1, red, ESSENSE);
    public static Achievement BIG_STAIRS = create("big_stairs", -1, 2, red, ESSENSE);
    public static Achievement MOUSE_HOLES = create("mouse_holes", -1, 3, red, ESSENSE);
    public static Achievement C_AND_B_GALORE = create("c&b_galore", -1, 4, red, ESSENSE);

    public static Achievement C_AND_B_SQUARED = create("c&b_squared", -1, 5, red, ESSENSE).setSpecial();

    public static Achievement ADVANCEMENTS = create("advancements", 2, 0, new ItemStack(ChiseledMe.PYM_CONTAINER_X), ESSENSE);

    public static Achievement CONCENTRATED = create("concentrated", 2, 2, new ItemStack(ChiseledMe.PYM_ESSENSE_X), ADVANCEMENTS);
    public static Achievement SUPERSMALLS = create("supersmalls", 3, 3, red, CONCENTRATED);
    public static Achievement THE_LIMIT = create("the_limit", 3, 4, red, CONCENTRATED).setSpecial();

    public static Achievement BLUE_NETHER_STAR = create("blue_nether_star", 2, -2, new ItemStack(ChiseledMe.BLUE_STAR), null);
    public static Achievement WEIRD_BEACON = create("weird_beacon", 4, -4, new ItemStack(Blocks.BEACON), BLUE_NETHER_STAR);
    public static Achievement SURPRISE = create("surprise", 0, -4, new ItemStack(Blocks.LAPIS_BLOCK), BLUE_NETHER_STAR).setSpecial();

    public static Achievement ANTIPOLARIZATION = create("antipolarization", 4, -2, new ItemStack(ChiseledMe.PYM_ESSENSE_B), BLUE_NETHER_STAR);
    public static Achievement DOUBLE = create("double", 5, -1, blue, ANTIPOLARIZATION);
    public static Achievement QUADRUPLE = create("quadruple", 5, 0, blue, ANTIPOLARIZATION);
    public static Achievement OCTUPLE = create("octuple", 5, 1, blue, ANTIPOLARIZATION);
    public static Achievement SEXDECUPLE = create("sexdecuple", 5, 2, blue, ANTIPOLARIZATION).setSpecial();

    private static AchievementPage page;

    public static void init() {
        AchievementPage.registerAchievementPage(page = new Achievements());
    }

    public static Achievement get(int id) {
        return page.getAchievements().get(id);
    }

    private Achievements() {
        super("Chiseled Me"); // soo.. no lang keys? m'key
        List<Achievement> achievements = getAchievements();
        RandomUtils.forEachStaticField(Achievement.class, achievements::add);
        if(!Config.enableSupersmalls) {
            achievements.remove(SUPERSMALLS);
            achievements.remove(THE_LIMIT);
        }
        if(!Config.enableBigSizes) {
            achievements.remove(ANTIPOLARIZATION);
            achievements.remove(DOUBLE);
            achievements.remove(QUADRUPLE);
            achievements.remove(OCTUPLE);
            achievements.remove(SEXDECUPLE);
        }
    }

    private static Achievement create(String name, int column, int row, ItemStack stack, Achievement parent) {
        return new Achievement("chiseled_me:" + name, "chiseled_me:" + name, column, row, stack, parent).registerStat();
    }
}