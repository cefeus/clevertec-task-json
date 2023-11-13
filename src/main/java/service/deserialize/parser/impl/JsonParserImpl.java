package service.deserialize.parser.impl;

import exception.JsonSerializationException;
import service.deserialize.parser.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static util.constants.StringLiterals.COLON_CHAR;
import static util.constants.StringLiterals.COMMA_CHAR;
import static util.constants.StringLiterals.LEFT_FIGURE_BRACKET_CHAR;
import static util.constants.StringLiterals.LEFT_STRAIGHT_BRACKET_CHAR;
import static util.constants.StringLiterals.QUOTATION_MARK_CHAR;
import static util.constants.StringLiterals.RIGHT_FIGURE_BRACKET_CHAR;
import static util.constants.StringLiterals.RIGHT_STRAIGHT_BRACKET_CHAR;

public class JsonParserImpl implements JsonParser {

    int index;
    public Map<String, Object> deserializeRecursively(String input)  {
       try {
        if( input == null || (!(input.charAt(0) == LEFT_FIGURE_BRACKET_CHAR && input.charAt(input.length()-1) == RIGHT_FIGURE_BRACKET_CHAR)) ){ throw new Exception();}

        Map<String, Object> map = new HashMap<>();
        input = input.substring(1, input.length() - 1);
        String key;
        index = 0;

        while(!input.isEmpty()) {
            char sw = input.charAt(index);

                if(sw == QUOTATION_MARK_CHAR) {
                    removeWhiteSpaces(input);
                    key = getString(input);
                    index++;
                    removeWhiteSpaces(input);
                    if (input.charAt(index) != COLON_CHAR){throw new Exception();}
                    removeWhiteSpaces(input);
                    input = input.substring(index + 1);
                    index = 0;
                    map.put(key, getValue(input));
                    if(input.length() >= index + 1) {
                        input = input.substring(index + 1);
                        index = 0;
                        if(!input.isEmpty()) {
                            if (input.charAt(index) == COMMA_CHAR) {index++;
                            removeWhiteSpaces(input);}
                        }
                    }
                    else
                        input = input.substring(index);
                }
                else {
                    throw new Exception();
                }
            }
        return map;

        } catch (Exception e) {
            throw new JsonSerializationException("Invalid json", e);
        }
    }

    private Object getValue(String input)  {
        switch (input.charAt(index)) {
            case '{' : {
                int i = findCurlyBracketIndex(input);
                Object rec = deserializeRecursively(input.substring(0, i + 1));
                index = i;
                return rec;
            }
            case '[' : {
                int i = findStraightBracketIndex(input);
                Object rec = getArray(input.substring(0, i + 1));
                index = i;
                return rec;
            }
            case '\"' : {
                return getString(input);
            }
            default : {
                return getObj(input);
            }
        }
    }

    private void removeWhiteSpaces(String input) {
        while(input.charAt(index) == ' ') index++;
    }

    private Integer findCurlyBracketIndex(String input) {
        int i = index;
        int bracketCount = 0;
        while(i < input.length()) {
            if(input.charAt(i) == RIGHT_FIGURE_BRACKET_CHAR) {
                bracketCount--;
                if (bracketCount == 0)
                    return i;
            }
            if(input.charAt(i) == LEFT_FIGURE_BRACKET_CHAR)
                bracketCount++;
            i++;
        }
        return i;
    }

    private Integer findStraightBracketIndex(String input) {
        int i = index;
        int bracketCount = 0;
        while(i < input.length()) {
            if(input.charAt(i) == RIGHT_STRAIGHT_BRACKET_CHAR) {
                bracketCount--;
                if (bracketCount == 0)
                    return i;
            }
            if(input.charAt(i) == LEFT_STRAIGHT_BRACKET_CHAR)
                bracketCount++;
            i++;
        }
        return i;
    }

    private Object getObj(String input) {
        StringBuilder sb = new StringBuilder();
        int i = index;
        while(i < input.length()) {
            if(input.charAt(i) == COMMA_CHAR)
                if(i+1 <=input.length() && (input.charAt(i+1) == QUOTATION_MARK_CHAR))
                    return sb.toString();
            sb.append(input.charAt(i));
            i++;
        }
        index = i;
        return sb.toString();
    }

    private String getString(String input) {
        StringBuilder sb = new StringBuilder();
        int doubleQuoteCount = 0;
        while (index < input.length() && doubleQuoteCount < 2) {
            if (input.charAt(index) == QUOTATION_MARK_CHAR) {
                doubleQuoteCount++;
            } else {
                sb.append(input.charAt(index));
            }
            if (doubleQuoteCount == 2) {
                break;
            }
            index++;
        }
        return sb.toString();
    }

    private List<Object> getArray(String input) {
        input = input.substring(1, input.length()-1);
        String buffer = input;
        int ind = index;
        List<String> parts = new ArrayList<>();
        if(input.charAt(0) == LEFT_FIGURE_BRACKET_CHAR) {
            while(index < buffer.length()) {
                if(buffer.charAt(0) == COMMA_CHAR){
                    index = 0;
                    removeWhiteSpaces(input);
                    buffer = buffer.substring(index + 1);
                    index = 0;
                }
                index = findCurlyBracketIndex(buffer);
                 if(index < buffer.length()) {parts.add(buffer.substring(0, ++index)); }
                 else {
                     parts.add(buffer.substring(0, index));
                     break;}
                buffer = buffer.substring(index);
            }
        }
        index = ind;
        return  parts.stream().map(s -> {
            Object o = getValue(s);
            index = 0;
            return o;
        }).collect(Collectors.toList());
    }
}
