/*
 * Copyright (C) 2014
 * Center for Excellence in Nanomedicine (NanoCAN)
 * Molecular Oncology
 * University of Southern Denmark
 * ###############################################
 * Written by:	Markus List
 * Contact: 	mlist'at'health'.'sdu'.'dk
 * Web:			http://www.nanocan.org/miracle/
 * ###########################################################################
 *	
 *	This file is part of MIRACLE.
 *
 *  MIRACLE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with this program. It can be found at the root of the project page.
 *	If not, see <http://www.gnu.org/licenses/>.
 *
 * ############################################################################
 */
package org.nanocan.rppa.scanner

class Antibody {

    String name
    String supplier
    String catalogNr
    Double concentration
    String concentrationUnit
    String comments
    //Double dilution
    //String species

    static searchable = true

    static constraints = {
        name()
        //dilution nullable: true
        //species inList: ["mouse", "rat", "goat", "rabbit"], nullable:true
        concentration nullable:true, min: new Double(0)
        concentrationUnit inList: ["microg/ml", "mM", "ratio 1:"], nullable:true
        comments nullable: true
        supplier nullable: true
        catalogNr nullable: true
    }

    String toString()
    {
       if(concentration != null && concentrationUnit!= null)
        (name + " " + concentration.toString() + " " + concentrationUnit)

       else
        name
    }
}
