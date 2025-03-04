/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2024  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.kits.utils;

/**
 * @author Plajer
 * <p>
 * Created at 29.09.2023
 */
public class ActionBarPriority {

  public static final int DAMAGE_EFFECT = 0;
  public static final int BUFFS = 1;
  public static final int HEALING = 2;
  public static final int HEALING_AND_BUFFS = 3;
  public static final int PASSIVE = 4;
  public static final int ULTIMATE = 5;
  public static final int LOW_PRIORITY = 1;
  public static final int MEDIUM_PRIORITY = 4;
  public static final int HIGH_PRIORITY = 6;

}
