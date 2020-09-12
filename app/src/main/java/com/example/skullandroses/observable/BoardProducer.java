package com.example.skullandroses.observable;

public class BoardProducer extends BaseObservable<BoardProducer.BoardActivityListener> {
    /**
     * unregistering all listeners
     */
    public void unregisterAllListeners() {
        getListeners().forEach(this::unregisterListener);
    }

    /**
     * send a message whether the action is done
     *
     * @param isFinished
     * @param actions
     */

    public void sendMessage(boolean isFinished, ACTIONS actions) {
        getListeners().forEach(e -> {
            e.onFinish(isFinished, actions);
        });
    }

    public enum ACTIONS {
        CLEAR,
    }

    public interface BoardActivityListener {
        void onFinish(boolean isFinished, ACTIONS action);

    }
}
