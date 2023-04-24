package ch.epfl.javions.gui;


import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Download and store the requested tiles
 */
public class TileManager {
    private final static int MAX_TILES_CACHE_MEMORY = 100;
    private final static String PROTOCOL = "https";
    private Path pathTiles;
    private String serverTiles;

    private final Map<TileId, Image> tiles;

    public record TileId(int zoom, int x, int y){}

    /**
     * Constructor of the TileManager
     * @param pathTiles path where the tiles are saved
     * @param severTiles server that host the tiles
     */
    public TileManager(Path pathTiles, String severTiles){
        this.pathTiles = pathTiles;
        this.serverTiles = severTiles;
        tiles = new LinkedHashMap<TileId, Image>(MAX_TILES_CACHE_MEMORY, 1f,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<TileId, Image> eldest) {
                return size() > MAX_TILES_CACHE_MEMORY;
            }
        };
    }

    private boolean isCacheFull(){
        int size = tiles.size();
        if (size < MAX_TILES_CACHE_MEMORY){
            System.out.println("Warning: there is more than "+ MAX_TILES_CACHE_MEMORY +" in the cache, it should never happens");
            return true;
        }
        return size == MAX_TILES_CACHE_MEMORY;
    }
    private void storeInCache(TileId tileId, byte[] bytesTile){
        InputStream streamBytesTile = new ByteArrayInputStream(bytesTile);
        Image tile = new Image(streamBytesTile);
        tiles.put(tileId, tile);
    }
    private Path pathTileDir(TileId tileId){
        Path pathZoom = Paths.get(Integer.toString(tileId.zoom));
        Path pathX = Paths.get(Integer.toString(tileId.x));
        return pathTiles.resolve(pathZoom).resolve(pathX);
    }

    private Path pathTileFile(TileId tileId){
        Path pathY = Paths.get(tileId.y + ".png");
        return pathTileDir(tileId).resolve(pathY);
    }

    private void createDirectoryTile(TileId tileId) throws IOException {
        Path pathTileDir = pathTileDir(tileId);
        if (!Files.exists(pathTileDir)){
            Files.createDirectories(pathTileDir);
        }
    }

    /**
     * Store the bytes on the disk
     * @param tileId
     * @param bytesTile
     * @throws IOException
     */
    private void storeOnDisk(TileId tileId, byte[] bytesTile) throws IOException {
        createDirectoryTile(tileId);
        System.out.println(pathTileFile(tileId));

        try (OutputStream o = new FileOutputStream(pathTileFile(tileId).toFile())){
            o.write(bytesTile);
        }
    }

    /**
     * Store the bytes on the disk and in the cache
     * @param tileId
     * @param bytesTile
     * @throws IOException
     */

    private void store(TileId tileId, byte[] bytesTile) throws IOException {
        storeInCache(tileId, bytesTile);
        storeOnDisk(tileId, bytesTile);
    }

    /**
     * The url from a tileId with this format: PROTOCO://host/zoom/x/y.png
     * @param tileId The tile id
     * @return the url to the image of the tile
     */
    private URL tileURL(TileId tileId) {
        try {
            return new URL(PROTOCOL,serverTiles, "/" + tileId.zoom() + "/" + tileId.x() + "/" + tileId.y() + ".png");
        }catch (MalformedURLException e){
            Preconditions.checkArgument(false);
            return null;
        }
    }

    /**
     * Download the image of an tileId
     * @param tileId
     * @return (byte[]) the bytes of the image downloaded
     * @throws IOException
     */
    private byte[] downloadTile(TileId tileId) throws IOException {
        URL tileURL = tileURL(tileId);
        URLConnection c = tileURL.openConnection();
        c.setRequestProperty("User-Agent", "Javions");
        try (InputStream i = c.getInputStream()){
            byte[] bytes = i.readAllBytes();
            return bytes;
        }
    }

    /**
     * Get javafx image from a given tileId
     * @param tileId the tileId wanted
     * @return Image or null if not able to get the image
     */
    public Image imageForTileAt(TileId tileId){
        Objects.requireNonNull(tileId);
        byte[] bytesTile;
        try{
            bytesTile = downloadTile(tileId);
            System.out.println("ho");
            store(tileId, bytesTile);
        }catch (IOException e){
            System.out.println("ha");
            return null;
        }
        return tiles.get(tileId);
    }

    private void load(){
        //TODO load all the images stored from the disk to the cache
    }
}
