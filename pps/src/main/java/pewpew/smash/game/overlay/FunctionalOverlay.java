package pewpew.smash.game.overlay;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionalOverlay {
    String[] value();
}
