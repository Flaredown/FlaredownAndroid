package com.flaredown.flaredownApp.Helpers.AndroidViewAnimation;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by thunter on 26/06/16.
 */
public class ViewAnimationHelper<T extends Enum<T>> {
    private Map<T, AnimationEvents> states = new HashMap<>();
    private T currentView = null;
    private List<PendingAnimation<T>> animationPending = new LinkedList<>();
    private boolean isAnimating = false;

    /**
     * Add state to the view animation helper.
     * @param stateEnum The state enum, used to show the state.
     * @param animationEvents The show and hide events.
     */
    public void addState(T stateEnum, AnimationEvents animationEvents) {
        states.put(stateEnum, animationEvents);
    }

    /**
     * Remove a state from the view animation helper.
     * @param stateEnum The state enum, used to represent a state.
     */
    public void removeState(T stateEnum) {
        states.remove(stateEnum);
    }

    public void changeState(T stateEnum, boolean animate) {
        changeState(stateEnum, animate, null);
    }

    public void changeState(final T stateEnum, final boolean animate, @Nullable final AnimationEndListener animationComplete) {
        if(isAnimating) { // Add to queue and do nothing.
            animationPending.add(new PendingAnimation<T>(stateEnum, animate, animationComplete));
            return;
        }
        isAnimating = true;
        if(stateEnum == currentView) { // No need to animate, notify and run any pending animations.
            if(animationComplete != null)
                animationComplete.addProgress(0);
            runAnimationPending();
            return;
        }

        final AnimationEvents hideAE = (currentView == null) ? null : states.get(currentView);
        final AnimationEvents showAE = states.get(stateEnum);

        final Counter hideProgressCounter = new Counter();
        final Counter showProgressCounter = new Counter();
        final AnimationEndListener showAEL = new AnimationEndListener() {
            @Override
            public void addProgress(int totalProgress) {
                Log.d("TMP", "TMP " + stateEnum.toString());
                showProgressCounter.incrementCounter();
                if(showProgressCounter.getValue() >= totalProgress) {
                    if(animationComplete != null) {
                        animationComplete.addProgress(0);
                    }
                    isAnimating = false;
                    runAnimationPending();
                }
            }
        };

        final AnimationEndListener hideAEL = new AnimationEndListener() {
            @Override
            public void addProgress(int totalProgress) {
                hideProgressCounter.incrementCounter();
                if(hideProgressCounter.getValue() >= totalProgress) {
                    if(showAE.isWaitForHideAnimationComplete())
                        showAE.getShow().start(animate, showAEL);
                }
            }
        };
        currentView = stateEnum;
        if(hideAE != null) {
            hideAE.getHide().start(animate, hideAEL);
        } else {
            hideAEL.addProgress(0);
        }

        if(!showAE.isWaitForHideAnimationComplete()) {
            showAE.getShow().start(animate, showAEL);
        }
    }

    /**
     * If there is an item in the queue then pop from queue and change state.
     */
    private void runAnimationPending() {
        if(animationPending.size() == 0) return;
        PendingAnimation<T> pendingAnimation = animationPending.get(0);
        animationPending.remove(0);
        changeState(pendingAnimation.getStateEnum(), pendingAnimation.isAnimate(), pendingAnimation.getAnimationComplete());
    }


    private static class PendingAnimation <T> {
        private T stateEnum;
        private AnimationEndListener animationComplete;
        private boolean animate;

        public PendingAnimation(T stateEnum, boolean animate, AnimationEndListener animationComplete) {
            this.stateEnum = stateEnum;
            this.animationComplete = animationComplete;
            this.animate = animate;
        }

        public T getStateEnum() {
            return stateEnum;
        }

        public AnimationEndListener getAnimationComplete() {
            return animationComplete;
        }

        public boolean isAnimate() {
            return animate;
        }
    }

    public static class AnimationEvents {
        private Animation show;
        private Animation hide;
        private boolean waitForHideAnimationComplete = true;

        public AnimationEvents(Animation show, Animation hide, boolean waitForHideAnimationComplete) {
            this.show = show;
            this.hide = hide;
            this.waitForHideAnimationComplete = waitForHideAnimationComplete;
        }

        public Animation getShow() {
            return show;
        }

        public Animation getHide() {
            return hide;
        }

        public boolean isWaitForHideAnimationComplete() {
            return waitForHideAnimationComplete;
        }
    }

    public static abstract class Animation {
        private List<View> animatedViews = new LinkedList<>();

        /**
         * Start animation for the current animation sequence.
         * @param animate If there should be an animation or just a instant change.
         */
        public abstract void start(boolean animate, AnimationEndListener animationEndListener);

        /**
         * Called to cancel all animations related to the animation.
         */
        public void cancel() {
            cancelAnimatedViews();
        }

        /**
         * Add a view to the list of views, allowing for an easy cancelable of animations.
         * @param v
         */
        protected void addAnimatedView(View v) {
            animatedViews.add(v);
        }

        /**
         * Remove a view from the list of animated views.
         * @param v
         */
        protected void removeAnimatedView(View v) {
            animatedViews.remove(v);
        }

        /**
         * Stops all animations for the views stored in the animatedViews field.
         */
        private void cancelAnimatedViews() {
            for (View animatedView : animatedViews) {
                animatedView.clearAnimation();
            }
        }
    }

    public interface AnimationEndListener {
        /**
         * Add one to the progress of the animation.
         * @param totalProgress Once the total progress is equal to the progress the animation is
         *                      assumed complete.
         */
        void addProgress(int totalProgress);
    }

    private static class Counter {
        private int counter = 0;

        public Counter() {

        }

        /**
         * Create counter with an initial value.
         * @param initialValue The initial value.
         */
        public Counter(int initialValue) {
            counter = initialValue;
        }

        /**
         * Get the counter's value.
         * @return The counter's value.
         */
        public int getValue() {
            return counter;
        }

        /**
         * Counter's value increments by one.
         */
        public void incrementCounter() {
            counter++;
        }
    }
}
