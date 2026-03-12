package mike.blueprint.util;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Text {

    public static Map<Character, Character> smallCapsToRegular = new HashMap<>();

    static {
        smallCapsToRegular.put('ᴀ', 'a'); // U+1D00
        smallCapsToRegular.put('ʙ', 'b'); // U+0299
        smallCapsToRegular.put('ᴄ', 'c'); // U+1D04
        smallCapsToRegular.put('ᴅ', 'd'); // U+1D05
        smallCapsToRegular.put('ᴇ', 'e'); // U+1D07
        smallCapsToRegular.put('ꜰ', 'f'); // U+A730
        smallCapsToRegular.put('ɢ', 'g'); // U+0262
        smallCapsToRegular.put('ʜ', 'h'); // U+029C
        smallCapsToRegular.put('ɪ', 'i'); // U+026A
        smallCapsToRegular.put('ᴊ', 'j'); // U+1D0A
        smallCapsToRegular.put('ᴋ', 'k'); // U+1D0B
        smallCapsToRegular.put('ʟ', 'l'); // U+029F
        smallCapsToRegular.put('ᴍ', 'm'); // U+1D0D
        smallCapsToRegular.put('ɴ', 'n'); // U+0274
        smallCapsToRegular.put('ᴏ', 'o'); // U+1D0F
        smallCapsToRegular.put('ᴘ', 'p'); // U+1D18
        smallCapsToRegular.put('ꞯ', 'q'); // U+A7AF
        smallCapsToRegular.put('ʀ', 'r'); // U+0280
        smallCapsToRegular.put('ꜱ', 's'); // U+A731
        smallCapsToRegular.put('ᴛ', 't'); // U+1D1B
        smallCapsToRegular.put('ᴜ', 'u'); // U+1D1C
        smallCapsToRegular.put('ᴠ', 'v'); // U+1D20
        smallCapsToRegular.put('ᴡ', 'w'); // U+1D21
        smallCapsToRegular.put('x', 'x');
        smallCapsToRegular.put('ʏ', 'y'); // U+028F
        smallCapsToRegular.put('ᴢ', 'z'); // U+1D22
    }

    public static final Map<Character, Character> regularToSmallCaps = new HashMap<>();

    static {
        regularToSmallCaps.put('a', 'ᴀ'); // U+1D00
        regularToSmallCaps.put('b', 'ʙ'); // U+0299
        regularToSmallCaps.put('c', 'ᴄ'); // U+1D04
        regularToSmallCaps.put('d', 'ᴅ'); // U+1D05
        regularToSmallCaps.put('e', 'ᴇ'); // U+1D07
        regularToSmallCaps.put('f', 'ꜰ'); // U+A730
        regularToSmallCaps.put('g', 'ɢ'); // U+0262
        regularToSmallCaps.put('h', 'ʜ'); // U+029C
        regularToSmallCaps.put('i', 'ɪ'); // U+026A
        regularToSmallCaps.put('j', 'ᴊ'); // U+1D0A
        regularToSmallCaps.put('k', 'ᴋ'); // U+1D0B
        regularToSmallCaps.put('l', 'ʟ'); // U+029F
        regularToSmallCaps.put('m', 'ᴍ'); // U+1D0D
        regularToSmallCaps.put('n', 'ɴ'); // U+0274
        regularToSmallCaps.put('o', 'ᴏ'); // U+1D0F
        regularToSmallCaps.put('p', 'ᴘ'); // U+1D18
        regularToSmallCaps.put('q', 'ꞯ'); // U+A7AF (closest approximation)
        regularToSmallCaps.put('r', 'ʀ'); // U+0280
        regularToSmallCaps.put('s', 'ꜱ'); // U+A731
        regularToSmallCaps.put('t', 'ᴛ'); // U+1D1B
        regularToSmallCaps.put('u', 'ᴜ'); // U+1D1C
        regularToSmallCaps.put('v', 'ᴠ'); // U+1D20
        regularToSmallCaps.put('w', 'ᴡ'); // U+1D21
        regularToSmallCaps.put('x', 'x'); // No proper small cap; using lowercase
        regularToSmallCaps.put('y', 'ʏ'); // U+028F
        regularToSmallCaps.put('z', 'ᴢ'); // U+1D22
    }

    public static LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacy('&');

    public static Component translate(String s) {
        if(s.contains("&") || s.contains("§")) {
            s = s.replace("§", "&");
            final Component text = legacyComponentSerializer.deserialize(s);
            final String miniMessage = MiniMessage.miniMessage().serialize(text);
            return MiniMessage.miniMessage().deserialize(miniMessage).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }
        return MiniMessage.miniMessage().deserialize(s).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static List<Component> translate(List<String> strings) {
        final List<Component> text = new ArrayList<>();
        if(strings == null) return text;
        for(String s : strings) {
            text.add(translate(s));
        }
        return text;
    }

    public static String toRegular(String text) {
        StringBuilder builder = new StringBuilder();
        for(char c : text.toCharArray()) {
            if(smallCapsToRegular.containsKey(c)) {
                builder.append(smallCapsToRegular.get(c));
            }else{
                builder.append(c);
            }
        }
        return builder.toString().replace("null", "");
    }

    public static String smallCaps(String text) {
        StringBuilder builder = new StringBuilder();
        for(char c : text.toCharArray()) {
            if(regularToSmallCaps.containsKey(c)) {
                char value = regularToSmallCaps.get(c);
                builder.append(value);
            }else{
                builder.append(c);
            }
        }
        return builder.toString().replace("null", "");
    }

    public static String center(String text) {
        final int len = text.length() / 2;
        final StringBuilder newText = new StringBuilder();
        newText.repeat(' ', len).append(text);
        return newText.toString();
    }

}