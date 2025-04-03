package edu.uga.cs.worldquiz;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * A class that detect swipes.
 */
public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    /**
     * Gesture listener for swipes.
     * @param context The context used to create GestureDetector.
     */
    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    /**
     * Called when the view is touched.
     * @param v The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *        the event.
     * @return true if the gesture detector correctly handled the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * Listens for swipes.
     */
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        /**
         * Returns true when the user touches.
         * @param e The down motion event.
         * @return true on touch event, false otherwise.
         */
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * Returns true when the user swipes.
         * @param e1 The first down motion event that started the fling. A {@code null} event
         *           indicates an incomplete event stream or error state.
         * @param e2 The move motion event that triggered the current onFling.
         * @param velocityX The velocity of this fling measured in pixels per second
         *              along the x axis.
         * @param velocityY The velocity of this fling measured in pixels per second
         *              along the y axis.
         * @return true if a swipe was detected, false otherwise.
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX < 0) {
                        onSwipeLeft(); // left swipe
                    } else {
                        onSwipeRight(); // right swipe
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Left swipe.
     */
    public void onSwipeLeft() {}

    /**
     * Right swipe.
     */
    public void onSwipeRight() {}
}
