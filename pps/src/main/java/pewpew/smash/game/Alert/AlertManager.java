package pewpew.smash.game.Alert;

import pewpew.smash.game.overlay.OverlayManager;

public class AlertManager {
    private static AlertManager instance;

    public static AlertManager getInstance() {
        if (instance == null) {
            instance = new AlertManager();
        }
        return instance;
    }

    public void showDisconnectAlert() {
        AlertOverlay alert = new AlertOverlay(
                "Disconnected",
                "Lost connection to the server");
        OverlayManager.getInstance().push(alert);
    }

    public void showServerCrashAlert() {
        AlertOverlay alert = new AlertOverlay(
                "Server Error",
                "The server has stopped responding");
        OverlayManager.getInstance().push(alert);
    }

    public void showAlert(String title, String message) {
        AlertOverlay alert = new AlertOverlay(title, message);
        OverlayManager.getInstance().push(alert);
    }
}
