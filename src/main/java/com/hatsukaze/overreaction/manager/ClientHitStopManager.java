package com.hatsukaze.overreaction.manager;

import com.zigythebird.playeranim.animation.PlayerAnimationController;

public class ClientHitStopManager {
    private static float stopTimer = 0f;
    private static boolean active = false;

    public static void start(PlayerAnimationController controller, float duration) {
        stopTimer = duration;
        active = true;
        controller.pause();
    }

    public static void tick(PlayerAnimationController controller) {
        if (!active) return;
        stopTimer -= 0.05f;
        if (stopTimer <= 0f) {
            active = false;
            stopTimer = 0f;
            controller.unpause();
        }
    }

    public static boolean isActive() {
        return active;
    }
}