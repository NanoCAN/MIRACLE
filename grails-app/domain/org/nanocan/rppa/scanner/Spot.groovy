package org.nanocan.rppa.scanner

import org.nanocan.rppa.layout.LayoutSpot

class Spot {

    double FG
    double BG
    double signal
    int x
    int y
    int diameter
    int flag
    int block
    int row
    int col

    static mapping = {
        slide index: 'spot_idx'
        block index: 'spot_idx'
        col index:  'spot_idx'
        row index: 'spot_idx'
    }

    static belongsTo = [slide: Slide, layoutSpot: LayoutSpot]

    static constraints = {
        layoutSpot nullable: true
    }

    String toString()
    {
        "B/C/R: ${block}/${col}/${row}, FG/BG: ${FG}/${BG}, flag: ${flag}"
    }

    def beforeInsert(){
        signal = FG - BG
    }

    def beforeUpdate()
    {
        signal = FG - BG
    }
}
