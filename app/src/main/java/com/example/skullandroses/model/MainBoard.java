package com.example.skullandroses.model;

import android.animation.Animator;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.example.skullandroses.observable.BoardProducer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainBoard {
    static int objectCount = 0;
    final GridLayout board;
    private final BoardProducer boardProducer;
    private final ArrayList<ImageView> allImages;


    public MainBoard(GridLayout board) {
        if (objectCount > 0) throw new AssertionError("Board cant be made one Time");
        this.board = board;
        this.boardProducer = new BoardProducer();
        allImages = new ArrayList<>();
        putAllImages();


    }

    private void putAllImages() {
        for (int i = 0; i < board.getChildCount(); i++) {
            allImages.add((ImageView) board.getChildAt(i));
        }
    }


    public List<ImageView> getAllImages() {

        return Collections.unmodifiableList(allImages);
    }


    public void clearBoard() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < size(); i++) {
            allImages.get(i).animate().alpha(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    atomicInteger.incrementAndGet();
                    if (atomicInteger.get() == size()) {
                        Log.d("ABRAHAM", "done ");
                        boardProducer.sendMessage(true, BoardProducer.ACTIONS.CLEAR);

                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public void registerListener(BoardProducer.BoardActivityListener boardActivityListener) {
        boardProducer.registerListener(boardActivityListener);
    }


    public void unregisterListener() {
        boardProducer.unregisterAllListeners();
    }


    int size() {
        return board.getChildCount();
    }


}
