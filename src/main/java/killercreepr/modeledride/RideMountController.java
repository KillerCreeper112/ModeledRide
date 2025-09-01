package killercreepr.modeledride;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.impl.AbstractMountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import org.bukkit.entity.Entity;

import java.util.function.Function;

public class RideMountController extends AbstractMountController {
    protected final Function<ActiveModel,Boolean> locked;
    public RideMountController(Entity entity, Mount mount, Function<ActiveModel,Boolean> locked) {
        super(entity, mount);
        this.locked = locked;
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
