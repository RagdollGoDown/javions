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
        Image im1 = tileManager.imageForTileAt(new TileManager.TileId(17, 67927, 0));
        Image im2 = tileManager.imageForTileAt(new TileManager.TileId(16, 67927, 0));

    }
}