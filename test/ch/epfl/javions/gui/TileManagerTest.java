package ch.epfl.javions.gui;

import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class TileManagerTest {

    @Test
    void imageForTileAt() {
        TileManager tileManager = new TileManager(Paths.get("out/tiles"),"tile.openstreetmap.org");
        Image im = tileManager.imageForTileAt(new TileManager.TileId(17, 67927, 46357));
        System.out.println(im);
    }
}