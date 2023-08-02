/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.kits.premium;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.MessageManager;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XSound;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;
import plugily.projects.villagedefense.kits.KitHelper;
import plugily.projects.villagedefense.kits.KitSpecifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tom on 18/08/2014.
 */
public class CleanerKit extends PremiumKit implements Listener {

  private static final String LANGUAGE_ACCESSOR = "KIT_CONTENT_CLEANER_";
  private static final double POP_DAMAGE = 1000000.0;
  private final List<Arena> poplustActives = new ArrayList<>();
  private final Random random = new Random();

  public CleanerKit() {
    registerMessages();
    setName(new MessageBuilder(LANGUAGE_ACCESSOR + "NAME").asKey().build());
    setKey("Cleaner");
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "DESCRIPTION");
    setDescription(description);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    getPlugin().getKitRegistry().registerKit(this);
    schedulePopAwe();
  }

  private void registerMessages() {
    MessageManager manager = getPlugin().getMessageManager();
    manager.registerMessage(LANGUAGE_ACCESSOR + "NAME", new Message("Kit.Content.Cleaner.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "DESCRIPTION", new Message("Kit.Content.Cleaner.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_CLEANSING_STICK_NAME", new Message("Kit.Content.Cleaner.Game-Item.Cleansing-Stick.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_CLEANSING_STICK_DESCRIPTION", new Message("Kit.Content.Cleaner.Game-Item.Cleansing-Stick.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_CLEANSING_STICK_ACTIVATE", new Message("Kit.Content.Cleaner.Game-Item.Cleansing-Stick.Activate", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_NAME", new Message("Kit.Content.Cleaner.Game-Item.Poplust.Name", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_DESCRIPTION", new Message("Kit.Content.Cleaner.Game-Item.Poplust.Description", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_ACTIVATE", new Message("Kit.Content.Cleaner.Game-Item.Poplust.Activate", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_ACTIVE_ACTION_BAR", new Message("Kit.Content.Cleaner.Game-Item.Poplust.Active-Action-Bar", ""));
    manager.registerMessage(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_ACTIVE_TITLE", new Message("Kit.Content.Cleaner.Game-Item.Poplust.Active-Title", ""));
  }

  private void schedulePopAwe() {
    //passive only works for alive players not dead ones
    Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
      for(PluginArena pluginArena : getPlugin().getArenaRegistry().getArenas()) {
        Arena arena = (Arena) pluginArena;
        if(arena.getArenaState() != ArenaState.IN_GAME || arena.getEnemies().isEmpty()) {
          continue;
        }
        for(Player player : arena.getPlayersLeft()) {
          if(!(getPlugin().getUserManager().getUser(player).getKit() instanceof CleanerKit)) {
            continue;
          }
          executeRandomPops(arena, player, (int) Settings.PASSIVE_POP_COUNT.getForArenaState(arena));
        }
      }
    }, 20 * 5L, 20 * 5L);
  }

  private void executeRandomPops(Arena arena, Player player, int amount) {
    for(int i = 0; i < amount; i++) {
      Creature enemy = arena.getEnemies().get(random.nextInt(arena.getEnemies().size()));
      VersionUtils.sendParticles("LAVA", arena.getPlayers(), enemy.getLocation(), 20);
      enemy.damage(POP_DAMAGE, player);
    }
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player) || player.hasPermission("villagedefense.kit.cleaner");
  }

  @Override
  public void giveKitItems(Player player) {
    ArmorHelper.setColouredArmor(Color.YELLOW, player);
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));

    player.getInventory().setItem(3, new ItemBuilder(new ItemStack(XMaterial.BLAZE_ROD.parseMaterial()))
      .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_CLEANSING_STICK_NAME").asKey().build())
      .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_CLEANSING_STICK_DESCRIPTION"))
      .build());
    player.getInventory().setItem(4, new ItemBuilder(new ItemStack(XMaterial.SUNFLOWER.parseMaterial()))
      .name(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_NAME").asKey().build())
      .lore(getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_DESCRIPTION"))
      .build());
    player.getInventory().setItem(5, new ItemStack(Material.SADDLE));
    player.getInventory().setItem(8, new ItemStack(Material.COOKED_BEEF, 10));
  }

  @Override
  public Material getMaterial() {
    return Material.BLAZE_POWDER;
  }

  @Override
  public void reStock(Player player) {
    Arena arena = (Arena) getPlugin().getUserManager().getUser(player).getArena();
    if(arena.getWave() == KitSpecifications.GameTimeState.MID.getStartWave()) {
      new MessageBuilder("KIT_ABILITY_UNLOCKED").asKey().value(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_NAME").asKey().build()).send(player);
      new MessageBuilder("KIT_ABILITY_POWER_INCREASED").asKey().value(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_CLEANSING_STICK_NAME").asKey().build()).send(player);
      new MessageBuilder("KIT_PASSIVE_POWER_INCREASED").asKey().send(player);
    } else if(arena.getWave() == KitSpecifications.GameTimeState.LATE.getStartWave()) {
      new MessageBuilder("KIT_ABILITY_POWER_INCREASED").asKey().value(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_NAME").asKey().build()).send(player);
      new MessageBuilder("KIT_ABILITY_POWER_INCREASED").asKey().value(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_CLEANSING_STICK_NAME").asKey().build()).send(player);
      new MessageBuilder("KIT_PASSIVE_POWER_INCREASED").asKey().send(player);
    }
  }

  @EventHandler
  public void onItemUse(PlugilyPlayerInteractEvent event) {
    if(!KitHelper.isInGameWithKitAndItemInHand(event.getPlayer(), CleanerKit.class)) {
      return;
    }
    ItemStack stack = VersionUtils.getItemInHand(event.getPlayer());
    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    Player player = event.getPlayer();
    if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_CLEANSING_STICK_NAME").asKey().build())) {
      if(!user.checkCanCastCooldownAndMessage("cleaner_cleansing")) {
        return;
      }
      applyCleansing(user, stack);
    } else if(ComplementAccessor.getComplement().getDisplayName(stack.getItemMeta()).equals(new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_NAME").asKey().build())) {
      if(!user.checkCanCastCooldownAndMessage("cleaner_poplust")) {
        return;
      }
      if(KitSpecifications.getTimeState((Arena) user.getArena()) == KitSpecifications.GameTimeState.EARLY) {
        new MessageBuilder("KIT_LOCKED_TILL").asKey().integer(16).send(player);
        return;
      }
      applyPoplust(user, stack);
    }
  }

  private void applyCleansing(User user, ItemStack stack) {
    int cooldown = getCleansingCooldown((Arena) user.getArena());
    user.setCooldown("cleaner_cleansing", cooldown);
    VersionUtils.setMaterialCooldown(user.getPlayer(), stack.getType(), cooldown * 20);

    new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_CLEANSING_STICK_ACTIVATE").asKey().send(user.getPlayer());
    int popsPerCycle = 5;
    int splitAmount = (int) Settings.CLEANSING_POP_COUNT.getForArenaState((Arena) user.getArena()) / popsPerCycle;
    for(int i = 0; i < splitAmount; i++) {
      Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
        XSound.BLOCK_LAVA_POP.play(user.getPlayer());
        executeRandomPops((Arena) user.getArena(), user.getPlayer(), popsPerCycle);
      }, 10L * splitAmount);
    }
  }

  private int getCleansingCooldown(Arena arena) {
    switch(KitSpecifications.getTimeState(arena)) {
      case LATE:
        return getKitsConfig().getInt("Kit-Cooldown.Cleaner.Cleansing-Wand.III", 35);
      case MID:
        return getKitsConfig().getInt("Kit-Cooldown.Cleaner.Cleansing-Wand.II", 40);
      case EARLY:
      default:
        return getKitsConfig().getInt("Kit-Cooldown.Cleaner.Cleansing-Wand.I", 45);
    }
  }

  private void applyPoplust(User user, ItemStack stack) {
    Arena arena = (Arena) user.getArena();
    int cooldown = getCleansingCooldown(arena);
    user.setCooldown("cleaner_poplust", cooldown);
    int castTime = (int) Settings.POPLUST_CAST_TIME.getForArenaState(arena);
    user.setCooldown("cleaner_poplust_running", castTime);
    poplustActives.add(arena);
    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> poplustActives.remove(arena), (int) Settings.POPLUST_CAST_TIME.getForArenaState(arena) * 20L);
    new MessageBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_ACTIVATE").asKey().send(user.getPlayer());

    List<String> messages = getPlugin().getLanguageManager().getLanguageListFromKey(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_ACTIVE_ACTION_BAR");
    Player player = user.getPlayer();
    new BukkitRunnable() {
      int tick = 0;
      int messageIndex = 0;

      @Override
      public void run() {
        if(tick % 10 == 0) {
          VersionUtils.sendActionBar(player, messages.get(messageIndex)
            .replace("%number%", String.valueOf(user.getCooldown("cleaner_poplust_running"))));
          for(Player alivePlayer : arena.getPlayersLeft()) {
            if(alivePlayer.equals(player)) {
              continue;
            }
            new TitleBuilder(LANGUAGE_ACCESSOR + "GAME_ITEM_POPLUST_ACTIVE_TITLE").asKey().send(alivePlayer);
          }
          messageIndex++;
          if(messageIndex > messages.size() - 1) {
            messageIndex = 0;
          }
        }
        if(tick >= 20 * 10 || !getPlugin().getArenaRegistry().isInArena(user.getPlayer()) || user.isSpectator()) {
          //reset action bar
          VersionUtils.sendActionBar(player, "");
          cancel();
          return;
        }
        tick++;
      }
    }.runTaskTimer(getPlugin(), 0, 1);

    KitHelper.scheduleAbilityCooldown(stack, user.getPlayer(), castTime, cooldown);
  }

  @EventHandler
  public void onPoplustDamage(EntityDamageByEntityEvent event) {
    if(!(event.getDamager() instanceof Player)) {
      return;
    }
    Player damager = (Player) event.getDamager();
    if(getPlugin().getArenaRegistry().getArena(damager) == null) {
      return;
    }
    User user = getPlugin().getUserManager().getUser(damager);
    if(!poplustActives.contains((Arena) user.getArena()) || user.isSpectator() || !CreatureUtils.isEnemy(event.getEntity())) {
      return;
    }
    LivingEntity entity = (LivingEntity) event.getEntity();
    entity.damage(POP_DAMAGE, damager);
    XSound.BLOCK_LAVA_POP.play(user.getPlayer());
    VersionUtils.sendParticles("LAVA", user.getArena().getPlayers(), entity.getLocation(), 20);
  }

  private enum Settings {
    PASSIVE_POP_COUNT(1, 2, 3), CLEANSING_POP_COUNT(10, 20, 25), POPLUST_CAST_TIME(0, 5, 7);

    private final double earlyValue;
    private final double midValue;
    private final double lateValue;

    Settings(double earlyValue, double midValue, double lateValue) {
      this.earlyValue = earlyValue;
      this.midValue = midValue;
      this.lateValue = lateValue;
    }

    public double getForArenaState(Arena arena) {
      switch(KitSpecifications.getTimeState(arena)) {
        case LATE:
          return lateValue;
        case MID:
          return midValue;
        case EARLY:
        default:
          return earlyValue;
      }
    }
  }

}
