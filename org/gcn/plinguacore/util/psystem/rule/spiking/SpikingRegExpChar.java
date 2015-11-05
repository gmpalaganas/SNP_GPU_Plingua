package org.gcn.plinguacore.util.psystem.rule.spiking;

public enum SpikingRegExpChar{
    
    REGEXP_A           ('a', "101") ,
    REGEXP_STAR        ('*', "100"),
    REGEXP_PLUS        ('+',"010"),
    REGEXP_LPAREN      ('(',"0111100"),
    REGEXP_RPAREN      (')',"0111101"),
    REGEXP_1           ('1',"001"),
    REGEXP_2           ('2',"000"),
    REGEXP_3           ('3',"1111"),
    REGEXP_4           ('4',"1101"),
    REGEXP_5           ('5',"1100"),
    REGEXP_6           ('6',"0110"),
    REGEXP_7           ('7',"11101"),
    REGEXP_8           ('8',"11100"),
    REGEXP_9           ('9',"01110"),
    REGEXP_0           ('0',"011111");

    private final char regExpChar;
    private final String encoded;

    private SpikingRegExpChar(char regExpChar, String encoded){
        this.regExpChar = regExpChar;
        this.encoded = encoded;
    }

    public char regExpChar(){
        return regExpChar;
    }

    public String encoded(){
        return encoded;
    }
 

}

