package com.br.converter.util;

public class MsgSystem {


    public static String sucGet(String objName){
        return String.format("%s encontrado(a) com sucesso", objName);
    }


    public static String errCriteria(String objName){
        return String.format("A busca de \"%s\" não encontrou nenhuma informação para os dados informados", objName);
    }

}
