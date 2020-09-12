package com.example.skullandroses;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skullandroses.helper.AnimationHelper;
import com.example.skullandroses.model.MainBoard;
import com.example.skullandroses.observable.BoardProducer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, BoardProducer.BoardActivityListener {
    private final static AnimationHelper animationHelper = new AnimationHelper();
    //const
    private final int[][] winCombo = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {6, 4, 2}};
    //ui components
    private GridLayout mainBoard; // main x and o boaard
    private TextView skullScoreBox, roseScoreBox;
    private Button play;
    private ImageView skull, rose;
    //vars
    private MainBoard mBoard;  //object that includes many methods for board
    private List<ImageView> myBoxes; //short way to access all imageViews from mainBoard
    private int turn = 0;
    private volatile AtomicInteger skullN = new AtomicInteger(0);
    private volatile AtomicInteger roseN = new AtomicInteger(0);
    private boolean enabled = false;
    private boolean allowAction = false;
    private Map<Integer, TYPE> used = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        configureMyToolbar();
        uiAttach();
        boardConfigure();

    }

    private void boardConfigure() {
        mBoard = new MainBoard(mainBoard);
        mBoard.registerListener(this);
        mBoard.clearBoard();
    }

    /**
     * configuring the toolbar if it will not be founded it will throw en exception
     *
     * @throws NullPointerException
     */

    private void configureMyToolbar() throws NullPointerException {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.toolbar_layout);
        actionBar.setDisplayShowTitleEnabled(false);

    }

    private void uiAttach() {
        mainBoard = findViewById(R.id.grid);
        skullScoreBox = getSupportActionBar().getCustomView().findViewById(R.id.skull_score);
        roseScoreBox = getSupportActionBar().getCustomView().findViewById(R.id.roses_score);
        play = getSupportActionBar().getCustomView().findViewById(R.id.play);
        play.setOnClickListener(this);
        skull = getSupportActionBar().getCustomView().findViewById(R.id.imageView);
        rose = getSupportActionBar().getCustomView().findViewById(R.id.imageView10);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.play:
                onPlayButtonClicked();
                break;
        }

    }

    /**
     * game start here
     */
    private void onPlayButtonClicked() {
        beforeStart();


    }

    private void beforeStart() {


        enabled = true;
        mBoard.clearBoard();

        play.setText(getString(R.string.button_end));

    }

    /**
     * taking a current imageView from board
     * to manipulate with them
     *
     * @param view the imageView
     */
    public void taskDo(View view) {
        final int index = Integer.parseInt(view.getTag().toString());
        Log.d("NAR", "" + allowAction + " " + enabled + " " + used.containsKey(index));
        if (!allowAction || !enabled || used.containsKey(index)) return;


        try {
            Log.d("NAR", "FULLER");
            if (turn % 2 == 0) {

                skull.setBackgroundResource(R.drawable.choose);
                rose.setBackground(null);
                view.animate().alpha(1);

                ((ImageView) view).setImageResource(R.drawable.skulls);

                used.put(index, TYPE.SKULL);
            } else {
                rose.setBackgroundResource(R.drawable.choose);
                skull.setBackground(null);
                view.animate().alpha(1);
                ((ImageView) view).setImageResource(R.drawable.rose);
                used.put(index, TYPE.ROSE);
            }
            animationHelper.basicSet(view);
            mainLogicCheck(index);

            turn++;


        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private void mainLogicCheck(int index) {

        new Thread(() -> {
            TYPE currentType = used.get(index);
            used.forEach((k, v) -> {
                Log.d("JOHN", k + " " + v.toString());
            });
            for (int i = 0; i < winCombo.length; i++) {

                Log.d("abraham", used.get(index).toString());


                if (used.containsKey(winCombo[i][0])
                        && used.containsKey(winCombo[i][1])
                        && used.containsKey(winCombo[i][2])) {
                    int check = 0;

                    ArrayList<ImageView> prefWinners = new ArrayList<>();
                    for (int j = 0; j < winCombo[i].length; j++) {
                        if (used.get(winCombo[i][j]) == currentType) {
                            check++;

                            prefWinners.add(mBoard.getAllImages().get(winCombo[i][j]));
                        } else {
                            break;
                        }
                    }
                    if (check == 3) {

                        switch (currentType) {
                            case SKULL:
                                skullN.incrementAndGet();


                                break;

                            case ROSE:
                                roseN.incrementAndGet();


                                break;
                        }

                        runOnUiThread(new Thread(() -> {
                            prefWinners.forEach(animationHelper::basicSetGroup);

                            skullScoreBox.setText(String.valueOf(skullN.get()));
                            roseScoreBox.setText(String.valueOf(roseN.get()));
                            enabled = false;
                            allowAction = false;
                            used.clear();
                            play.setText(getString(R.string.button_start));


                        }));
                        break;
                    }


                }
            }


        }).start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBoard.unregisterListener();
    }

    @Override
    public void onFinish(boolean isFinished, BoardProducer.ACTIONS action) {
        switch (action) {
            case CLEAR:
                allowAction = true;
                break;

        }

    }

    //enum
    private enum TYPE {
        SKULL, ROSE
    }
}