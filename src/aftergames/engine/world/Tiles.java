package aftergames.engine.world;

import aftergames.engine.EngineAPI;
import aftergames.engine.utils.geom.Rect;
import aftergames.engine.render.Renderer;
import aftergames.engine.render.Texture;

/**
 *
 * @author KiQDominaN
 */
public class Tiles {

    protected final Texture texture;
    protected Rect[] tiles;
    protected final int[][] map;
    protected final int columns, rows, tile_width, tile_height;
    protected float x, y;

    public Tiles(Texture texture, int columns, int rows, int tile_width, int tile_height) {
        this.columns = columns;
        this.rows = rows;
        this.tile_width = tile_width;
        this.tile_height = tile_height;

        this.texture = texture;
        map = new int[rows][columns];

        int tiles_num = (texture.width / tile_width) * (texture.height / tile_height) + 1;
        tiles = new Rect[tiles_num];

        int tile = 1;
        float u, v, u2, v2;
        for (float locY = 0; locY < texture.height; locY += tile_height) {
            for (float locX = 0; locX < texture.width; locX += tile_width) {
                u = locX / texture.width;
                v = locY / texture.height;
                u2 = u + (float) tile_width / texture.width;
                v2 = v + (float) tile_height / texture.height;

                tiles[tile++] = this.texture.getRegion(u, v, u2, v2);
            }
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setMap(int[] map) {
        int tile = 0;

        for (int locY = 0; locY < columns; locY++)
            for (int locX = 0; locX < rows; locX++)
                setTile(locX, locY, map[tile++]);
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getTileWidth() {
        return tile_width;
    }

    public int getTileHeight() {
        return tile_height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return rows * tile_width;
    }

    public int getHeight() {
        return columns * tile_height;
    }

    public int getTile(int x, int y) {
        return map[x][y];
    }

    private void setTile(int x, int y, int tile) {
        map[x][y] = tile;
    }

    protected Rect quad = new Rect(0, 0, 0, 0);

    public void render() {
        int tileIndex;

        float cX = EngineAPI.getWorld().getCamera().getWorldX();
        float cY = EngineAPI.getWorld().getCamera().getWorldY();
        float cW = EngineAPI.getWorld().getCamera().getWidth();
        float cH = EngineAPI.getWorld().getCamera().getHeight();

        float tX;
        float tY = getY();
        int tW = getTileWidth();
        int tH = getTileHeight();

        Renderer.setTexture(texture);
        Renderer.begin(Renderer.R_QUAD);
        {
            for (int row = 0; row < getRows(); row++, tY += tH) {
                if (tY < cY - tH) continue;
                if (tY > cY + cH) break;

                tX = getX();

                for (int column = 0; column < getColumns(); column++, tX += tW) {
                    if (tX < cX - tW) continue;
                    if (tX > cX + cW) break;

                    if ((tileIndex = getTile(row, column)) == 0) continue;

                    Renderer.setTexCoords(tiles[tileIndex].x, tiles[tileIndex].y);
                    Renderer.setVertexCoords(tX, tY);
                    Renderer.setTexCoords(tiles[tileIndex].w, tiles[tileIndex].y);
                    Renderer.setVertexCoords(tX + tW, tY);
                    Renderer.setTexCoords(tiles[tileIndex].w, tiles[tileIndex].h);
                    Renderer.setVertexCoords(tX + tW, tY + tH);
                    Renderer.setTexCoords(tiles[tileIndex].x, tiles[tileIndex].h);
                    Renderer.setVertexCoords(tX, tY + tH);
                }
            }
        }
        Renderer.end();
    }

    public Texture getTexture() {
        return texture;
    }
}