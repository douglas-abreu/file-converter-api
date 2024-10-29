package com.br.converter.util;

public class FormatUtil {


    public static String stringify(String value){
        return "\"".concat(removeMultipleWhitespace(value)).concat("\"");
    }

    public static String singleStringify(String value){
        return "'".concat(removeMultipleWhitespace(value)).concat("'");
    }

    public static String removeMultipleWhitespace(String value){
        return value.replaceAll("^ +| +$|( )+", "$1");
    }

}
