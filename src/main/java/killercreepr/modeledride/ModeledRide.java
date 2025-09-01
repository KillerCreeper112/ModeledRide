package killercreepr.modeledride;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class ModeledRide extends JavaPlugin implements Listener {
    protected final Collection<String> locked = new HashSet<>();

    @Override
    public void onEnable() {
        reloadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        Entity e = event.getRightClicked();

        ModelResult result = getNearestModelResult(e);
        if(result == null) return;
        ActiveModel model = result.model();
        for (String seatBoneID : result.seatBones()) {
            ModelBone bone = model.getBone(seatBoneID).orElseThrow();
            Mount mount = bone.getBoneBehavior(BoneBehaviorTypes.MOUNT).orElseThrow();
            if(!mount.canMountMore()) continue;
            model.getMountManager().orElseThrow().mountPassenger(
                mount, p, (entity, mount1) -> new RideMountController(entity, mount1, (activeModel) -> locked.contains(activeModel.getBlueprint().getName()))
            );
            break;
        }
    }

    public ModelResult getNearestModelResult(Entity e){
        for(Entity check : e.getWorld().getNearbyEntities(e.getLocation(), 32D, 32D, 32D, filter ->{
            return !filter.equals(e) && ModelEngineAPI.getModeledEntity(filter) != null;
        })){
            ModelResult result = getModelResult(e, ModelEngineAPI.getModeledEntity(check));
            if(result == null) continue;
            return result;
        }
        return null;
    }

    public ModelResult getModelResult(@NotNull Entity e, ModeledEntity model){
        for (ActiveModel value : model.getModels().values()) {
            ModelResult result = getModelResult(e, value);
            if(result != null) return result;
        }
        return null;
    }

    public ModelResult getModelResult(@NotNull Entity e, ActiveModel model){
        UUID uuid = e.getUniqueId();
        for(ModelBone bone : model.getBones().values()){
            SubHitbox hitbox = bone.getBoneBehavior(BoneBehaviorTypes.SUB_HITBOX).orElse(null);
            if(hitbox == null) continue;
            if(!uuid.equals(hitbox.getHitboxEntity().getUniqueId())) continue;

            if(bone.getParent() == null) continue;
            ModelBone parent = bone.getParent();
            List<String> seats = new ArrayList<>();
            for (ModelBone child : parent.getChildren().values()) {
                if(child.getBoneBehavior(BoneBehaviorTypes.MOUNT).isEmpty()) continue;
                seats.add(child.getBoneId());
            }
            if(seats.isEmpty()) return null;
            return new ModelResult(seats, model);
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (command.getName().toLowerCase()){
            case "modeledride" ->{
                String action = args.length > 0 ? args[0].toLowerCase() : "";
                switch (action){
                    case "lock", "unlock", "togglelock" ->{
                        String modelID = args.length > 1 ? args[1].toLowerCase() : "";
                        if(ModelEngineAPI.getBlueprint(modelID) == null){
                            sender.sendMessage("Model " + modelID + " not found.");
                            return true;
                        }

                        boolean lock;
                        if(action.equalsIgnoreCase("lock")) lock = true;
                        else if(action.equalsIgnoreCase("unlock")) lock = false;
                        else lock = !locked.contains(modelID);

                        if(lock){
                            locked.add(modelID);
                            sender.sendMessage(modelID + " has been locked.");
                        }else{
                            locked.remove(modelID);
                            sender.sendMessage(modelID + " has been unlocked.");
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        switch (command.getName().toLowerCase()){
            case "modeledride" ->{
                switch (args.length){
                    case 1 ->{
                        list.addAll(List.of(
                            "lock", "unlock", "togglelock"
                        ));
                    }
                    case 2 ->{
                        list.addAll(ModelEngineAPI.getAPI().getModelRegistry().getKeys());
                    }
                }
            }
        }
        return list;
    }
}
