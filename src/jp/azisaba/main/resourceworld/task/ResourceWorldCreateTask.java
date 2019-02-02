package jp.azisaba.main.resourceworld.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import jp.azisaba.main.resourceworld.RecreateWorld;
import jp.azisaba.main.resourceworld.ResourceWorld;
import jp.azisaba.main.resourceworld.utils.TimeCalculateManager;
import net.md_5.bungee.api.ChatColor;

public class ResourceWorldCreateTask {

	private ResourceWorld plugin;

	private List<RecreateWorld> worldList = new ArrayList<RecreateWorld>();

	private BukkitTask task;

	public ResourceWorldCreateTask(ResourceWorld plugin, List<RecreateWorld> worldList) {
		this.plugin = plugin;
		this.worldList = worldList;
	}

	public void runTask() {

		if (plugin.config.logInConsole) {
			plugin.getLogger().info("次の時刻確認タスクを " + (getWaitTick() / 2) + " tick(s) 後に実行します。");
		}

		task = getTask().runTaskLater(plugin, getWaitTick() / 2);
	}

	public void stopTask() {
		if (task != null) {
			task.cancel();
		}
	}

	private int getWaitTick() {
		long nextWarn = TimeCalculateManager.getNextRecreate();
		long now = System.currentTimeMillis();

		double distance = (double) (nextWarn - now);
		double seconds = distance / 1000;

		return (int) (seconds * 20);
	}

	private BukkitRunnable getTask() {
		return new BukkitRunnable() {
			public void run() {

				if (TimeCalculateManager.getNextRecreate() - System.currentTimeMillis() > 500) {

					if (plugin.config.logInConsole) {
						plugin.getLogger()
								.info("次の時刻確認タスクは " + (getWaitTick() / 2) + " tick(s) 後に実行します。");
					}
					task = getTask().runTaskLater(plugin, getWaitTick() / 2);
					return;
				}

				for (RecreateWorld world : worldList) {

					boolean b = plugin.recreateResourceWorld(world);

					if (b && plugin.config.logInConsole) {
						plugin.getLogger().info(world.getWorldName() + "の生成に成功。");
						Bukkit.broadcastMessage(
								ChatColor.YELLOW + "[" + ChatColor.GREEN + "再生成システム" + ChatColor.YELLOW + "] "
										+ ChatColor.RED + world.getWorldName() + ChatColor.GREEN + " の再生成に成功！");
					}

					//					if (world.getPortalName() != null) {
					//						b = plugin.connectPortal(world.getPortalName(), world.getWorldName());
					//					}
				}

				task = getTask().runTaskLater(plugin, 20 * 60 * 60);
			}
		};
	}
}