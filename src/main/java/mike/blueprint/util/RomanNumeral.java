package mike.blueprint.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RomanNumeral {

    public static final BiMap<String, Integer> numeralMap = HashBiMap.create(Map.of(
            "I", 1,
            "IV", 4,
            "V", 5,
            "IX", 9,
            "X", 10,
            "XL", 40,
            "L", 50,
            "XC", 90
    ));

    //EX "VII" get last I = 1
    public static int fromRoman(String roman) {
        int val = 0;
        int total = 0;
        int len = roman.length()-1;
        char[] arr = roman.toCharArray();
        for(int i = len; i >= 0; i--) {
            final String character = String.valueOf(arr[i]);
            if(!numeralMap.containsKey(character)) continue;
            int numeralValue = numeralMap.get(character);
            if(numeralValue >= val) {
                total += numeralValue;
            }else{
                total -= numeralValue;
            }
            val = numeralValue;
        }
        return total;
    }

    public static String toRoman(int num) {
        final StringBuilder builder = new StringBuilder();
        final Map<Integer, String> numerals = numeralMap.inverse();
        final List<Integer> sorted = numerals.keySet().stream().sorted(Comparator.reverseOrder()).toList();
        while(num > 0) {
            for (int value : sorted) {
                if(num < value) continue;
                num -= value;
                builder.append(numerals.get(value));
                break;
            }
        }
        return builder.toString();
    }

}
