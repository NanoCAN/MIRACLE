package org.nanocan.rppa.scanner

class Antibody {

    String name
    Double concentration
    String concentrationUnit
    String comments

    static searchable = true

    static constraints = {
        name()
        concentration nullable:true
        concentrationUnit inList: ["µg/ml", "mM"], nullable:true
        comments nullable: true
    }

    String toString()
    {
       if(concentration != null && concentrationUnit!= null)
        (name + " " + concentration.toString() + " " + concentrationUnit)

       else
        name
    }
}
