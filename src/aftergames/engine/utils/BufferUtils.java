package aftergames.engine.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author KiQDominaN
 */
public class BufferUtils {

    public static ByteBuffer create_byte_buffer(int size) {
        ByteBuffer buff = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());

        return buff;
    }

    public static IntBuffer create_int_buffer(int size) {
        IntBuffer buff = create_byte_buffer(size * 4).asIntBuffer();

        return buff;
    }

    public static FloatBuffer create_float_buffer(int size) {
        FloatBuffer buff = create_byte_buffer(size * 4).asFloatBuffer();

        return buff;
    }
}
