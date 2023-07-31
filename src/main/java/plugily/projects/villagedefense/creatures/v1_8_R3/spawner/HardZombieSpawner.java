
/*
 *  Village Defense - Protect villagers from hordes of zombies
 *  Copyright (c) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.creatures.v1_8_R3.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.SimpleEnemySpawner;
import plugily.projects.villagedefense.creatures.CreatureUtils;

public class HardZombieSpawner implements SimpleEnemySpawner {
  @Override
  public int getMinWave() {
    return 4;
  }

  @Override
  public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
    if(phase == 5 && wave > 14 && wave <= 20) {
      return 1D / 3;
    }
    if(phase == 15 && wave > 8) {
      return 1;
    }
    return 0;
  }

  @Override
  public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
    if(phase == 5 && wave > 14 && wave <= 20) {
      return spawnAmount;
    }
    if(phase == 15 && wave > 8) {
      return spawnAmount - 7;
    }
    return 0;
  }

  @Override
  public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
    return phase == 5 || phase == 15;
  }

  @Override
  public Creature spawn(Location location) {
    Creature hardZombie = CreatureUtils.getCreatureInitializer().spawnHardZombie(location);
    hardZombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    hardZombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    hardZombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    hardZombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    return hardZombie;
  }

  @Override
  public String getName() {
    return "HardZombie";
  }

  @Override
  public ItemStack getDropItem() {
    return null;
  }
}
