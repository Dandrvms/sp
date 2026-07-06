package com.ceos.sparkline;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Stop;


import java.util.Random;

public class SparkLine extends StackPane {
    private long lastTimerCall;
    private AnimationTimer timer;
    private static final Random RND = new Random();
    private Tile sparkLineTile;


    public SparkLine() {

        this.setPickOnBounds(true);


        double TILE_SIZE = 500;
        sparkLineTile = TileBuilder.create()
                .skinType(Tile.SkinType.SPARK_LINE)
                .title("SparkLine Tile")
                .unit("°C")
                .gradientStops(new Stop(0, Tile.GREEN),
                        new Stop(0.5, Tile.YELLOW),
                        new Stop(1.0, Tile.RED))
                .strokeWithGradient(true)
                .build();

        sparkLineTile.setMouseTransparent(true);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                if (now > lastTimerCall + 2_000_000_000) {

                    sparkLineTile.setValue(RND.nextDouble() * sparkLineTile.getRange() + sparkLineTile.getMinValue());

                    lastTimerCall = now;
                }

            }

        };


        timer.start();

        this.getChildren().add(sparkLineTile);

    }


}
