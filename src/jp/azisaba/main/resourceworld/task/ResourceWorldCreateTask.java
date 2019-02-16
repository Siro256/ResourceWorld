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

		long wait = getWaitTicks() / 2;

		if (plugin.config.logInConsole) {
			plugin.getLogger().info("次の時刻確認タスクを " + wait + " tick(s) 後に実行します。");
		}

		task = getTask().runTaskLater(plugin, wait);
	}

	public void stopTask() {
		if (task != null) {
			task.cancel();
		}
	}

	private long getWaitTicks() {
		long nextRecreate = TimeCalculateManager.getNextRecreate();
		long now = System.currentTimeMillis();

		long after = nextRecreate - now;
		long seconds = (long) Math.ceil(after / 1000L);

		return (long) (seconds * 20);
	}

	private BukkitRunnable getTask() {
		return new BukkitRunnable() {
			public void run() {

				if (TimeCalculateManager.getNextRecreate() - System.currentTimeMillis() > 500) {

					long wait = getWaitTicks() / 2;

					if (plugin.config.logInConsole) {
						plugin.getLogger()
								.info("次の時刻確認タスクは " + wait + " tick(s) 後に実行します。 (");
					}
					task = getTask().runTaskLater(plugin, wait);
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
				}

				task = getTask().runTaskLater(plugin, 20 * 60 * 60);
			}
		};
	}
}
