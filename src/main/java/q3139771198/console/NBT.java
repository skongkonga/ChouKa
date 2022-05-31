package q3139771198.console;

import com.comphenix.protocol.utility.StreamSerializer;

public class NBT {

    public static StreamSerializer streamSerializer;
    public static void init(){
        streamSerializer = new StreamSerializer();
    }
}
