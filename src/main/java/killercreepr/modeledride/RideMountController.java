package killercreepr.modeledride;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.impl.AbstractMountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import org.bukkit.entity.Entity;

import java.util.function.Function;

public class RideMountController extends AbstractMountController {
    protected final Function<ActiveModel,Boolean> locked;
    protected final ActiveModel model;
    public RideMountController(Entity entity, Mount mount, Function<ActiveModel,Boolean> locked, ActiveModel model) {
        super(entity, mount);
        this.locked = locked;
        this.model = model;
    }

    public boolean isLocked(){
        return getLocked().apply(model);
    }

    public ActiveModel getModel() {
        return model;
    }

    public Function<ActiveModel, Boolean> getLocked() {
        return locked;
    }

    @Override
    public void updateDriverMovement(MoveController controller, ActiveModel model) {
        if(model.getMountManager().isEmpty()) return;
        if (this.input.isSneak()) {
            if(locked.apply(model)) return;
            model.getMountManager().get().dismountRider(this.entity);
        }
    }

    @Override
    public void updatePassengerMovement(MoveController controller, ActiveModel model) {
        if(model.getMountManager().isEmpty()) return;
        if (this.input.isSneak()) {
            if(locked.apply(model)) return;
            model.getMountManager().get().dismountRider(this.entity);
        }
    }
}
